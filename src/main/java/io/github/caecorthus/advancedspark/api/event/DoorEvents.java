package io.github.caecorthus.advancedspark.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Door interaction callbacks for map mechanics.
 * Chinese: 面向地图机制的门交互回调。
 */
public final class DoorEvents {
    public static final Event<BeforeDoorUse> BEFORE_DOOR_USE = EventFactory.createArrayBacked(
            BeforeDoorUse.class,
            callbacks -> (player, doorPos) -> {
                for (BeforeDoorUse callback : callbacks) {
                    ActionResult result = callback.beforeDoorUse(player, doorPos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    public static final Event<AfterDoorUse> AFTER_DOOR_USE = EventFactory.createArrayBacked(
            AfterDoorUse.class,
            callbacks -> (player, doorPos) -> {
                for (AfterDoorUse callback : callbacks) {
                    callback.afterDoorUse(player, doorPos);
                }
            }
    );

    private DoorEvents() {
    }

    public enum Decision {
        PASS,
        ALLOW,
        CANCEL
    }

    public static final class Bridge<TPlayer, TDoor> {
        private final List<BridgeBeforeDoorUse<TPlayer, TDoor>> beforeDoorUseCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeAfterDoorUse<TPlayer, TDoor>> afterDoorUseCallbacks = new CopyOnWriteArrayList<>();

        public void registerBeforeDoorUse(BridgeBeforeDoorUse<TPlayer, TDoor> callback) {
            beforeDoorUseCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerAfterDoorUse(BridgeAfterDoorUse<TPlayer, TDoor> callback) {
            afterDoorUseCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public Decision dispatchBeforeDoorUse(TPlayer player, TDoor door) {
            for (BridgeBeforeDoorUse<TPlayer, TDoor> callback : beforeDoorUseCallbacks) {
                Decision decision = Objects.requireNonNull(callback.beforeDoorUse(player, door), "decision");
                if (decision != Decision.PASS) {
                    return decision;
                }
            }
            return Decision.PASS;
        }

        public void dispatchAfterDoorUse(TPlayer player, TDoor door) {
            for (BridgeAfterDoorUse<TPlayer, TDoor> callback : afterDoorUseCallbacks) {
                callback.afterDoorUse(player, door);
            }
        }
    }

    @FunctionalInterface
    public interface BridgeBeforeDoorUse<TPlayer, TDoor> {
        Decision beforeDoorUse(TPlayer player, TDoor door);
    }

    @FunctionalInterface
    public interface BridgeAfterDoorUse<TPlayer, TDoor> {
        void afterDoorUse(TPlayer player, TDoor door);
    }

    @FunctionalInterface
    public interface BeforeDoorUse {
        ActionResult beforeDoorUse(ServerPlayerEntity player, BlockPos doorPos);
    }

    @FunctionalInterface
    public interface AfterDoorUse {
        void afterDoorUse(ServerPlayerEntity player, BlockPos doorPos);
    }
}
