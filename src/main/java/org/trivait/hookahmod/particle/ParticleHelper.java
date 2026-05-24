package org.trivait.hookahmod.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import org.trivait.hookahmod.HookahModClient;
import org.trivait.hookahmod.config.Config;

public class ParticleHelper {

    /**
     * Spawns a hookah smoke particle (or campfire smoke if white particles are off).
     * If effectColor != -1 and shadeOfEffect is enabled, applies tint.
     *
     * @param effectColor packed RGB, or -1 for no tint
     */
    public static void spawnSmoke(ClientLevel level,
                                   double x, double y, double z,
                                   double vx, double vy, double vz,
                                   int effectColor) {
        Config cfg = HookahModClient.CONFIG;
        if (cfg == null) return;

        if (!cfg.particleSettings.whiteParticles) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, vx, vy, vz);
            return;
        }

        // Spawn via the particle engine so we can get the instance back for tinting
        Minecraft mc = Minecraft.getInstance();
        Particle particle = mc.particleEngine.createParticle(
                ModParticles.HOOKAH_PARTICLE, x, y, z, vx, vy, vz
        );

        if (particle instanceof HookahParticle hp
                && effectColor != -1
                && cfg.particleSettings.shadeOfEffect) {
            float coeff = cfg.particleSettings.tintCoefficient / 100f;
            hp.applyTint(effectColor, coeff);
        }
    }
}
