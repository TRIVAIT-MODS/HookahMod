package org.trivait.hookahmod.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

import java.util.List;
import java.util.Optional;

public class BrewingEffectHelper {

    public static final int NO_COLOR = -1;
    public static Optional<MobEffectInstance> getEffect(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BrewingStandBlockEntity stand)) return Optional.empty();

        for (int i = 0; i < 3; i++) {
            ItemStack stack = stand.getItem(i);
            if (stack.isEmpty()) continue;

            PotionContents potionContents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            if (potionContents == null) continue;

            List<MobEffectInstance> effects = new java.util.ArrayList<>();
            potionContents.getAllEffects().forEach(effects::add);
            if (!effects.isEmpty()) {
                MobEffectInstance original = effects.get(0);
                int duration = 20;
                return Optional.of(new MobEffectInstance(
                        original.getEffect(), original.getAmplifier(), duration, false, true, true
                ));
            }
        }

        ItemStack ingredient = stand.getItem(3);
        if (!ingredient.isEmpty()) {
            Optional<MobEffectInstance> fromIngredient = ingredientToEffect(ingredient);
            if (fromIngredient.isPresent()) return fromIngredient;
        }

        return Optional.empty();
    }
    public static int getEffectColor(MobEffectInstance effect) {
        return effect.getEffect().value().getColor();
    }

    private static Optional<MobEffectInstance> ingredientToEffect(ItemStack stack) {
        if (stack.is(Items.SUGAR))
            return Optional.of(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            MobEffects.SPEED.value()
                    ), 0, 40, false, true, true));

        if (stack.is(Items.BLAZE_POWDER))
            return Optional.of(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            MobEffects.STRENGTH.value()
                    ), 0, 40, false, true, true));

        if (stack.is(Items.GLOWSTONE_DUST))
            return Optional.of(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            MobEffects.HASTE.value()
                    ), 0, 40, false, true, true));

        if (stack.is(Items.REDSTONE))
            return Optional.of(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            MobEffects.SLOWNESS.value()
                    ), 0, 40, false, true, true));

        if (stack.is(Items.FERMENTED_SPIDER_EYE) || stack.is(Items.LARGE_FERN) || stack.is(Items.FERN))
            return Optional.of(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            MobEffects.NAUSEA.value()
                    ), 0, 40, false, true, true));

        return Optional.empty();
    }
}
