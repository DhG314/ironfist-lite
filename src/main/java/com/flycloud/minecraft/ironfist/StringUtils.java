package com.flycloud.minecraft.ironfist;

import net.minecraft.client.resources.language.I18n;

public class StringUtils {
    public static String translate(String toLocalized) {
        return translate(toLocalized, true);
    }

    public static String translate(String toLocalized, boolean prefix) {
        String key = prefix ? IronFist.PREFIX + toLocalized : toLocalized;
        return I18n.get(key);
    }

    public static String translateWithFormat(String toLocalized, Object... args) {
        return translateWithFormat(toLocalized, true, args);
    }

    public static String translateWithFormat(String toLocalized, boolean prefix, Object... args) {
        String key = prefix ? IronFist.PREFIX + toLocalized : toLocalized;
        return I18n.get(key, args);
    }
}
