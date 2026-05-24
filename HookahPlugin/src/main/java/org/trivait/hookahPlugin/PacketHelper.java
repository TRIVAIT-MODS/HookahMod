package org.trivait.hookahPlugin;

import java.io.*;
import java.util.UUID;

public class PacketHelper {

    public static StartPacketData readStartPacket(byte[] data) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        long duration = in.readLong();
        boolean firstTick = in.readBoolean();
        double sx = in.readDouble();
        double sy = in.readDouble();
        double sz = in.readDouble();
        return new StartPacketData(duration, firstTick, sx, sy, sz);
    }

    public static byte[] writeSmokePacket(UUID playerUuid,
                                           double standX, double standY, double standZ,
                                           long smokingDurationMs,
                                           boolean playSound,
                                           int effectColor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeLong(playerUuid.getMostSignificantBits());
        out.writeLong(playerUuid.getLeastSignificantBits());
        out.writeDouble(standX);
        out.writeDouble(standY);
        out.writeDouble(standZ);
        out.writeLong(smokingDurationMs);
        out.writeBoolean(playSound);
        out.writeInt(effectColor);
        return baos.toByteArray();
    }

    public record StartPacketData(long smokingDurationMs, boolean isFirstTick, double standX, double standY, double standZ) {}
}
