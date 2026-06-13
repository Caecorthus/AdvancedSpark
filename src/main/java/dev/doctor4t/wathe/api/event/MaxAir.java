package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

/**
 * English: Spark-wathe max-air modifier event.
 * Chinese: Spark-wathe 最大氧气值修改事件。
 */
public final class MaxAir {
    public static final Event<Modifier> EVENT = EventFactory.createArrayBacked(
            Modifier.class,
            callbacks -> (player, maxAir) -> {
                int result = maxAir;
                for (Modifier callback : callbacks) {
                    result = callback.modifyMaxAir(player, result);
                }
                return result;
            }
    );

    private MaxAir() {
    }

    @FunctionalInterface
    public interface Modifier {
        int modifyMaxAir(PlayerEntity player, int maxAir);
    }
}
