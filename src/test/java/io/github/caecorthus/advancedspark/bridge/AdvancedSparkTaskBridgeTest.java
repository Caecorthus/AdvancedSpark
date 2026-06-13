package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import io.github.caecorthus.advancedspark.api.event.TaskEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkTaskBridgeTest {
    @Test
    public void genericWatheTaskCompletionUsesAdvancedSparkNamespace() {
        assertEquals(AdvancedSparkConstants.id("wathe_taskcomplete"), AdvancedSparkTaskBridge.GENERIC_TASK_COMPLETE);
    }

    @Test
    public void watheTaskTypesUseStableAdvancedSparkIds() {
        assertEquals(
                AdvancedSparkConstants.id("wathe_task_sleep"),
                AdvancedSparkTaskBridge.idForWatheTask(PlayerMoodComponent.Task.SLEEP)
        );
        assertEquals(
                AdvancedSparkConstants.id("wathe_task_drink"),
                AdvancedSparkTaskBridge.idForWatheTask(PlayerMoodComponent.Task.DRINK)
        );
    }

    @Test
    public void beforeWatheTaskCompletionCanCancelRuntimeTaskCompletion() {
        Identifier taskId = Identifier.of("advancedspark_test", "cancel_runtime_task");
        TaskEvents.BEFORE_TASK_COMPLETE.register((player, receivedTaskId) ->
                taskId.equals(receivedTaskId) ? ActionResult.FAIL : ActionResult.PASS
        );

        assertTrue(AdvancedSparkTaskBridge.shouldCancelWatheTaskComplete(null, taskId));
    }

    @Test
    public void beforeWatheTaskCompletionOnlyCancelsOnFail() {
        Identifier taskId = Identifier.of("advancedspark_test", "allow_runtime_task");
        TaskEvents.BEFORE_TASK_COMPLETE.register((player, receivedTaskId) ->
                taskId.equals(receivedTaskId) ? ActionResult.SUCCESS : ActionResult.PASS
        );

        assertFalse(AdvancedSparkTaskBridge.shouldCancelWatheTaskComplete(null, taskId));
    }
}
