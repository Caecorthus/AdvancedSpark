package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.api.event.TaskComplete;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkTaskBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Bridges Wathe task completion feedback into AdvancedSpark task events.
 * Chinese: 将 Wathe 任务完成反馈桥接到 AdvancedSpark 任务事件。
 */
@Mixin(PlayerMoodComponent.class)
public abstract class PlayerMoodComponentTaskBridgeMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @Unique
    private PlayerMoodComponent.Task advancedspark$completedTaskType;

    @Redirect(
            method = "serverTick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/PlayerMoodComponent$TrainTask;isFulfilled(Lnet/minecraft/entity/player/PlayerEntity;)Z"
            )
    )
    private boolean advancedspark$beforeTaskComplete(PlayerMoodComponent.TrainTask task, PlayerEntity player) {
        if (!task.isFulfilled(player)) {
            return false;
        }
        if (this.player instanceof ServerPlayerEntity serverPlayer
                && AdvancedSparkTaskBridge.shouldCancelWatheTaskComplete(serverPlayer, task.getType())) {
            return false;
        }
        this.advancedspark$completedTaskType = task.getType();
        return true;
    }

    @Inject(
            method = "serverTick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking;send(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/packet/CustomPayload;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void advancedspark$afterTaskComplete(CallbackInfo ci) {
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            PlayerMoodComponent.Task taskType = this.advancedspark$completedTaskType;
            AdvancedSparkTaskBridge.afterWatheTaskComplete(serverPlayer, taskType);
            TaskComplete.EVENT.invoker().onTaskComplete(serverPlayer, taskType);
            this.advancedspark$completedTaskType = null;
        }
    }
}
