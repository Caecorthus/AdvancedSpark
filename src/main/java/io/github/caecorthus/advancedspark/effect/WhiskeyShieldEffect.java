package io.github.caecorthus.advancedspark.effect;

import io.github.caecorthus.advancedspark.content.AdvancedSparkStatusEffects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * English: NoellesRoles whiskey shield effect; amplifier stores the remaining shield layers minus one.
 * Chinese: NoellesRoles 威士忌护盾效果；amplifier 表示剩余护盾层数减一。
 */
public final class WhiskeyShieldEffect extends StatusEffect {
    public WhiskeyShieldEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xD2691E);
    }

    public static boolean consumeShield(ServerPlayerEntity player) {
        StatusEffectInstance shield = player.getStatusEffect(AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered());
        if (shield == null) {
            return false;
        }

        int amplifier = shield.getAmplifier();
        int duration = shield.getDuration();
        player.removeStatusEffect(AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered());
        if (amplifier > 0) {
            player.addStatusEffect(new StatusEffectInstance(
                    AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered(),
                    duration,
                    amplifier - 1,
                    false,
                    false,
                    true
            ));
        }
        return true;
    }

    public static void addShieldLayer(ServerPlayerEntity player, int durationTicks) {
        StatusEffectInstance existing = player.getStatusEffect(AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered());
        if (existing == null) {
            player.addStatusEffect(new StatusEffectInstance(
                    AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered(),
                    durationTicks,
                    0,
                    false,
                    false,
                    true
            ));
            return;
        }

        int amplifier = existing.getAmplifier() + 1;
        int duration = existing.getDuration();
        player.removeStatusEffect(AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered());
        player.addStatusEffect(new StatusEffectInstance(
                AdvancedSparkStatusEffects.WHISKEY_SHIELD.registered(),
                duration,
                amplifier,
                false,
                false,
                true
        ));
    }
}
