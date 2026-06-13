package io.github.caecorthus.advancedspark.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Task callbacks for future objective and interruption logic.
 * Chinese: 面向未来任务目标和打断逻辑的任务回调。
 */
public final class TaskEvents {
    public static final Event<BeforeTaskComplete> BEFORE_TASK_COMPLETE = EventFactory.createArrayBacked(
            BeforeTaskComplete.class,
            callbacks -> (player, taskId) -> {
                for (BeforeTaskComplete callback : callbacks) {
                    ActionResult result = callback.beforeTaskComplete(player, taskId);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    public static final Event<AfterTaskComplete> AFTER_TASK_COMPLETE = EventFactory.createArrayBacked(
            AfterTaskComplete.class,
            callbacks -> (player, taskId) -> {
                for (AfterTaskComplete callback : callbacks) {
                    callback.afterTaskComplete(player, taskId);
                }
            }
    );

    private TaskEvents() {
    }

    public enum Decision {
        PASS,
        ALLOW,
        CANCEL
    }

    public static final class Bridge<TPlayer, TTask> {
        private final List<BridgeBeforeTaskComplete<TPlayer, TTask>> beforeTaskCompleteCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeAfterTaskComplete<TPlayer, TTask>> afterTaskCompleteCallbacks = new CopyOnWriteArrayList<>();

        public void registerBeforeTaskComplete(BridgeBeforeTaskComplete<TPlayer, TTask> callback) {
            beforeTaskCompleteCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerAfterTaskComplete(BridgeAfterTaskComplete<TPlayer, TTask> callback) {
            afterTaskCompleteCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public Decision dispatchBeforeTaskComplete(TPlayer player, TTask task) {
            for (BridgeBeforeTaskComplete<TPlayer, TTask> callback : beforeTaskCompleteCallbacks) {
                Decision decision = Objects.requireNonNull(callback.beforeTaskComplete(player, task), "decision");
                if (decision != Decision.PASS) {
                    return decision;
                }
            }
            return Decision.PASS;
        }

        public Decision dispatchTaskComplete(TPlayer player, TTask task, Runnable vanillaTaskComplete) {
            Objects.requireNonNull(vanillaTaskComplete, "vanillaTaskComplete");
            Decision decision = dispatchBeforeTaskComplete(player, task);
            if (decision == Decision.CANCEL) {
                return decision;
            }
            vanillaTaskComplete.run();
            dispatchAfterTaskComplete(player, task);
            return decision;
        }

        public void dispatchAfterTaskComplete(TPlayer player, TTask task) {
            for (BridgeAfterTaskComplete<TPlayer, TTask> callback : afterTaskCompleteCallbacks) {
                callback.afterTaskComplete(player, task);
            }
        }
    }

    @FunctionalInterface
    public interface BridgeBeforeTaskComplete<TPlayer, TTask> {
        Decision beforeTaskComplete(TPlayer player, TTask task);
    }

    @FunctionalInterface
    public interface BridgeAfterTaskComplete<TPlayer, TTask> {
        void afterTaskComplete(TPlayer player, TTask task);
    }

    @FunctionalInterface
    public interface BeforeTaskComplete {
        ActionResult beforeTaskComplete(ServerPlayerEntity player, Identifier taskId);
    }

    @FunctionalInterface
    public interface AfterTaskComplete {
        void afterTaskComplete(ServerPlayerEntity player, Identifier taskId);
    }
}
