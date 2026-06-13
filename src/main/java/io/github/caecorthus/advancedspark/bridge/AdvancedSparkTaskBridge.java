package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import io.github.caecorthus.advancedspark.api.event.TaskEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * English: Shared adapter for Wathe task completion hooks.
 * Chinese: Wathe 任务完成钩子的共享适配器。
 */
public final class AdvancedSparkTaskBridge {
    public static final Identifier GENERIC_TASK_COMPLETE = AdvancedSparkConstants.id("wathe_taskcomplete");

    private AdvancedSparkTaskBridge() {
    }

    public static void afterWatheTaskComplete(ServerPlayerEntity player) {
        TaskEvents.AFTER_TASK_COMPLETE.invoker().afterTaskComplete(player, GENERIC_TASK_COMPLETE);
    }

    public static void afterWatheTaskComplete(ServerPlayerEntity player, @Nullable PlayerMoodComponent.Task taskType) {
        TaskEvents.AFTER_TASK_COMPLETE.invoker().afterTaskComplete(player, idForWatheTask(taskType));
    }

    public static boolean shouldCancelWatheTaskComplete(@Nullable ServerPlayerEntity player, @Nullable PlayerMoodComponent.Task taskType) {
        return shouldCancelWatheTaskComplete(player, idForWatheTask(taskType));
    }

    public static boolean shouldCancelWatheTaskComplete(@Nullable ServerPlayerEntity player, Identifier taskId) {
        Objects.requireNonNull(taskId, "taskId");
        ActionResult result = TaskEvents.BEFORE_TASK_COMPLETE.invoker().beforeTaskComplete(player, taskId);
        return result == ActionResult.FAIL;
    }

    public static Identifier idForWatheTask(@Nullable PlayerMoodComponent.Task taskType) {
        if (taskType == null) {
            return GENERIC_TASK_COMPLETE;
        }
        return AdvancedSparkConstants.id("wathe_task_" + taskType.name().toLowerCase(Locale.ROOT));
    }
}
