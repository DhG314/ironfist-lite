package com.flycloud.minecraft.ironfist;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkDirection;

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
        player.sendMessage(Component.nullToEmpty(msg),player.getUUID());
    }

    public void applyAllModifiers() {
        applyFistRangeModifiers();
        applyFistDamageModifier();
    }

    private void applyFistRangeModifiers() {
        AttributeInstance blockReach = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (blockReach != null) {
            blockReach.removeModifier(player.getUUID());
        }
        AttributeInstance entityReach = player.getAttribute(ForgeMod.ATTACK_RANGE.get());
        if (entityReach != null) {
            entityReach.removeModifier(player.getUUID());
        }
        if (Config.fistRange) {
            if (blockReach != null) {
                blockReach.addTransientModifier(new AttributeModifier(player.getUUID(), "Fist block range modifier", (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADDITION));
            }
            if (entityReach != null) {
                entityReach.addTransientModifier(new AttributeModifier(player.getUUID(), "Fist entity range modifier", (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private void applyFistDamageModifier() {
        AttributeInstance attackDamage = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(player.getUUID());
        }
        if (Config.fistDamage && attackDamage != null) {
            attackDamage.addTransientModifier(new AttributeModifier(player.getUUID(), "Fist damage modifier", (double) (fistLV - 1) / 2, AttributeModifier.Operation.ADDITION));
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
            PacketHandler.instance.sendTo(new PacketHandler.MessageFistSync(fistLV, fistXP),serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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
        player.sendMessage(Component.nullToEmpty(StringUtils.translate("fist.levelup")),player.getUUID());
        player.sendMessage(Component.nullToEmpty(StringUtils.translateWithFormat("fist.showdata", fistLV, fistXP, requiredXP)),player.getUUID());
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
