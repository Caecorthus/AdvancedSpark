package io.github.caecorthus.advancedspark.effect;

import dev.doctor4t.wathe.cca.PlayerStaminaComponent;
import dev.doctor4t.wathe.index.WatheAttributes;
import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import io.github.caecorthus.advancedspark.content.AdvancedSparkStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * English: NoellesRoles stimulation effect for temporary infinite stamina and end-of-effect fatigue.
 * Chinese: NoellesRoles 兴奋效果，提供临时无限体力并在结束时施加疲劳惩罚。
 */
public final class StimulationEffect extends StatusEffect {
    public static final Identifier VODKA_STAMINA_MODIFIER_ID =
            AdvancedSparkSourceIds.noellesRoles("vodka_stimulation_sprint");

    public StimulationEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFF00);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return true;
        }

        StatusEffectInstance instance = player.getStatusEffect(AdvancedSparkStatusEffects.STIMULATION.registered());
        if (instance != null && instance.getDuration() <= 1) {
            removeStimulation(player, instance.getAmplifier());
        }
        return true;
    }

    public static void applyStaminaModifier(ServerPlayerEntity player) {
        EntityAttributeInstance attribute = player.getAttributeInstance(WatheAttributes.MAX_SPRINT_TIME);
        if (attribute == null || attribute.hasModifier(VODKA_STAMINA_MODIFIER_ID)) {
            return;
        }

        attribute.addTemporaryModifier(new EntityAttributeModifier(
                VODKA_STAMINA_MODIFIER_ID,
                499.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
        PlayerStaminaComponent stamina = PlayerStaminaComponent.KEY.get(player);
        stamina.setSprintingTicks((float) attribute.getValue());
        stamina.setMaxSprintTime((int) attribute.getValue());
        stamina.setExhausted(false);
        stamina.sync();
    }

    private static void removeStimulation(ServerPlayerEntity player, int amplifier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(WatheAttributes.MAX_SPRINT_TIME);
        if (attribute != null) {
            attribute.removeModifier(VODKA_STAMINA_MODIFIER_ID);
        }

        PlayerStaminaComponent stamina = PlayerStaminaComponent.KEY.get(player);
        stamina.setSprintingTicks(0.0f);
        stamina.setExhausted(true);
        stamina.sync();

        int slownessDuration = amplifier >= 1 ? 6 * 20 : 3 * 20;
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                slownessDuration,
                1,
                false,
                false,
                true
        ));
    }
}
