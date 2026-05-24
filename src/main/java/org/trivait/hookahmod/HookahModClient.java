package org.trivait.hookahmod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.trivait.hookahmod.config.Config;
import org.trivait.hookahmod.network.HookahStartPacket;
import org.trivait.hookahmod.network.ModNetworkingClient;
import org.trivait.hookahmod.particle.HookahParticle;
import org.trivait.hookahmod.particle.ModParticles;
import org.trivait.hookahmod.particle.ParticleHelper;

public class HookahModClient implements ClientModInitializer {
    public static Config CONFIG;

    private long start = 0;
    private long particleEnd = 0;

    private boolean brewing = false;
    private boolean showParticle = false;
    private BlockPos brewingStandPos = null;

    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        ModSounds.register();
        ModParticles.register();

        ParticleFactoryRegistry.getInstance().register(ModParticles.HOOKAH_PARTICLE, HookahParticle.Factory::new);

        AutoConfig.register(Config.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();

        ModNetworkingClient.register();

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (level.isClientSide() && player.isCrouching() && CONFIG.modEnabled
                    && level.getBlockState(hit.getBlockPos()).getBlock() instanceof BrewingStandBlock) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            ModNetworkingClient.tick();

            if (!CONFIG.modEnabled) return;

            LocalPlayer player = client.player;

            if (client.hitResult instanceof BlockHitResult blockHitResult
                    && player.isCrouching()
                    && client.options.keyUse.isDown()) {

                BlockPos pos = blockHitResult.getBlockPos();
                Block block = client.level.getBlockState(pos).getBlock();

                if (block instanceof BrewingStandBlock) {
                    if (!brewing) {
                        brewing = true;
                        start = System.currentTimeMillis();
                        tickCounter = 0;
                        brewingStandPos = pos;

                        if (CONFIG.sounds) player.playSound(ModSounds.INHALE);
                    }

                    boolean firstTick = tickCounter == 0;
                    tickCounter++;
                    ClientPlayNetworking.send(new HookahStartPacket(
                            0L, firstTick, pos.getX(), pos.getY(), pos.getZ()
                    ));

                    if (tickCounter % 3 == 0 && CONFIG.particlesAboveBrewingStand) {
                        ParticleHelper.spawnSmoke(client.level,
                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                0, 0.3, 0,
                                ModNetworkingClient.getSelfEffectColor());
                    }
                }

            } else if (brewing) {
                brewing = false;
                showParticle = true;

                long brewingTime = System.currentTimeMillis() - start;
                particleEnd = System.currentTimeMillis() + brewingTime;

                if (CONFIG.sounds) player.playSound(ModSounds.EXHALE);

                BlockPos stand = brewingStandPos != null ? brewingStandPos : BlockPos.ZERO;
                ClientPlayNetworking.send(new HookahStartPacket(
                        brewingTime, false, stand.getX(), stand.getY(), stand.getZ()
                ));
                brewingStandPos = null;
            }

            if (showParticle && System.currentTimeMillis() < particleEnd) {
                Vec3 eye = player.getEyePosition();
                Vec3 look = player.getViewVector(1f);
                ClientLevel level = client.level;

                ParticleHelper.spawnSmoke(level,
                        eye.x + look.x * 0.5,
                        (eye.y + look.y * 0.5) - 0.3,
                        eye.z + look.z * 0.5,
                        look.x * 0.1, look.y * 0.1, look.z * 0.1,
                        ModNetworkingClient.getSelfEffectColor());
            } else {
                showParticle = false;
                ModNetworkingClient.removeSelfExhaleWhenDone();
            }
        });
    }
}
