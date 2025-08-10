package com.voltrexportals;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalListener implements Listener {
    private final VoltrexsPortals plugin;
    private final Map<UUID, Long> lastTeleport = new HashMap<>();

    public PortalListener(VoltrexsPortals plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        // Check move across block boundaries
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
            e.getFrom().getBlockY() == e.getTo().getBlockY() &&
            e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        // Permission check for using portals
        if (!p.hasPermission("portals.use") && !p.hasPermission("voltrexsportals.use")) return;

        long now = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("cooldown-ms", 2000);
        long last = lastTeleport.getOrDefault(p.getUniqueId(), 0L);
        if (now - last < cooldown) return;

        for (Portal portal : plugin.getPortalManager().getAll()) {
            Location a = portal.getPos1(), b = portal.getPos2();
            if (a == null || b == null) continue;
            if (!p.getWorld().getName().equals(a.getWorld().getName())) continue;

            double minX = Math.min(a.getX(), b.getX()), maxX = Math.max(a.getX(), b.getX()) + 1;
            double minY = Math.min(a.getY(), b.getY()), maxY = Math.max(a.getY(), b.getY()) + 1;
            double minZ = Math.min(a.getZ(), b.getZ()), maxZ = Math.max(a.getZ(), b.getZ()) + 1;
            Location loc = p.getLocation();
            
            if (loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                
                // LuckPerms blacklist check
                var lp = plugin.getLuckPermsApi();
                if (lp != null && portal.getBlacklistedGroups() != null && !portal.getBlacklistedGroups().isEmpty()) {
                    var user = lp.getUserManager().getUser(p.getUniqueId());
                    if (user != null && portal.getBlacklistedGroups().stream()
                        .anyMatch(g -> g.equalsIgnoreCase(user.getPrimaryGroup()))) {
                        p.sendMessage(Utils.colorize(plugin.getConfig().getString("prefix") +
                            plugin.getConfig().getString("icons.error") +
                            " You are not allowed to use this portal."));
                        return;
                    }
                }

                // Teleport destination logic
                String worldName = portal.getDestinationWorld();
                double x = portal.getDestinationX(), y = portal.getDestinationY(), z = portal.getDestinationZ();
                var destWorld = Bukkit.getWorld(worldName);
                if (worldName == null || worldName.isEmpty() || destWorld == null) {
                    p.sendMessage(Utils.colorize(plugin.getConfig().getString("prefix") +
                        plugin.getConfig().getString("icons.error") +
                        " Destination not configured."));
                    return;
                }

                p.teleport(new Location(destWorld, x, y, z));
                if (plugin.getConfig().getBoolean("sound-on-teleport", true)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                }

                lastTeleport.put(p.getUniqueId(), now);
                p.sendMessage(Utils.colorize(plugin.getConfig().getString("prefix") +
                    plugin.getConfig().getString("icons.success") +
                    " Teleported to " + worldName));
                return;
            }
        }
    }
}
