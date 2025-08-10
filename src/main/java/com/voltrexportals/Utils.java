package com.voltrexportals;

import org.bukkit.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String input) {
        if (input == null) return "";

        // Replace hex tokens like &#9F00FF with ChatColor equivalents
        Matcher m = HEX.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            String hexColor = m.group(1);
            m.appendReplacement(buffer, ChatColor.of("#" + hexColor).toString());
        }
        m.appendTail(buffer);

        // Translate & codes to Bukkit colors
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
