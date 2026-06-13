package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.api.event.KillPlayer;
import io.github.caecorthus.advancedspark.api.event.KillEvents;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkGameFunctions;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoundStateBridge;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Bridges Wathe kill lifecycle calls into AdvancedSpark events.
 * Chinese: 将 Wathe 的击杀生命周期调用桥接到 AdvancedSpark 事件。
 */
@Mixin(GameFunctions.class)
public abstract class GameFunctionsKillBridgeMixin {
    @Inject(
            method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void advancedspark$beforeKill(
            PlayerEntity victim,
            boolean spawnBody,
            PlayerEntity killer,
            Identifier deathReason,
            CallbackInfo ci
    ) {
        if (!(victim instanceof ServerPlayerEntity serverVictim)) {
            return;
        }

        ActionResult result = KillEvents.BEFORE_KILL.invoker().beforeKill(asServerPlayer(killer), serverVictim);
        KillPlayer.KillResult sparkResult = KillPlayer.BEFORE.invoker()
                .beforeKillPlayer(serverVictim, asServerPlayer(killer), deathReason);
        if (!AdvancedSparkGameFunctions.isForceKillActive()
                && (KillEvents.shouldCancelVanillaKill(result) || (sparkResult != null && sparkResult.cancelled()))) {
            ci.cancel();
        }
    }

    @Inject(
            method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
            at = @At("TAIL")
    )
    private static void advancedspark$afterKill(
            PlayerEntity victim,
            boolean spawnBody,
            PlayerEntity killer,
            Identifier deathReason,
            CallbackInfo ci
    ) {
        if (victim instanceof ServerPlayerEntity serverVictim) {
            AdvancedSparkRoundStateBridge.markKilled(
                    AdvancedSparkComponents.get(serverVictim.getServer()),
                    serverVictim.getUuid()
            );
            KillEvents.AFTER_KILL.invoker().afterKill(asServerPlayer(killer), serverVictim);
            KillPlayer.AFTER.invoker().afterKillPlayer(serverVictim, asServerPlayer(killer), deathReason);
        }
    }

    private static ServerPlayerEntity asServerPlayer(PlayerEntity player) {
        return player instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null;
    }
}
