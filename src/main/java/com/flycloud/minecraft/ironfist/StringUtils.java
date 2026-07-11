package com.flycloud.minecraft.ironfist;

import net.minecraft.network.chat.Component;

public class StringUtils {
    public static String translate(String toLocalized) {
        return translate(toLocalized, true);
    }

    public static String translate(String toLocalized, boolean prefix) {
        String key = prefix ? IronFist.PREFIX + toLocalized : toLocalized;
        return Component.translatable(key).getString();
    }

    public static String translateWithFormat(String toLocalized, Object... args) {
        return translateWithFormat(toLocalized, true, args);
    }

    public static String translateWithFormat(String toLocalized, boolean prefix, Object... args) {
        String key = prefix ? IronFist.PREFIX + toLocalized : toLocalized;
        return Component.translatable(key, args).getString();
    }
}
