package com.voltrexportals;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalCommand implements CommandExecutor {
    private final VoltrexsPortals plugin;

    public PortalCommand(VoltrexsPortals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String iconsInfo = plugin.getConfig().getString("icons.info", "&#F1FF00 ⓘ");
        String iconsError = plugin.getConfig().getString("icons.error", "&#FF0000 ❌");
        String iconsSuccess = plugin.getConfig().getString("icons.success", "&#00FF26 ✅");
        String prefix = plugin.getConfig().getString("prefix", "&#9F00FFVoltrexsPortals ► ");

        if (args.length == 0) {
            sender.sendMessage(Utils.colorize(iconsInfo + " " + prefix + "&a/portal wand &7- Give the selection wand"));
            sender.sendMessage(Utils.colorize(iconsInfo + " " + prefix + "&a/portal create <name> &7- Create a portal with your selected positions"));
            sender.sendMessage(Utils.colorize(iconsInfo + " " + prefix + "&a/portal delete <name> &7- Delete a portal"));
            sender.sendMessage(Utils.colorize(iconsInfo + " " + prefix + "&a/portal reload &7- Reload config and portals"));
            sender.sendMessage(Utils.colorize(iconsInfo + " " + prefix + "&7Discord: " + plugin.getConfig().getString("discord-invite", "https://discord.gg/yourinvite")));
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("wand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "You must be a player to get a wand."));
                return true;
            }
            Player p = (Player) sender;
            plugin.getWandListener().giveWandTo(p);
            return true;
        }

        if (sub.equals("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "Only players may create portals."));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "Usage: /portal create <name>"));
                return true;
            }
            Player p = (Player) sender;
            String name = args[1];
            Location pos1 = plugin.getWandListener().getPos1For(p.getUniqueId());
            Location pos2 = plugin.getWandListener().getPos2For(p.getUniqueId());
            if (pos1 == null || pos2 == null) {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "You must set both positions with the wand first (left and right click blocks)."));
                return true;
            }
            Portal portal = new Portal(name);
            portal.setPos1(pos1);
            portal.setPos2(pos2);
            // default destination world to the player's current world — user can edit the portal file later
            portal.setDestinationWorld(p.getWorld().getName());
            plugin.getPortalManager().savePortal(portal);
            sender.sendMessage(Utils.colorize(iconsSuccess + " " + prefix + "Portal '" + name + "' created. Edit plugins/" + plugin.getDataFolder().getName() + "/" + plugin.getConfig().getString("portals-folder") + "/" + name + ".yml to change destination or blacklist groups."));
            return true;
        }

        if (sub.equals("delete")) {
            if (args.length < 2) {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "Usage: /portal delete <name>"));
                return true;
            }
            String name = args[1];
            boolean ok = plugin.getPortalManager().deletePortal(name);
            if (ok) {
                sender.sendMessage(Utils.colorize(iconsSuccess + " " + prefix + "Portal '" + name + "' deleted."));
            } else {
                sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "Portal '" + name + "' not found."));
            }
            return true;
        }

        if (sub.equals("reload")) {
            plugin.reloadConfig();
            plugin.getPortalManager().loadAll();
            sender.sendMessage(Utils.colorize(iconsSuccess + " " + prefix + "Configuration and portals reloaded."));
            return true;
        }

        sender.sendMessage(Utils.colorize(iconsError + " " + prefix + "Unknown subcommand. Use /portal for help."));
        return true;
    }
}
