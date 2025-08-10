package com.voltrexportals;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PortalManager {
    private final VoltrexsPortals plugin;
    private final Map<String, Portal> portals = new HashMap<>();
    private final File portalsFolder;

    public PortalManager(VoltrexsPortals plugin) {
        this.plugin = plugin;
        portalsFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("portals-folder", "portals"));
        if (!portalsFolder.exists()) portalsFolder.mkdirs();
    }

    public void loadAll() {
        portals.clear();
        File[] files = portalsFolder.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File f : files) {
            try {
                Portal p = Portal.load(f);
                portals.put(p.getName().toLowerCase(), p);
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed loading portal file: " + f.getName());
                ex.printStackTrace();
            }
        }
    }

    public void savePortal(Portal portal) {
        File f = new File(portalsFolder, portal.getName() + ".yml");
        portal.save(f);
        portals.put(portal.getName().toLowerCase(), portal);
    }

    public boolean deletePortal(String name) {
        String key = name.toLowerCase();
        Portal p = portals.remove(key);
        File f = new File(portalsFolder, name + ".yml");
        if (f.exists()) return f.delete();
        return p != null; // deleted from memory
    }

    public Portal getPortal(String name) {
        return portals.get(name.toLowerCase());
    }

    public Collection<Portal> getAll() {
        return portals.values();
    }
}
