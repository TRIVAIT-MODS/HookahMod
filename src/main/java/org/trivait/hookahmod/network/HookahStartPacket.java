package org.trivait.hookahmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.trivait.hookahmod.HookahMod;

public record HookahStartPacket(
        long smokingDurationMs,
        boolean isFirstTick,
        double standX, double standY, double standZ
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HookahStartPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(HookahMod.MOD_ID, "hookah_start"));

    public static final StreamCodec<FriendlyByteBuf, HookahStartPacket> CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeLong(pkt.smokingDurationMs());
                buf.writeBoolean(pkt.isFirstTick());
                buf.writeDouble(pkt.standX());
                buf.writeDouble(pkt.standY());
                buf.writeDouble(pkt.standZ());
            },
            buf -> new HookahStartPacket(
                    buf.readLong(),
                    buf.readBoolean(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
