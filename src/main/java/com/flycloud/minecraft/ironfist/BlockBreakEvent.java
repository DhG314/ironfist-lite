package com.flycloud.minecraft.ironfist;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

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
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND && event.getEntity() instanceof Player player) {
            IronFistPlayer.get(player).applyAllModifiers();
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }
        int fistLV = IFPlayer.getFistLV();
        float speed = fistLV * event.getOriginalSpeed();
        if(Config.limitBreakSpeed > 0){
            float hardness = Math.max(event.getState().getDestroySpeed(event.getEntity().level(), event.getPosition().get()), 0.1f);
            speed = Math.min(speed, hardness * 2/3 * Config.limitBreakSpeed);
        }
        event.setNewSpeed(speed);
    }

    @SubscribeEvent
    public void canHarvestBlock(PlayerEvent.HarvestCheck event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }

        event.setCanHarvest(true);
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerEvent.Clone event) {
        if(!event.isWasDeath()){
            return;
        }
        IronFistPlayer oldPlayer = IronFistPlayer.get(event.getOriginal());
        IronFistPlayer newPlayer = IronFistPlayer.get(event.getEntity());
        if(oldPlayer == null || newPlayer == null){
            return;
        }
        if(Config.saveDataOnDeath) {
            newPlayer.setFistLV(oldPlayer.getFistLV());
            newPlayer.setFistXP(oldPlayer.getFistXP());
        }
    }


    private IronFistPlayer getIFPlayer(Player player) {
        if (player == null || player instanceof FakePlayer) {
            return null;
        }
        if (player.getMainHandItem().getItem() != Items.AIR &&
                (Config.fistOnly ||
                        (player.getMainHandItem().getTags().anyMatch(
                                tag -> "minecraft:tools".equals(tag.location().toString()) ||
                                       "c:tools".equals(tag.location().toString()) ||
                                       "forge:tools".equals(tag.location().toString()))
                        ))) {
            return null;
        }
        return IronFistPlayer.get(player);
    }
}
