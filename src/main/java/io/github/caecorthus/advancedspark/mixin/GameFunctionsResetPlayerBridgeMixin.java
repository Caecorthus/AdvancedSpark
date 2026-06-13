package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.api.event.ResetPlayer;
import io.github.caecorthus.advancedspark.api.event.RoleEvents;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoundStateBridge;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Bridges Wathe player reset calls into AdvancedSpark round state and role reset events.
 * Chinese: 将 Wathe 玩家重置调用桥接到 AdvancedSpark 回合状态与职业重置事件。
 */
@Mixin(GameFunctions.class)
public abstract class GameFunctionsResetPlayerBridgeMixin {
    @Inject(method = "resetPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
    private static void advancedspark$afterResetPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        AdvancedSparkRoundStateBridge.resetPlayer(
                AdvancedSparkComponents.get(player.getServer()),
                player.getUuid()
        );
        RoleEvents.ROLE_RESET.invoker().onRoleReset(player);
        ResetPlayer.EVENT.invoker().onReset(player);
    }
}
