package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkWinBridge;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Lets AdvancedSpark neutral winners stop the round before vanilla faction wins are chosen.
 * Chinese: 允许 AdvancedSpark 中立胜利在原版阵营胜利判定前结束回合。
 */
@Mixin(MurderGameMode.class)
public abstract class MurderGameModeNeutralWinBridgeMixin {
    @Inject(method = "tickServerGameLoop(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/cca/GameWorldComponent;)V", at = @At("HEAD"), cancellable = true)
    private void advancedspark$stopForNeutralWinner(
            ServerWorld serverWorld,
            GameWorldComponent gameWorldComponent,
            CallbackInfo ci
    ) {
        if (AdvancedSparkWinBridge.stopGameForNeutralWinner(serverWorld, gameWorldComponent)) {
            ci.cancel();
        }
    }
}
