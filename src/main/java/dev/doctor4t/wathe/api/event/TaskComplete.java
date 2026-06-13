package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe task completion event.
 * Chinese: Spark-wathe 任务完成事件。
 */
public interface TaskComplete {
    Event<TaskComplete> EVENT = EventFactory.createArrayBacked(
            TaskComplete.class,
            callbacks -> (player, taskType) -> {
                for (TaskComplete callback : callbacks) {
                    callback.onTaskComplete(player, taskType);
                }
            }
    );

    void onTaskComplete(ServerPlayerEntity player, @Nullable PlayerMoodComponent.Task taskType);
}
