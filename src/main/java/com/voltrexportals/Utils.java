package com.voltrexportals;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String input) {
        if (input == null) return "";
        // replace hex tokens like &#9F00FF with ChatColor.of("#9F00FF") string
        Matcher m = 
    }
}
