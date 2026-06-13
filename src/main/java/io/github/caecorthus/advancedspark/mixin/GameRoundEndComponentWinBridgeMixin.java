package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoundEndBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkWinBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * English: Observes Wathe round-end data writes before vanilla state is stored.
 * Chinese: 在 Wathe 写入原版回合结束数据前观察胜利结果。
 */
@Mixin(GameRoundEndComponent.class)
public abstract class GameRoundEndComponentWinBridgeMixin {
    @Shadow
    @Final
    private World world;

    @Inject(method = "setRoundEndData(Ljava/util/List;Ldev/doctor4t/wathe/game/GameFunctions$WinStatus;)V", at = @At("HEAD"))
    private void advancedspark$beforeRoundEndData(
            List<ServerPlayerEntity> players,
            GameFunctions.WinStatus winStatus,
            CallbackInfo ci
    ) {
        MinecraftServer server = this.world.getServer();
        if (this.world instanceof ServerWorld serverWorld) {
            AdvancedSparkRoundEndBridge.captureRoundEndData(serverWorld, winStatus);
            AdvancedSparkWinBridge.dispatchWinDetermined(
                    serverWorld,
                    GameWorldComponent.KEY.get(serverWorld),
                    winStatus,
                    null
            );
        }
        if (server != null) {
            AdvancedSparkWinBridge.dispatchWinnerDeclared(server, winStatus);
        }
    }
}
