package com.flycloud.minecraft.ironfist;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = IronFist.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue FIST_ONLY = BUILDER.comment("Whether to only allow fists").define("fistOnly", false);
    private static final ForgeConfigSpec.IntValue MAX_LV = BUILDER.comment("The max level of fist").defineInRange("maxLV", 30, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue XPMULTIPLE = BUILDER.comment("The xp multiple").defineInRange("xpMultiple", 1.0, 0.1, Double.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue LIMIT_BREAK_SPEED = BUILDER.comment("The limit of break speed(0 to disable)").defineInRange("limitBreakSpeed", 10, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue FIST_DAMAGE = BUILDER.comment("Whether add fist damage").define("fistDamage", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean fistOnly = false;
    public static int maxLV = 30;
    public static float XPMultiple = 1.0f;
    public static int limitBreakSpeed = 10;
    public static boolean fistDamage = false;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        fistOnly = FIST_ONLY.get();
        maxLV = MAX_LV.get();
        XPMultiple = XPMULTIPLE.get().floatValue();
        limitBreakSpeed = LIMIT_BREAK_SPEED.get();
        fistDamage = FIST_DAMAGE.get();
        IronFist.LOGGER.info("IronFist config load");
    }
}
