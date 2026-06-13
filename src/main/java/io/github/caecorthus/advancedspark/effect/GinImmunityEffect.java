package io.github.caecorthus.advancedspark.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * English: NoellesRoles marker effect for gin immunity.
 * Chinese: NoellesRoles 的金酒免疫标记效果。
 */
public final class GinImmunityEffect extends StatusEffect {
    public GinImmunityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x00FF00);
    }
}
