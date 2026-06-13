package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerStaminaComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheAttributes;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkStaminaRules;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * English: Connects Spark-wathe public stamina state to player movement ticks.
 * Chinese: 将 Spark-wathe 公开体力状态接入玩家移动 tick。
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityStaminaBridgeMixin extends LivingEntity {
    protected PlayerEntityStaminaBridgeMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void advancedspark$addMaxSprintTimeAttribute(
            CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir
    ) {
        cir.getReturnValue().add(WatheAttributes.MAX_SPRINT_TIME, 200.0);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void advancedspark$tickSparkStamina(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        GameWorldComponent gameComponent = GameWorldComponent.KEY.get(this.getWorld());
        if (gameComponent == null || !gameComponent.isRunning() || !GameFunctions.isPlayerAliveAndSurvival(player)) {
            return;
        }

        PlayerStaminaComponent stamina = PlayerStaminaComponent.KEY.get(player);
        Role role = gameComponent.getRole(player);
        int maxSprintTime = maxSprintTime(player, role);
        boolean canSprintFromMood = !PlayerMoodComponent.KEY.get(player).isLowerThanDepressed();
        AdvancedSparkStaminaRules.TickResult result = AdvancedSparkStaminaRules.tick(
                stamina.state(),
                maxSprintTime,
                this.isSprinting(),
                canSprintFromMood
        );

        if (!result.sprintingAllowed()) {
            this.setSprinting(false);
        }
        stamina.apply(result.state());
    }

    private static int maxSprintTime(PlayerEntity player, Role role) {
        if (role == null || role.getMaxSprintTime() < 0) {
            return -1;
        }

        EntityAttributeInstance sprintAttribute = player.getAttributeInstance(WatheAttributes.MAX_SPRINT_TIME);
        if (sprintAttribute == null) {
            return role.getMaxSprintTime();
        }
        if (sprintAttribute.getBaseValue() != role.getMaxSprintTime()) {
            sprintAttribute.setBaseValue(role.getMaxSprintTime());
        }
        return (int) sprintAttribute.getValue();
    }
}
