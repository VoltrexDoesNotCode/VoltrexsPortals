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
        String iconsInfo = plugin.getConfig().getString("icons.info", "&#5FE2C5 ⓘ");
        String iconsError = plugin.getConfig().getString("icons.error", "&#FF0000 ❌");
        String iconsSuccess = plugin.getConfig().getString("icons.success", "&#00FF26 ✅");
        String prefix = plugin.getConfig().getString("prefix", "&#5FE2C5VoltrexsPortals ► ");

        if (args.length == 0) {
            sender.sendMessage(Utils.colorize(prefix + iconsInfo + " &a/portal wand &7- Give the selection wand"));
            sender.sendMessage(Utils.colorize(prefix + iconsInfo + " &a/portal create <name> &7- Create a portal with your selected positions"));
            sender.sendMessage(Utils.colorize(prefix + iconsInfo + " &a/portal delete <name> &7- Delete a portal"));
            sender.sendMessage(Utils.colorize(prefix + iconsInfo + " &a/portal reload &7- Reload config and portals"));
            sender.sendMessage(Utils.colorize(prefix + iconsInfo + " &7Discord: " + plugin.getConfig().getString("discord-invite", "https://discord.gg/5V9ngPj9ek")));
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("wand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You must be a player to get a wand."));
                return true;
            }
            if (!sender.hasPermission("portals.admin") && !sender.hasPermission("voltrexsportals.admin")) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You do not have permission to use this command."));
                return true;
            }
            Player p = (Player) sender;
            plugin.getWandListener().giveWandTo(p);
            sender.sendMessage(Utils.colorize(prefix + iconsSuccess + " Wand given."));
            return true;
        }

        if (sub.equals("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " Only players may create portals."));
                return true;
            }
            if (!sender.hasPermission("portals.admin") && !sender.hasPermission("voltrexsportals.admin")) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You do not have permission to use this command."));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " Usage: /portal create <name>"));
                return true;
            }
            Player p = (Player) sender;
            String name = args[1];
            Location pos1 = plugin.getWandListener().getPos1For(p.getUniqueId());
            Location pos2 = plugin.getWandListener().getPos2For(p.getUniqueId());
            if (pos1 == null || pos2 == null) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You must set both positions with the wand first (left and right click blocks)."));
                return true;
            }
            Portal portal = new Portal(name);
            portal.setPos1(pos1);
            portal.setPos2(pos2);
            // default destination world and coords — user can edit in portal file
            portal.setDestinationWorld(p.getWorld().getName());
            portal.setDestinationX(p.getLocation().getX());
            portal.setDestinationY(p.getLocation().getY());
            portal.setDestinationZ(p.getLocation().getZ());
            plugin.getPortalManager().savePortal(portal);
            sender.sendMessage(Utils.colorize(prefix + iconsSuccess + " Portal '" + name + "' created. Edit the portal's file to change destination or permissions."));
            return true;
        }

        if (sub.equals("delete")) {
            if (!sender.hasPermission("portals.admin") && !sender.hasPermission("voltrexsportals.admin")) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You do not have permission to use this command. (voltrexsportals.admin, portals.admin)"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " Usage: /portal delete <name>"));
                return true;
            }
            String name = args[1];
            boolean ok = plugin.getPortalManager().deletePortal(name);
            if (ok) {
                sender.sendMessage(Utils.colorize(prefix + iconsSuccess + " Portal '" + name + "' deleted."));
            } else {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " Portal '" + name + "' not found."));
            }
            return true;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission("portals.admin") && !sender.hasPermission("voltrexsportals.admin")) {
                sender.sendMessage(Utils.colorize(prefix + iconsError + " You do not have permission to use this command. (voltrexsportals.admin, portals.admin)"));
                return true;
            }
            plugin.reloadConfig();
            plugin.getPortalManager().loadAll();
            sender.sendMessage(Utils.colorize(prefix + iconsSuccess + " Configuration and portals successfully reloaded."));
            return true;
        }

        sender.sendMessage(Utils.colorize(prefix + iconsError + " Unknown subcommand. Use /portal for help."));
        return true;
    }
}
