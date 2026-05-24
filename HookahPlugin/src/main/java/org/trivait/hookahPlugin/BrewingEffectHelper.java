package org.trivait.hookahPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Optional;

public class BrewingEffectHelper {

    public static final int NO_COLOR = -1;

    public static Optional<PotionEffect> getEffect(Location loc) {
        Block block = loc.getWorld().getBlockAt(loc);
        if (!(block.getState() instanceof BrewingStand stand)) return Optional.empty();

        BrewerInventory inv = stand.getInventory();

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (!(stack.getItemMeta() instanceof PotionMeta meta)) continue;

            if (!meta.getCustomEffects().isEmpty()) {
                PotionEffect orig = meta.getCustomEffects().get(0);
                return Optional.of(new PotionEffect(orig.getType(), 40, orig.getAmplifier(), false, true, true));
            }

            PotionType base = meta.getBasePotionType();
            if (base != null && !base.getPotionEffects().isEmpty()) {
                PotionEffect orig = base.getPotionEffects().get(0);
                return Optional.of(new PotionEffect(orig.getType(), 40, orig.getAmplifier(), false, true, true));
            }
        }

        ItemStack ingredient = inv.getIngredient();
        if (ingredient != null && ingredient.getType() != Material.AIR) {
            return ingredientToEffect(ingredient.getType());
        }

        return Optional.empty();
    }

    public static int getEffectColor(PotionEffect effect) {
        return effect.getType().getColor() != null
                ? effect.getType().getColor().asRGB()
                : NO_COLOR;
    }

    private static Optional<PotionEffect> ingredientToEffect(Material mat) {
        return switch (mat) {
            case SUGAR ->
                Optional.of(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, true, true));
            case BLAZE_POWDER ->
                Optional.of(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, false, true, true));
            case GLOWSTONE_DUST ->
                Optional.of(new PotionEffect(PotionEffectType.HASTE, 40, 0, false, true, true));
            case REDSTONE ->
                Optional.of(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, false, true, true));
            case FERMENTED_SPIDER_EYE, FERN, LARGE_FERN, POTTED_FERN ->
                Optional.of(new PotionEffect(PotionEffectType.NAUSEA, 40, 0, false, true, true));
            default -> Optional.empty();
        };
    }
}
