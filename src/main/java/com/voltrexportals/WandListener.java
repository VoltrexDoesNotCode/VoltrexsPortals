package com.voltrexportals;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WandListener implements Listener {
    private final VoltrexsPortals plugin;
    // temporary per-player positions using the wand
    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    public WandListener(VoltrexsPortals plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if (!e.getItem().hasItemMeta()) return;
        var meta = e.getItem().getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().has(plugin.getWandKey(), PersistentDataType.BYTE)) return;

        Player p = e.getPlayer();
        Action a = e.getAction();
        if (a == Action.LEFT_CLICK_BLOCK) {
            var block = e.getClickedBlock();
            if (block == null) return;
            Location loc = block.getLocation();
            pos1.put(p.getUniqueId(), loc);
            p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.info", "&#F1FF00 ⓘ") + " " + plugin.getConfig().getString("prefix") + "Position 1 set: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()));
            e.setCancelled(true);
        } else if (a == Action.RIGHT_CLICK_BLOCK) {
            var block = e.getClickedBlock();
            if (block == null) return;
            Location loc = block.getLocation();
            pos2.put(p.getUniqueId(), loc);
            p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.info", "&#F1FF00 ⓘ") + " " + plugin.getConfig().getString("prefix") + "Position 2 set: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()));
            e.setCancelled(true);
        }
    }

    public void giveWandTo(Player p) {
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = axe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Utils.colorize(plugin.getConfig().getString("wand-item-name", "&eVoltrex Wand")));
            meta.getPersistentDataContainer().set(plugin.getWandKey(), PersistentDataType.BYTE, (byte) 1);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            axe.setItemMeta(meta);
        }
        p.getInventory().addItem(axe);
        p.sendMessage(Utils.colorize(plugin.getConfig().getString("icons.success", "&#00FF26 ✅") + " " + plugin.getConfig().getString("prefix") + "You were given the wand."));
    }

    public Location getPos1For(UUID player) { return pos1.get(player); }
    public Location getPos2For(UUID player) { return pos2.get(player); }

    public void setPos1For(UUID player, Location loc) { pos1.put(player, loc); }
    public void setPos2For(UUID player, Location loc) { pos2.put(player, loc); }
}
