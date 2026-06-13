package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.world.ServerWorld;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Spark-wathe hooks for match record lifecycle notifications.
 * Chinese: Spark-wathe 对局记录生命周期通知钩子。
 */
public final class RecordEvents {
    private RecordEvents() {
    }

    public static final Event<OnRecordEnd> ON_RECORD_END = createArrayBacked(OnRecordEnd.class, listeners -> (world, record) -> {
        for (OnRecordEnd listener : listeners) {
            listener.onRecordEnd(world, record);
        }
    });

    @FunctionalInterface
    public interface OnRecordEnd {
        void onRecordEnd(ServerWorld world, GameRecordManager.MatchRecord record);
    }
}
