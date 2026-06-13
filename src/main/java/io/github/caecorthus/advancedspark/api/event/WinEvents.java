package io.github.caecorthus.advancedspark.api.event;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Objects;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Win-resolution callbacks, including neutral winner side channels.
 * Chinese: 胜利判定回调，包括中立胜利者的侧向通道。
 */
public final class WinEvents {
    public static final Event<WinnerCheck> WINNER_CHECK = EventFactory.createArrayBacked(
            WinnerCheck.class,
            callbacks -> server -> {
                for (WinnerCheck callback : callbacks) {
                    Optional<Identifier> winner = callback.findWinner(server);
                    if (winner.isPresent()) {
                        return winner;
                    }
                }
                return Optional.empty();
            }
    );

    public static final Event<WinnerDeclared> WINNER_DECLARED = EventFactory.createArrayBacked(
            WinnerDeclared.class,
            callbacks -> (server, winnerId) -> {
                for (WinnerDeclared callback : callbacks) {
                    callback.onWinnerDeclared(server, winnerId);
                }
            }
    );

    public static final Event<WinDetermined> WIN_DETERMINED = EventFactory.createArrayBacked(
            WinDetermined.class,
            callbacks -> (world, gameComponent, winStatus, neutralWinner) -> {
                for (WinDetermined callback : callbacks) {
                    callback.onWinDetermined(world, gameComponent, winStatus, neutralWinner);
                }
            }
    );

    private WinEvents() {
    }

    public record Decision<TWinner>(Kind kind, Optional<TWinner> winner) {
        public Decision {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(winner, "winner");
            if (kind == Kind.NEUTRAL && winner.isEmpty()) {
                throw new IllegalArgumentException("Neutral win decisions require a winner.");
            }
            if (kind != Kind.NEUTRAL && winner.isPresent()) {
                throw new IllegalArgumentException("Only neutral win decisions may carry a winner.");
            }
        }

        public static <TWinner> Decision<TWinner> pass() {
            return new Decision<>(Kind.PASS, Optional.empty());
        }

        public static <TWinner> Decision<TWinner> allow() {
            return new Decision<>(Kind.ALLOW, Optional.empty());
        }

        public static <TWinner> Decision<TWinner> block() {
            return new Decision<>(Kind.BLOCK, Optional.empty());
        }

        public static <TWinner> Decision<TWinner> neutral(TWinner winner) {
            return new Decision<>(Kind.NEUTRAL, Optional.of(Objects.requireNonNull(winner, "winner")));
        }

        public enum Kind {
            PASS,
            ALLOW,
            BLOCK,
            NEUTRAL
        }
    }

    public static final class Bridge<TServer, TWinner> {
        private final List<BridgeWinnerCheck<TServer, TWinner>> winnerChecks = new CopyOnWriteArrayList<>();

        public void registerWinnerCheck(BridgeWinnerCheck<TServer, TWinner> callback) {
            winnerChecks.add(Objects.requireNonNull(callback, "callback"));
        }

        public Decision<TWinner> resolveWinner(TServer server) {
            Decision<TWinner> resolved = Decision.pass();
            for (BridgeWinnerCheck<TServer, TWinner> callback : winnerChecks) {
                Decision<TWinner> decision = Objects.requireNonNull(callback.findWinner(server), "decision");
                if (decision.kind() == Decision.Kind.NEUTRAL) {
                    return decision;
                }
                if (decision.kind() == Decision.Kind.BLOCK) {
                    resolved = decision;
                } else if (decision.kind() == Decision.Kind.ALLOW && resolved.kind() == Decision.Kind.PASS) {
                    resolved = decision;
                }
            }
            return resolved;
        }
    }

    @FunctionalInterface
    public interface BridgeWinnerCheck<TServer, TWinner> {
        Decision<TWinner> findWinner(TServer server);
    }

    @FunctionalInterface
    public interface WinnerCheck {
        Optional<Identifier> findWinner(MinecraftServer server);
    }

    @FunctionalInterface
    public interface WinnerDeclared {
        void onWinnerDeclared(MinecraftServer server, Identifier winnerId);
    }

    @FunctionalInterface
    public interface WinDetermined {
        void onWinDetermined(
                @Nullable ServerWorld world,
                @Nullable GameWorldComponent gameComponent,
                GameFunctions.WinStatus winStatus,
                @Nullable ServerPlayerEntity neutralWinner
        );
    }
}
