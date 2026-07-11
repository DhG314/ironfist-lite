package com.flycloud.minecraft.ironfist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = IronFist.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue FIST_ONLY = BUILDER.comment("Whether to only allow fists").define("fistOnly", false);
    private static final ModConfigSpec.IntValue MAX_LV = BUILDER.comment("The max level of fist").defineInRange("maxLV", 30, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue XPMULTIPLE = BUILDER.comment("The xp multiple").defineInRange("xpMultiple", 1.0, 0.1, Float.MAX_VALUE);
    private static final ModConfigSpec.IntValue LIMIT_BREAK_SPEED = BUILDER.comment("The limit of break speed(0 to disable)").defineInRange("limitBreakSpeed", 10, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue FIST_DAMAGE = BUILDER.comment("Whether add fist damage").define("fistDamage", false);
    private static final ModConfigSpec.BooleanValue FIST_RANGE = BUILDER.comment("Whether add fist range").define("fistRange", false);
    private static final ModConfigSpec.BooleanValue SAVE_DATA_ON_DEATH = BUILDER.comment("Whether save data on death").define("saveDataOnDeath", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean fistOnly = false;
    public static int maxLV = 30;
    public static float XPMultiple = 1.0f;
    public static int limitBreakSpeed = 10;
    public static boolean fistDamage = false;
    public static boolean fistRange = false;
    public static boolean saveDataOnDeath = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        fistOnly = FIST_ONLY.get();
        maxLV = MAX_LV.get();
        XPMultiple = XPMULTIPLE.get().floatValue();
        limitBreakSpeed = LIMIT_BREAK_SPEED.get();
        fistDamage = FIST_DAMAGE.get();
        fistRange = FIST_RANGE.get();
        saveDataOnDeath = SAVE_DATA_ON_DEATH.get();
        IronFist.LOGGER.info("IronFist config load");
    }
    static void save(){
        FIST_ONLY.set(fistOnly);
        MAX_LV.set(maxLV);
        XPMULTIPLE.set((double) XPMultiple);
        LIMIT_BREAK_SPEED.set(limitBreakSpeed);
        FIST_DAMAGE.set(fistDamage);
        FIST_RANGE.set(fistRange);
        SAVE_DATA_ON_DEATH.set(saveDataOnDeath);
        SPEC.save();
        IronFist.LOGGER.info("IronFist config save");
    }
}
