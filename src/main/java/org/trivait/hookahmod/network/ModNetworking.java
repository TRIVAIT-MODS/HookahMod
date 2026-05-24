package org.trivait.hookahmod.network;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.trivait.hookahmod.server.BrewingEffectHelper;
import org.trivait.hookahmod.server.HookahCommand;
import org.trivait.hookahmod.server.HookahServerState;

import java.util.Optional;

public class ModNetworking {

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(HookahStartPacket.TYPE, HookahStartPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HookahSmokePacket.TYPE, HookahSmokePacket.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                HookahCommand.register(dispatcher)
        );

        ServerPlayNetworking.registerGlobalReceiver(HookahStartPacket.TYPE, (payload, context) -> {
            if (!HookahServerState.networkingEnabled) return;

            ServerPlayer sender = context.player();
            ServerLevel level = (ServerLevel) sender.level();

            BlockPos standPos = BlockPos.containing(payload.standX(), payload.standY(), payload.standZ());

            int effectColor = BrewingEffectHelper.NO_COLOR;
            Optional<MobEffectInstance> effect = BrewingEffectHelper.getEffect(level, standPos);

            if (effect.isPresent()) {
                effectColor = BrewingEffectHelper.getEffectColor(effect.get());
                if (HookahServerState.effectsEnabled && payload.smokingDurationMs() == 0) {
                    MobEffectInstance orig = effect.get();
                    sender.addEffect(new MobEffectInstance(orig.getEffect(), orig.getAmplifier(), 40, false, true, true));
                }
            }

            long broadcastDuration = payload.smokingDurationMs() == 0 ? -1L : payload.smokingDurationMs();
            boolean playSound = payload.isFirstTick() || payload.smokingDurationMs() > 0;

            HookahSmokePacket broadcast = new HookahSmokePacket(
                    sender.getUUID(),
                    payload.standX(), payload.standY(), payload.standZ(),
                    broadcastDuration, playSound, effectColor
            );

            for (ServerPlayer other : level.players()) {
                if (other.distanceTo(sender) <= 64) {
                    ServerPlayNetworking.send(other, broadcast);
                }
            }
        });
    }
}
