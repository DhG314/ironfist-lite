package com.flycloud.minecraft.ironfist;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = IronFist.MODID, bus = EventBusSubscriber.Bus.MOD)
public abstract class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void init() {
        IronFist.LOGGER.info("Registering network messages");
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                MessageFistSync.TYPE,
                MessageFistSync.STREAM_CODEC,
                MessageFistSync::handle
        );
        registrar.playToServer(
                MessageConfigSync.TYPE,
                MessageConfigSync.STREAM_CODEC,
                MessageConfigSync::handle
        );
    }

    public record MessageFistSync(int fistLV, float fistXP) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageFistSync> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IronFist.MODID, "fist_sync"));

        public static final StreamCodec<FriendlyByteBuf, MessageFistSync> STREAM_CODEC = StreamCodec.of(
                (buf, msg) -> {
                    buf.writeInt(msg.fistLV);
                    buf.writeFloat(msg.fistXP);
                },
                buf -> new MessageFistSync(buf.readInt(), buf.readFloat())
        );

        @Override
        public CustomPacketPayload.Type<MessageFistSync> type() {
            return TYPE;
        }

        public void handle(final IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    IronFistPlayer IFPlayer = IronFistPlayer.get(player);
                    IFPlayer.setFistLV(fistLV);
                    IFPlayer.setFistXP(fistXP);
                }
            });
        }
    }

    public record MessageConfigSync(boolean fistOnly,
                                    int maxLV,
                                    float XPMultiple,
                                    int limitBreakSpeed,
                                    boolean fistDamage,
                                    boolean fistRange,
                                    boolean saveDataOnDeath) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageConfigSync> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IronFist.MODID, "config_sync"));

        public static final StreamCodec<FriendlyByteBuf, MessageConfigSync> STREAM_CODEC = StreamCodec.of(
                (buf, msg) -> {
                    buf.writeBoolean(msg.fistOnly);
                    buf.writeInt(msg.maxLV);
                    buf.writeFloat(msg.XPMultiple);
                    buf.writeInt(msg.limitBreakSpeed);
                    buf.writeBoolean(msg.fistDamage);
                    buf.writeBoolean(msg.fistRange);
                    buf.writeBoolean(msg.saveDataOnDeath);
                },
                buf -> new MessageConfigSync(
                        buf.readBoolean(),
                        buf.readInt(),
                        buf.readFloat(),
                        buf.readInt(),
                        buf.readBoolean(),
                        buf.readBoolean(),
                        buf.readBoolean()
                )
        );

        @Override
        public CustomPacketPayload.Type<MessageConfigSync> type() {
            return TYPE;
        }

        public void handle(final IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player != null) {
                    Config.fistOnly = fistOnly;
                    Config.maxLV = maxLV;
                    Config.XPMultiple = XPMultiple;
                    Config.limitBreakSpeed = limitBreakSpeed;
                    Config.fistDamage = fistDamage;
                    Config.fistRange = fistRange;
                    Config.saveDataOnDeath = saveDataOnDeath;
                }
            });
        }
    }
}
