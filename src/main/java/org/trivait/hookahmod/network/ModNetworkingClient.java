package org.trivait.hookahmod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.trivait.hookahmod.HookahModClient;
import org.trivait.hookahmod.ModSounds;
import org.trivait.hookahmod.particle.ParticleHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModNetworkingClient {

    private static final Map<UUID, InhaleData> inhaling = new HashMap<>();
    private static final Map<UUID, ExhaleData> exhaling = new HashMap<>();

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(HookahSmokePacket.TYPE, (payload, context) -> {
            if (HookahModClient.CONFIG == null || !HookahModClient.CONFIG.modEnabled) return;

            Minecraft client = Minecraft.getInstance();
            UUID uuid = payload.playerUuid();
            boolean isSelf = client.player != null && client.player.getUUID().equals(uuid);

            if (payload.smokingDurationMs() == -1L) {
                inhaling.put(uuid, new InhaleData(payload.standX(), payload.standY(), payload.standZ(), payload.effectColor()));

                if (!isSelf && payload.playSound() && HookahModClient.CONFIG.sounds) {
                    client.execute(() -> {
                        ClientLevel level = client.level;
                        if (level == null) return;
                        level.players().stream().filter(p -> p.getUUID().equals(uuid)).findFirst()
                                .ifPresentOrElse(
                                        p -> level.playLocalSound(p.getX(), p.getY(), p.getZ(), ModSounds.INHALE, SoundSource.PLAYERS, 1f, 1f, false),
                                        () -> level.playLocalSound(payload.standX(), payload.standY(), payload.standZ(), ModSounds.INHALE, SoundSource.PLAYERS, 1f, 1f, false)
                                );
                    });
                }
            } else {
                inhaling.remove(uuid);
                long endTime = System.currentTimeMillis() + payload.smokingDurationMs();
                exhaling.put(uuid, new ExhaleData(endTime, payload.standX(), payload.standY(), payload.standZ(), payload.effectColor()));

                if (!isSelf && payload.playSound() && HookahModClient.CONFIG.sounds) {
                    client.execute(() -> {
                        ClientLevel level = client.level;
                        if (level == null) return;
                        level.players().stream().filter(p -> p.getUUID().equals(uuid)).findFirst()
                                .ifPresentOrElse(
                                        p -> level.playLocalSound(p.getX(), p.getY(), p.getZ(), ModSounds.EXHALE, SoundSource.PLAYERS, 1f, 1f, false),
                                        () -> level.playLocalSound(payload.standX(), payload.standY(), payload.standZ(), ModSounds.EXHALE, SoundSource.PLAYERS, 1f, 1f, false)
                                );
                    });
                }
            }
        });
    }

    public static void tick() {
        if (HookahModClient.CONFIG == null || !HookahModClient.CONFIG.modEnabled) return;

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        UUID selfUuid = client.player != null ? client.player.getUUID() : null;
        boolean standParticles = HookahModClient.CONFIG.particlesAboveBrewingStand;
        long now = System.currentTimeMillis();

        if (standParticles) {
            for (Map.Entry<UUID, InhaleData> entry : inhaling.entrySet()) {
                if (entry.getKey().equals(selfUuid)) continue;
                InhaleData data = entry.getValue();
                if (now % 150 < 50) {
                    ParticleHelper.spawnSmoke(level, data.standX + 0.5, data.standY + 1.0, data.standZ + 0.5, 0, 0.3, 0, data.effectColor);
                }
            }
        }

        exhaling.entrySet().removeIf(entry -> {
            if (entry.getKey().equals(selfUuid)) return false;
            ExhaleData data = entry.getValue();
            if (now >= data.endTime) return true;

            AbstractClientPlayer player = level.players().stream()
                    .filter(p -> p.getUUID().equals(entry.getKey()))
                    .findFirst().orElse(null);

            if (player != null) {
                Vec3 eye = player.getEyePosition();
                Vec3 look = player.getViewVector(1f);
                ParticleHelper.spawnSmoke(level,
                        eye.x + look.x * 0.5, (eye.y + look.y * 0.5) - 0.3, eye.z + look.z * 0.5,
                        look.x * 0.1, look.y * 0.1, look.z * 0.1, data.effectColor);
                if (standParticles && now % 150 < 50) {
                    ParticleHelper.spawnSmoke(level, data.standX + 0.5, data.standY + 1.0, data.standZ + 0.5, 0, 0.3, 0, data.effectColor);
                }
            }
            return false;
        });
    }

    public static int getSelfEffectColor() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return -1;
        UUID self = client.player.getUUID();
        InhaleData inhale = inhaling.get(self);
        if (inhale != null) return inhale.effectColor;
        ExhaleData exhale = exhaling.get(self);
        if (exhale != null) return exhale.effectColor;
        return -1;
    }

    public static void removeSelfExhaleWhenDone() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        UUID self = client.player.getUUID();
        ExhaleData data = exhaling.get(self);
        if (data != null && System.currentTimeMillis() >= data.endTime) exhaling.remove(self);
    }

    private record InhaleData(double standX, double standY, double standZ, int effectColor) {}
    private record ExhaleData(long endTime, double standX, double standY, double standZ, int effectColor) {}
}
