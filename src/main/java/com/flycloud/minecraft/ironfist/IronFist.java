package com.flycloud.minecraft.ironfist;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IronFist.MODID)
@Mod.EventBusSubscriber(modid = IronFist.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IronFist {

    public static final String MODID = "ironfist";
    public static final String PREFIX = MODID + ".";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronFist() {
        //noinspection removal
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockBreakEvent());
        MinecraftForge.EVENT_BUS.register(new IronFistCommand());
        //noinspection removal
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        PacketHandler.init();
    }

    @SubscribeEvent
    public void onPlayerEnter(PlayerEvent.PlayerLoggedInEvent event) {
        IronFistPlayer.get(event.getEntity()).sync();
    }
}
