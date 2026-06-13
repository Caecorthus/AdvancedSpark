package io.github.caecorthus.advancedspark.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * English: NoellesRoles marker effect that lets later collision hooks identify phasing players.
 * Chinese: NoellesRoles 标记效果，供后续碰撞钩子识别可穿行玩家。
 */
public final class NoCollisionEffect extends StatusEffect {
    public NoCollisionEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }
}
