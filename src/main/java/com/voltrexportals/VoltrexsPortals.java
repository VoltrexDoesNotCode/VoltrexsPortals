package com.voltrexportals;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VoltrexsPortals extends JavaPlugin {
    private PortalManager portalManager;
    private NamespacedKey wandKey;
    private LuckPerms luckPermsApi; // may be null if not installed

    public NamespacedKey getWandKey() {
        return wandKey;
    }

    public PortalManager getPortalManager() {
        return portalManager;
    }

    public LuckPerms getLuckPermsApi() {
        return luckPermsApi;
    }

    @Override
    public void onEnable() {
        // ensure plugin folder exists (this will be plugins/VoltrexsPortals)
        saveDefaultConfig();
        File data = getDataFolder();
        if (!data.exists()) data.mkdirs();

        // create portals folder
        File portals = new File(data, getConfig().getString("portals-folder", "portals"));
        if (!portals.exists()) portals.mkdirs();

        wandKey = new NamespacedKey(this, "voltrex_wand");

        // try to load LuckPerms if present (soft-dependency)
        if (getConfig().getBoolean("use-luckperms", true) && Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            var reg = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (reg != null) luckPermsApi = reg.getProvider();
        }

        // init managers and listeners
        portalManager = new PortalManager(this);
        portalManager.loadAll();

        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);

        var cmd = getCommand("portal");
        if (cmd != null) cmd.setExecutor(new PortalCommand(this));

        getLogger().info("Voltrex's Portals enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Voltrex's Portals disabled");
    }
}
