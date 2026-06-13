package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe win condition override event.
 * Chinese: Spark-wathe 胜利条件覆盖事件。
 */
public interface CheckWinCondition {
    Event<CheckWinCondition> EVENT = EventFactory.createArrayBacked(
            CheckWinCondition.class,
            callbacks -> (world, gameComponent, currentStatus) -> {
                for (CheckWinCondition callback : callbacks) {
                    WinResult result = callback.checkWin(world, gameComponent, currentStatus);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
    );

    @Nullable
    WinResult checkWin(ServerWorld world, GameWorldComponent gameComponent, GameFunctions.WinStatus currentStatus);

    record WinResult(GameFunctions.WinStatus status, @Nullable ServerPlayerEntity winner) {
        public static WinResult neutralWin(ServerPlayerEntity winner) {
            return new WinResult(GameFunctions.WinStatus.LOOSE_END, winner);
        }

        public static WinResult block() {
            return new WinResult(GameFunctions.WinStatus.NONE, null);
        }

        public static WinResult allow(GameFunctions.WinStatus status) {
            return new WinResult(status, null);
        }
    }
}
