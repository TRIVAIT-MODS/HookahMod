package org.trivait.hookahmod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import org.trivait.hookahmod.HookahModClient;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Config cfg = HookahModClient.CONFIG;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("text.autoconfig.hookah_mod.title"))
                    .setSavingRunnable(() -> {
                        me.shedaniel.autoconfig.AutoConfig.getConfigHolder(Config.class).save();
                    });

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

            general.addEntry(eb.startBooleanToggle(
                            Component.translatable("text.autoconfig.hookah_mod.option.modEnabled"),
                            cfg.modEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> cfg.modEnabled = v)
                    .build());

            general.addEntry(eb.startBooleanToggle(
                            Component.translatable("text.autoconfig.hookah_mod.option.particlesAboveBrewingStand"),
                            cfg.particlesAboveBrewingStand)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> cfg.particlesAboveBrewingStand = v)
                    .build());

            general.addEntry(eb.startBooleanToggle(
                            Component.translatable("text.autoconfig.hookah_mod.option.sounds"),
                            cfg.sounds)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> cfg.sounds = v)
                    .build());

            ConfigCategory particles = builder.getOrCreateCategory(
                    Component.translatable("text.autoconfig.hookah_mod.option.particleSettings"));

            particles.addEntry(eb.startBooleanToggle(
                            Component.translatable("text.autoconfig.hookah_mod.option.particleSettings.whiteParticles"),
                            cfg.particleSettings.whiteParticles)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> cfg.particleSettings.whiteParticles = v)
                    .build());

            particles.addEntry(eb.startBooleanToggle(
                            Component.translatable("text.autoconfig.hookah_mod.option.particleSettings.shadeOfEffect"),
                            cfg.particleSettings.shadeOfEffect)
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("text.autoconfig.hookah_mod.option.particleSettings.shadeOfEffect.@Tooltip"))
                    .setSaveConsumer(v -> cfg.particleSettings.shadeOfEffect = v)
                    .build());

            particles.addEntry(eb.startIntSlider(
                            Component.translatable("text.autoconfig.hookah_mod.option.particleSettings.tintCoefficient"),
                            cfg.particleSettings.tintCoefficient, 1, 100)
                    .setDefaultValue(60)
                    .setSaveConsumer(v -> cfg.particleSettings.tintCoefficient = v)
                    .build());

            return builder.build();
        };
    }
}
