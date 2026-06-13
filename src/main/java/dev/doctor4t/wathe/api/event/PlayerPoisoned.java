package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Spark-wathe compatibility hooks for server-side player poisoning.
 * Chinese: 面向 Spark-wathe 的服务端玩家中毒兼容事件。
 */
public final class PlayerPoisoned {
    private PlayerPoisoned() {
    }

    public static final Event<Before> BEFORE = createArrayBacked(Before.class, listeners -> (player, ticks, poisoner) -> {
        for (Before listener : listeners) {
            PoisonResult result = listener.beforePlayerPoisoned(player, ticks, poisoner);
            if (result != null) {
                return result;
            }
        }
        return null;
    });

    public static final Event<After> AFTER = createArrayBacked(After.class, listeners -> (player, ticks, poisoner) -> {
        for (After listener : listeners) {
            listener.afterPlayerPoisoned(player, ticks, poisoner);
        }
    });

    @FunctionalInterface
    public interface Before {
        @Nullable
        PoisonResult beforePlayerPoisoned(PlayerEntity player, int ticks, @Nullable UUID poisoner);
    }

    @FunctionalInterface
    public interface After {
        void afterPlayerPoisoned(PlayerEntity player, int ticks, @Nullable UUID poisoner);
    }

    public record PoisonResult(boolean cancelled) {
        public static PoisonResult allow() {
            return new PoisonResult(false);
        }

        public static PoisonResult cancel() {
            return new PoisonResult(true);
        }
    }
}
