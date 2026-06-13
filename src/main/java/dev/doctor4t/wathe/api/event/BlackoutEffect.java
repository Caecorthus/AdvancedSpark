package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe blackout effect veto event.
 * Chinese: Spark-wathe 关灯失明效果否决事件。
 */
public final class BlackoutEffect {
    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
            Before.class,
            callbacks -> (player, durationTicks) -> {
                for (Before callback : callbacks) {
                    BlackoutResult result = callback.beforeBlackoutEffect(player, durationTicks);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
    );

    private BlackoutEffect() {
    }

    @FunctionalInterface
    public interface Before {
        @Nullable
        BlackoutResult beforeBlackoutEffect(ServerPlayerEntity player, int durationTicks);
    }

    public record BlackoutResult(boolean cancelled) {
        public static BlackoutResult allow() {
            return new BlackoutResult(false);
        }

        public static BlackoutResult cancel() {
            return new BlackoutResult(true);
        }
    }
}
