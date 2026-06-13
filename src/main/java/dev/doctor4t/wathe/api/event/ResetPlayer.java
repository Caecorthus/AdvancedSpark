package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * English: Spark-wathe player reset event.
 * Chinese: Spark-wathe 玩家重置事件。
 */
public interface ResetPlayer {
    Event<ResetPlayer> EVENT = EventFactory.createArrayBacked(
            ResetPlayer.class,
            callbacks -> player -> {
                for (ResetPlayer callback : callbacks) {
                    callback.onReset(player);
                }
            }
    );

    void onReset(ServerPlayerEntity player);
}
