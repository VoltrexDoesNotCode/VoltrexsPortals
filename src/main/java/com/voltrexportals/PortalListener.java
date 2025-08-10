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
        // only check when the player changed block coordinates
        if (e.getFrom().getBlockX() == e.getTo().getBlockX()
                && e.getFrom().getBlockY() == e.getTo().getBlockY()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        long now = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("cooldown-ms", 2000);
        Long last = lastTeleport.get(p.getUniqueId());
        if (last != null && now - last < cooldown) return;

        for (Portal portal : plugin.getPortalManager().getAll()) {
            Location a = portal.getPos1();
            Location b = portal.getPos2();
            if (a == null || b == null) continue;
            // portal only works if the player is in the same world where the portal region is defined
            if (!p.getWorld().getName().equals(a.getWorld().getName())) continue;

            double minX = Math.min(a.getX(), b.getX());
            double maxX = Math.max(a.getX(), b.getX()) + 1; // include block
            double minY = Math.min(a.getY(), b.getY());
            double maxY = Math.max(a.getY(), b.getY()) + 1;
            double minZ = Math.min(a.getZ(), b.getZ());
            double maxZ = Math.max(a.getZ(), b.getZ()) + 1;

            Location loc = p.getLocation();
            if (loc.getX() >= minX && loc.getX() <= maxX
                    && loc.getY() >= minY && loc.getY() <= maxY
                    && loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                // check blacklists (LuckPerms primary group if LP present)
                var lp = plugin.getLuckPermsApi();
                if (lp != null && portal.getBlacklistedGroups() != null && !portal.getBlacklistedGroups().isEmpty()) {
                    var user = lp.getUserManager().getUser(p.getUniqueId());
                    if (user != null) {
                        String primary = user.getPrimaryGroup();
                        if (portal.getBlacklistedGroups().stream().anyMatch(g -> g.equalsIgnoreCase(primary))) {
                            p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.error", "&#FF0000 ❌") + " " + plugin.getConfig().getString("prefix") + "You are not allowed to use this portal."));
                            return;
                        }
                    }
                }

                // destination world
                String dest = portal.getDestinationWorld();
                if (dest == null || dest.isEmpty()) {
                    p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.error", "&#FF0000 ❌") + " " + plugin.getConfig().getString("prefix") + "Portal destination not configured."));
                    return;
                }
                var world = Bukkit.getWorld(dest);
                if (world == null) {
                    p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.error", "&#FF0000 ❌") + " " + plugin.getConfig().getString("prefix") + "Destination world '" + dest + "' not found."));
                    return;
                }

                p.teleport(world.getSpawnLocation());
                if (plugin.getConfig().getBoolean("sound-on-teleport", true)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                }
                lastTeleport.put(p.getUniqueId(), now);
                p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.success", "&#00FF26 ✅") + " " + plugin.getConfig().getString("prefix") + "Teleported to " + dest));
                return; // stop checking other portals for this movement
            }
        }
    }
}
