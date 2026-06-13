package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.record.GameRecordManager;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkComponentLifecycleBridge;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * English: Starts and ends the Spark-wathe record manager around Wathe rounds.
 * Chinese: 围绕 Wathe 回合启动与结束 Spark-wathe 记录管理器。
 */
@Mixin(GameFunctions.class)
public abstract class GameFunctionsRecordBridgeMixin {
    @Inject(
            method = "initializeGame(Lnet/minecraft/server/world/ServerWorld;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/api/GameMode;initializeGame(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/cca/GameWorldComponent;Ljava/util/List;)V"
            )
    )
    private static void advancedspark$startRecord(ServerWorld serverWorld, CallbackInfo ci) {
        GameRecordManager.startMatch(serverWorld, GameWorldComponent.KEY.get(serverWorld));
    }

    @Inject(method = "finalizeGame(Lnet/minecraft/server/world/ServerWorld;)V", at = @At("HEAD"))
    private static void advancedspark$endRecord(ServerWorld serverWorld, CallbackInfo ci) {
        GameRecordManager.endMatch(serverWorld);
    }

    @Inject(method = "finalizeGame(Lnet/minecraft/server/world/ServerWorld;)V", at = @At("TAIL"))
    private static void advancedspark$resetRoundScopedComponents(ServerWorld serverWorld, CallbackInfo ci) {
        AdvancedSparkComponentLifecycleBridge.resetWorldComponents(serverWorld);
    }
}
