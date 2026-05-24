package org.trivait.hookahPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class HookahMessageListener implements PluginMessageListener {

    private static final String CHANNEL_C2S = "hookahmod:hookah_start";
    private static final String CHANNEL_S2C = "hookahmod:hookah_smoke";
    private static final double BROADCAST_RADIUS = 64.0;

    private final HookahPlugin plugin;
    private final Logger log;

    public HookahMessageListener(HookahPlugin plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player sender, byte[] message) {
        if (!channel.equals(CHANNEL_C2S) || !HookahServerState.networkingEnabled) return;

        PacketHelper.StartPacketData data;
        try {
            data = PacketHelper.readStartPacket(message);
        } catch (IOException e) {
            log.warning("Failed to read HookahStartPacket from " + sender.getName() + ": " + e.getMessage());
            return;
        }

        // onPluginMessageReceived runs off the main thread on Spigot.
        // Block/world access must happen on the main thread to avoid crashes
        // that break GUI opening for other players.
        UUID senderUuid = sender.getUniqueId();
        long smokingDurationMs = data.smokingDurationMs();
        boolean isFirstTick = data.isFirstTick();
        double sx = data.standX(), sy = data.standY(), sz = data.standZ();

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(senderUuid);
            if (player == null) return;

            Location standLoc = new Location(player.getWorld(), sx, sy, sz);

            int effectColor = BrewingEffectHelper.NO_COLOR;
            Optional<PotionEffect> effect = BrewingEffectHelper.getEffect(standLoc);

            if (effect.isPresent()) {
                effectColor = BrewingEffectHelper.getEffectColor(effect.get());
                if (HookahServerState.effectsEnabled && smokingDurationMs == 0) {
                    player.addPotionEffect(effect.get());
                }
            }

            long broadcastDuration = smokingDurationMs == 0 ? -1L : smokingDurationMs;
            boolean playSound = isFirstTick || smokingDurationMs > 0;

            byte[] smokePacket;
            try {
                smokePacket = PacketHelper.writeSmokePacket(
                        senderUuid, sx, sy, sz, broadcastDuration, playSound, effectColor
                );
            } catch (IOException e) {
                log.warning("Failed to write HookahSmokePacket: " + e.getMessage());
                return;
            }

            for (Player other : player.getWorld().getPlayers()) {
                if (other.getLocation().distance(player.getLocation()) <= BROADCAST_RADIUS
                        && other.getListeningPluginChannels().contains(CHANNEL_S2C)) {
                    other.sendPluginMessage(plugin, CHANNEL_S2C, smokePacket);
                }
            }
        });
    }
}
