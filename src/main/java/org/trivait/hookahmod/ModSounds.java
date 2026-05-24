package org.trivait.hookahmod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent INHALE = registerSoundEvent("inhale");
    public static final SoundEvent EXHALE = registerSoundEvent("exhale");

    private static SoundEvent registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HookahMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {

    }
}
