package com.flycloud.minecraft.ironfist;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

@Mod(IronFist.MODID)
public class IronFist {

    public static final String MODID = "ironfist";
    public static final String PREFIX = MODID + ".";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronFist(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new BlockBreakEvent());
        NeoForge.EVENT_BUS.register(new IronFistCommand());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        PacketHandler.init();
    }

    @SubscribeEvent
    public void onPlayerEnter(PlayerEvent.PlayerLoggedInEvent event) {
        IronFistPlayer.get(event.getEntity()).sync();
    }
}
