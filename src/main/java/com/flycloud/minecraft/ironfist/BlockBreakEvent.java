package com.flycloud.minecraft.ironfist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IronFist.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockBreakEvent {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        float obtainedXP;
        IronFistPlayer IFPlayer = getIFPlayer(event.getPlayer());
        if (IFPlayer == null) {
            return;
        }
        float hardness = event.getState().getDestroySpeed(event.getLevel(), event.getPos());
        if(hardness > 0){
            obtainedXP = hardness * Config.XPMultiple;
        }else {
            obtainedXP = 0;
        }
        IFPlayer.addFistXP(obtainedXP);
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }
        int fistLV = IFPlayer.getFistLV();
        event.setNewSpeed(fistLV);
    }

    @SubscribeEvent
    public void canHarvestBlock(PlayerEvent.HarvestCheck event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }

        event.setCanHarvest(true);
    }


    private IronFistPlayer getIFPlayer(Player player) {
        if (player == null || player instanceof FakePlayer) {
            return null;
        }
        if (player.getMainHandItem().getItem() != Items.AIR &&
                (Config.fistOnly ||
                        (player.getMainHandItem().hasTag() &&
                                (player.getMainHandItem().getTags().anyMatch(
                                        tag -> "minecraft:tools".equals(tag.location().toString()) || "forge:tools".equals(tag.location().toString()))
                                )))) {
            return null;
        }
        return IronFistPlayer.get(player);
    }
}
