package com.flycloud.minecraft.ironfist;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public abstract class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel instance = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(IronFist.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        IronFist.LOGGER.info("Registering network messages");
        instance.registerMessage(0, MessageFistSync.class, MessageFistSync::encode, MessageFistSync::decode, MessageFistSync::handle);
//        instance.registerMessage(1, MessageConfigSync.class, MessageConfigSync::encode, MessageConfigSync::decode, MessageConfigSync::handle);
    }

    public record MessageFistSync(int fistLV, float fistXP){
        public void encode(FriendlyByteBuf Buf) {
            Buf.writeInt(fistLV);
            Buf.writeFloat(fistXP);
        }

        public static MessageFistSync decode(FriendlyByteBuf Buf) {
            return new MessageFistSync(Buf.readInt(), Buf.readFloat());
        }

        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            contextSupplier.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientPacketHandler.handlePacketFist(this,contextSupplier));
            });
            contextSupplier.get().setPacketHandled(true);

        }

    }

    public record MessageConfigSync(boolean fistOnly, int maxLV, float XPMultiple){
        public void encode(FriendlyByteBuf Buf) {
            Buf.writeBoolean(fistOnly);
            Buf.writeInt(maxLV);
            Buf.writeFloat(XPMultiple);
        }
        public static MessageConfigSync decode(FriendlyByteBuf Buf) {
            return new MessageConfigSync(Buf.readBoolean(), Buf.readInt(), Buf.readFloat());
        }
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            contextSupplier.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientPacketHandler.handlePacketConfig(this,contextSupplier));
            });
            contextSupplier.get().setPacketHandled(true);
        }
    }

    public static class clientPacketHandler {
        public static void handlePacketFist(MessageFistSync msg, Supplier<NetworkEvent.Context> contextSupplier) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                IronFistPlayer IFPlayer = IronFistPlayer.get(player);
                IFPlayer.setFistLV(msg.fistLV);
                IFPlayer.setFistXP(msg.fistXP);
            }
        }

        public static void handlePacketConfig(MessageConfigSync msg, Supplier<NetworkEvent.Context> contextSupplier) {
            Player player = contextSupplier.get().getSender();
            if (player != null) {
                Config.fistOnly = msg.fistOnly;
                Config.maxLV = msg.maxLV;
                Config.XPMultiple = msg.XPMultiple;
            }
        }
    }
}