package org.trivait.hookahmod.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import org.trivait.hookahmod.HookahMod;

public class ModParticles {
    public static final SimpleParticleType HOOKAH_PARTICLE =
            registerParticle("hookah_particle", FabricParticleTypes.simple());

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(HookahMod.MOD_ID, name), particleType);
    }

    public static void register() {

    }
}