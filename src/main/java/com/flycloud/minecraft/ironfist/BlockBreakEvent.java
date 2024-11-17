package com.flycloud.minecraft.ironfist;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

        if(Config.fistRange){
            int fistLV = IFPlayer.getFistLV();

            AttributeInstance blockReach = IFPlayer.getPlayer().getAttribute(ForgeMod.REACH_DISTANCE.get());
            AttributeModifier blockReachModifier = new AttributeModifier(IFPlayer.getPlayer().getUUID(), "Fist block range modifier", (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADDITION);
            if (blockReach != null && !blockReach.hasModifier(blockReachModifier)) {
                blockReach.addTransientModifier(blockReachModifier);
            }

            AttributeInstance entityReach = IFPlayer.getPlayer().getAttribute(ForgeMod.ATTACK_RANGE.get());
            AttributeModifier entityReachModifier = new AttributeModifier(IFPlayer.getPlayer().getUUID(), "Fist entity range modifier", (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADDITION);
            if (entityReach != null && !entityReach.hasModifier(entityReachModifier)) {
                entityReach.addTransientModifier(entityReachModifier);
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }
        int fistLV = IFPlayer.getFistLV();
        float hardness = event.getState().getDestroySpeed(event.getEntity().getLevel(), event.getPosition().get());
        hardness= Math.max(hardness, 0.1f);
        if(Config.limitBreakSpeed>0){
            event.setNewSpeed(Math.min(Math.min(Config.limitBreakSpeed, fistLV), hardness * 2/3 * Config.limitBreakSpeed));
            // same to these lines:
//            float newSpeed = hardness * 2/3 * Config.limitBreakSpeed;
//            if(Math.min(Config.limitBreakSpeed, fistLV) > newSpeed) {
//                event.setNewSpeed(newSpeed);
//            }else{
//                event.setNewSpeed(Math.min(Config.limitBreakSpeed, fistLV));
//            }
        }else {
            event.setNewSpeed(fistLV);
        }
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
    public void onPlayerAttackEntity(AttackEntityEvent event) {
        IronFistPlayer IFPlayer = getIFPlayer(event.getEntity());
        if (IFPlayer == null) {
            return;
        }
        int fistLV = IFPlayer.getFistLV();
        if(Config.fistDamage) {
            AttributeInstance attackDamage = event.getEntity().getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
            AttributeModifier attackDamageModifier = new AttributeModifier(event.getEntity().getUUID(), "Fist damage modifier", (double) (fistLV-1) /2, AttributeModifier.Operation.ADDITION);
            if (attackDamage != null && !attackDamage.hasModifier(attackDamageModifier)) {
                attackDamage.addTransientModifier(attackDamageModifier);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerEvent.Clone event) {
        if(!event.isWasDeath()){
            return;
        }
        IronFistPlayer oldPlayer = getIFPlayer(event.getOriginal());
        IronFistPlayer newPlayer = getIFPlayer(event.getEntity());
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
                        (player.getMainHandItem().hasTag() &&
                                (player.getMainHandItem().getTags().anyMatch(
                                        tag -> "minecraft:tools".equals(tag.location().toString()) || "forge:tools".equals(tag.location().toString()))
                                )))) {
            return null;
        }
        return IronFistPlayer.get(player);
    }
}
