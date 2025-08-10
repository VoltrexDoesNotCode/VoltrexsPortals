package com.voltrexportals;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Portal {
    private final String name;
    private Location pos1;
    private Location pos2;
    private String destinationWorld; // world name
    private List<String> blacklistedGroups = new ArrayList<>();

    public Portal(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public String getDestinationWorld() { return destinationWorld; }
    public void setDestinationWorld(String destinationWorld) { this.destinationWorld = destinationWorld; }
    public List<String> getBlacklistedGroups() { return blacklistedGroups; }
    public void setBlacklistedGroups(List<String> blacklistedGroups) { this.blacklistedGroups = blacklistedGroups; }

    public void save(File file) {
        var cfg = new YamlConfiguration();
        cfg.set("name", name);
        if (pos1 != null) {
            cfg.set("pos1.world", pos1.getWorld().getName());
            cfg.set("pos1.x", pos1.getX());
            cfg.set("pos1.y", pos1.getY());
            cfg.set("pos1.z", pos1.getZ());
        }
        if (pos2 != null) {
            cfg.set("pos2.world", pos2.getWorld().getName());
            cfg.set("pos2.x", pos2.getX());
            cfg.set("pos2.y", pos2.getY());
            cfg.set("pos2.z", pos2.getZ());
        }
        cfg.set("destinationWorld", destinationWorld);
        cfg.set("blacklistedGroups", blacklistedGroups);
        try {
            cfg.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Portal load(File file) {
        var cfg = YamlConfiguration.loadConfiguration(file);
        String name = cfg.getString("name", file.getName().replaceFirst("\\\\.yml$",""));
        Portal p = new Portal(name);
        if (cfg.contains("pos1.world")) {
            var w = cfg.getString("pos1.world");
            double x = cfg.getDouble("pos1.x");
            double y = cfg.getDouble("pos1.y");
            double z = cfg.getDouble("pos1.z");
            var world = org.bukkit.Bukkit.getWorld(w);
            if (world != null) p.setPos1(new Location(world, x, y, z));
        }
        if (cfg.contains("pos2.world")) {
            var w = cfg.getString("pos2.world");
            double x = cfg.getDouble("pos2.x");
            double y = cfg.getDouble("pos2.y");
            double z = cfg.getDouble("pos2.z");
            var world = org.bukkit.Bukkit.getWorld(w);
            if (world != null) p.setPos2(new Location(world, x, y, z));
        }
        p.setDestinationWorld(cfg.getString("destinationWorld", null));
        p.setBlacklistedGroups(cfg.getStringList("blacklistedGroups"));
        return p;
    }
}
