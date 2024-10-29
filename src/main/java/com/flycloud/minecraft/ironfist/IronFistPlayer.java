package com.flycloud.minecraft.ironfist;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        player.sendSystemMessage(Component.nullToEmpty(msg));
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
    }

    public float getRequiredXP() {
        return requiredXP;
    }

    public void updateRequiredXP(){
        requiredXP = getLevelUpXP(fistLV);
    }
}
