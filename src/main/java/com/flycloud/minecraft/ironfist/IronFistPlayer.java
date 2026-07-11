package com.flycloud.minecraft.ironfist;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.PacketDistributor;

public class IronFistPlayer {
    public final static String NAME = "IronFistPlayer";
    private final Player player;
    private final CompoundTag nbt;

    int fistLV = 1;
    float fistXP = 0;
    float requiredXP = getLevelUpXP(fistLV);

    public IronFistPlayer(Player player) {
        this.player = player;
        this.nbt = player.getPersistentData();
        if (!nbt.contains(NAME + ".fistLV")) {
            save();
        }
        load();
    }
    public static IronFistPlayer get(Player player) {
        return new IronFistPlayer(player);
    }
    public Player getPlayer() {
        return player;
    }

    public void say(String msg) {
        player.sendSystemMessage(Component.nullToEmpty(msg));
    }

    private boolean shouldApplyFistModifiers() {
        return !(player.getMainHandItem().getItem() != Items.AIR &&
                (Config.fistOnly ||
                        (player.getMainHandItem().getTags().anyMatch(
                                tag -> "minecraft:tools".equals(tag.location().toString()) || "c:tools".equals(tag.location().toString()) || "forge:tools".equals(tag.location().toString()))
                        )));
    }

    public void applyAllModifiers() {
        applyFistRangeModifiers();
        applyFistDamageModifier();
    }

    private void applyFistRangeModifiers() {
//        AttributeInstance blockReach = player.getAttribute(NeoForgeMod.BLOCK_REACH.get());
        AttributeInstance blockReach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockReach != null) {
            blockReach.removeModifier(ResourceLocation.parse("ironfist:block_range"));
        }
//        AttributeInstance entityReach = player.getAttribute(NeoForgeMod.ENTITY_REACH.get());
        AttributeInstance entityReach = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityReach != null) {
            entityReach.removeModifier(ResourceLocation.parse("ironfist:entity_range"));
        }
        if (Config.fistRange && shouldApplyFistModifiers()) {
            if (blockReach != null) {
                blockReach.addTransientModifier(new AttributeModifier(ResourceLocation.parse("ironfist:block_range"), (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADD_VALUE));
            }
            if (entityReach != null) {
                entityReach.addTransientModifier(new AttributeModifier(ResourceLocation.parse("ironfist:entity_range"), (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private void applyFistDamageModifier() {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ResourceLocation.parse("ironfist:attack_damage"));
        }
        if (Config.fistDamage && shouldApplyFistModifiers() && attackDamage != null) {
            attackDamage.addTransientModifier(new AttributeModifier(ResourceLocation.parse("ironfist:attack_damage"), (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public void save() {
        updateRequiredXP();
        nbt.putInt(NAME + ".fistLV", fistLV);
        nbt.putFloat(NAME + ".fistXP", fistXP);
    }

    public void load() {
        fistLV = nbt.getInt(NAME + ".fistLV");
        fistXP = nbt.getFloat(NAME + ".fistXP");
        updateRequiredXP();
        sync();
        applyAllModifiers();
    }

    public void sync(){
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new PacketHandler.MessageFistSync(fistLV, fistXP));
        }
    }

    public static float getLevelUpXP(int level) {
        if (level < 1) {
            return 0;
        }
        return (float) (200*Math.pow(1.5,level)-200);
    }

    public void checkLevelUp() {
        updateRequiredXP();
        if (fistXP >= requiredXP && fistLV < Config.maxLV) {
            levelUp();
        }
    }

    public void levelUp() {
        fistLV++;
        updateRequiredXP();
        applyAllModifiers();
        player.sendSystemMessage(Component.nullToEmpty(StringUtils.translate("fist.levelup")));
        player.sendSystemMessage(Component.nullToEmpty(StringUtils.translateWithFormat("fist.showdata", fistLV, fistXP, requiredXP)));
    }

    public float getFistXP() {
        return fistXP;
    }

    public void setFistXP(float fistXP) {
        this.fistXP = fistXP;
        save();
    }
    public void addFistXP(float fistXP) {
        this.fistXP += fistXP;
        checkLevelUp();
        save();
    }

    public int getFistLV() {
        return fistLV;
    }

    public void setFistLV(int fistLV) {
        this.fistLV = fistLV;
        save();
        applyAllModifiers();
    }

    public float getRequiredXP() {
        return requiredXP;
    }

    public void updateRequiredXP(){
        requiredXP = getLevelUpXP(fistLV);
    }
}
