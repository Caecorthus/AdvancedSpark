package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe kill lifecycle events.
 * Chinese: Spark-wathe 击杀生命周期事件。
 */
public final class KillPlayer {
    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
            Before.class,
            callbacks -> (victim, killer, deathReason) -> {
                for (Before callback : callbacks) {
                    KillResult result = callback.beforeKillPlayer(victim, killer, deathReason);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
    );
    public static final Event<After> AFTER = EventFactory.createArrayBacked(
            After.class,
            callbacks -> (victim, killer, deathReason) -> {
                for (After callback : callbacks) {
                    callback.afterKillPlayer(victim, killer, deathReason);
                }
            }
    );

    private KillPlayer() {
    }

    @FunctionalInterface
    public interface Before {
        @Nullable
        KillResult beforeKillPlayer(
                ServerPlayerEntity victim,
                @Nullable ServerPlayerEntity killer,
                Identifier deathReason
        );
    }

    @FunctionalInterface
    public interface After {
        void afterKillPlayer(
                ServerPlayerEntity victim,
                @Nullable ServerPlayerEntity killer,
                Identifier deathReason
        );
    }

    public record KillResult(boolean cancelled, @Nullable Boolean spawnBody) {
        public static KillResult allow() {
            return new KillResult(false, null);
        }

        public static KillResult allow(boolean spawnBody) {
            return new KillResult(false, spawnBody);
        }

        public static KillResult allowWithoutBody() {
            return new KillResult(false, false);
        }

        public static KillResult cancel() {
            return new KillResult(true, null);
        }
    }
}
