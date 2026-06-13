package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.api.event.CheckWinCondition;
import io.github.caecorthus.advancedspark.api.event.WinEvents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * English: Maps Wathe round-end statuses into AdvancedSpark winner identifiers.
 * Chinese: 将 Wathe 回合结束状态映射为 AdvancedSpark 胜利者标识符。
 */
public final class AdvancedSparkWinBridge {
    private static final String WATHE_NAMESPACE = "wathe";
    private static final GameFunctions.WinStatus NEUTRAL_FALLBACK_STATUS = GameFunctions.WinStatus.LOOSE_END;

    private AdvancedSparkWinBridge() {
    }

    public static Optional<Identifier> toWinnerId(GameFunctions.WinStatus winStatus) {
        return switch (Objects.requireNonNull(winStatus, "winStatus")) {
            case NONE -> Optional.empty();
            case KILLERS -> Optional.of(Identifier.of(WATHE_NAMESPACE, "killers"));
            case PASSENGERS -> Optional.of(Identifier.of(WATHE_NAMESPACE, "passengers"));
            case TIME -> Optional.of(Identifier.of(WATHE_NAMESPACE, "time"));
            case LOOSE_END -> Optional.of(Identifier.of(WATHE_NAMESPACE, "loose_end"));
        };
    }

    public static Optional<Identifier> resolveWinnerId(
            AdvancedSparkGameState state,
            Optional<Identifier> eventNeutralWinner,
            GameFunctions.WinStatus vanillaWinStatus
    ) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(eventNeutralWinner, "eventNeutralWinner");
        return state.neutralWinner()
                .or(() -> eventNeutralWinner)
                .or(() -> toWinnerId(vanillaWinStatus));
    }

    public static Optional<Identifier> resolveNeutralWinner(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        AdvancedSparkGameState state = AdvancedSparkComponents.get(server);
        if (state.neutralWinner().isPresent()) {
            return state.neutralWinner();
        }

        Optional<Identifier> eventNeutralWinner = WinEvents.WINNER_CHECK.invoker().findWinner(server);
        eventNeutralWinner.ifPresent(state::setNeutralWinner);
        return eventNeutralWinner;
    }

    public static boolean stopGameForNeutralWinner(ServerWorld world, GameWorldComponent gameWorldComponent) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(gameWorldComponent, "gameWorldComponent");
        if (gameWorldComponent.getGameStatus() != GameWorldComponent.GameStatus.ACTIVE) {
            return false;
        }

        CheckWinCondition.WinResult sparkWin = CheckWinCondition.EVENT.invoker()
                .checkWin(world, gameWorldComponent, GameFunctions.WinStatus.NONE);
        if (sparkWin != null && sparkWin.status() != GameFunctions.WinStatus.NONE) {
            ServerPlayerEntity winner = sparkWin.winner();
            if (winner != null) {
                gameWorldComponent.setLooseEndWinner(winner.getUuid());
                WinEvents.WINNER_DECLARED.invoker().onWinnerDeclared(
                        world.getServer(),
                        Identifier.of("advancedspark", "neutral/" + winner.getUuid())
                );
            }
            GameRoundEndComponent.KEY.get(world).setRoundEndData(world.getPlayers(), sparkWin.status());
            GameFunctions.stopGame(world);
            return true;
        }

        Optional<Identifier> neutralWinner = resolveNeutralWinner(world.getServer());
        if (neutralWinner.isEmpty()) {
            return false;
        }

        GameRoundEndComponent.KEY.get(world).setRoundEndData(world.getPlayers(), NEUTRAL_FALLBACK_STATUS);
        WinEvents.WINNER_DECLARED.invoker().onWinnerDeclared(world.getServer(), neutralWinner.get());
        GameFunctions.stopGame(world);
        return true;
    }

    public static void dispatchWinnerDeclared(MinecraftServer server, GameFunctions.WinStatus winStatus) {
        Objects.requireNonNull(server, "server");
        AdvancedSparkGameState state = AdvancedSparkComponents.get(server);
        Optional<Identifier> eventNeutralWinner = WinEvents.WINNER_CHECK.invoker().findWinner(server);
        if (state.neutralWinner().isEmpty()) {
            eventNeutralWinner.ifPresent(state::setNeutralWinner);
        }
        Optional<Identifier> winnerId = resolveWinnerId(state, eventNeutralWinner, winStatus);

        winnerId.ifPresent(winner -> WinEvents.WINNER_DECLARED.invoker().onWinnerDeclared(server, winner));
    }

    public static void dispatchWinDetermined(
            @Nullable ServerWorld world,
            @Nullable GameWorldComponent gameComponent,
            GameFunctions.WinStatus winStatus,
            @Nullable ServerPlayerEntity neutralWinner
    ) {
        WinEvents.WIN_DETERMINED.invoker().onWinDetermined(
                world,
                gameComponent,
                Objects.requireNonNull(winStatus, "winStatus"),
                neutralWinner
        );
    }
}
