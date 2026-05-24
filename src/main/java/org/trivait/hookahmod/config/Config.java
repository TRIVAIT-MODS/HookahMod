package org.trivait.hookahmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "hookah_mod")
public class Config implements ConfigData {
    public boolean modEnabled = true;
    public boolean particlesAboveBrewingStand = true;
    public boolean sounds = true;

    @ConfigEntry.Gui.CollapsibleObject
    public ParticleSettings particleSettings = new ParticleSettings();

    public static class ParticleSettings{
        public boolean whiteParticles = true;
        @ConfigEntry.Gui.Tooltip
        public boolean shadeOfEffect = true;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int tintCoefficient = 60;
    }
}