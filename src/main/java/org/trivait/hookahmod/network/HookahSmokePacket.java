package org.trivait.hookahmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.trivait.hookahmod.HookahMod;

import java.util.UUID;

public record HookahSmokePacket(
        UUID playerUuid,
        double standX, double standY, double standZ,
        long smokingDurationMs,
        boolean playSound,
        int effectColor
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HookahSmokePacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(HookahMod.MOD_ID, "hookah_smoke"));

    public static final StreamCodec<FriendlyByteBuf, HookahSmokePacket> CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeUUID(pkt.playerUuid());
                buf.writeDouble(pkt.standX());
                buf.writeDouble(pkt.standY());
                buf.writeDouble(pkt.standZ());
                buf.writeLong(pkt.smokingDurationMs());
                buf.writeBoolean(pkt.playSound());
                buf.writeInt(pkt.effectColor());
            },
            buf -> new HookahSmokePacket(
                    buf.readUUID(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readLong(),
                    buf.readBoolean(),
                    buf.readInt()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
