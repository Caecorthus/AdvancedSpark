package io.github.caecorthus.advancedspark.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Role assignment and reset callbacks.
 * Chinese: 职业分配与重置回调。
 */
public final class RoleEvents {
    public static final Event<RoleAssigned> ROLE_ASSIGNED = EventFactory.createArrayBacked(
            RoleAssigned.class,
            callbacks -> (player, roleId) -> {
                for (RoleAssigned callback : callbacks) {
                    callback.onRoleAssigned(player, roleId);
                }
            }
    );

    public static final Event<RoleAssignedByUuid> ROLE_ASSIGNED_BY_UUID = EventFactory.createArrayBacked(
            RoleAssignedByUuid.class,
            callbacks -> (playerUuid, roleId) -> {
                for (RoleAssignedByUuid callback : callbacks) {
                    callback.onRoleAssigned(playerUuid, roleId);
                }
            }
    );

    public static final Event<RoleReset> ROLE_RESET = EventFactory.createArrayBacked(
            RoleReset.class,
            callbacks -> player -> {
                for (RoleReset callback : callbacks) {
                    callback.onRoleReset(player);
                }
            }
    );

    private RoleEvents() {
    }

    public static final class Bridge<TPlayer, TRole> {
        private final List<BridgeRoleAssigned<TPlayer, TRole>> roleAssignedCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeRoleReset<TPlayer>> roleResetCallbacks = new CopyOnWriteArrayList<>();

        public void registerRoleAssigned(BridgeRoleAssigned<TPlayer, TRole> callback) {
            roleAssignedCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerRoleReset(BridgeRoleReset<TPlayer> callback) {
            roleResetCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void dispatchRoleAssigned(TPlayer player, TRole role) {
            for (BridgeRoleAssigned<TPlayer, TRole> callback : roleAssignedCallbacks) {
                callback.onRoleAssigned(player, role);
            }
        }

        public void dispatchRoleReset(TPlayer player) {
            for (BridgeRoleReset<TPlayer> callback : roleResetCallbacks) {
                callback.onRoleReset(player);
            }
        }
    }

    @FunctionalInterface
    public interface BridgeRoleAssigned<TPlayer, TRole> {
        void onRoleAssigned(TPlayer player, TRole role);
    }

    @FunctionalInterface
    public interface BridgeRoleReset<TPlayer> {
        void onRoleReset(TPlayer player);
    }

    @FunctionalInterface
    public interface RoleAssigned {
        void onRoleAssigned(ServerPlayerEntity player, Identifier roleId);
    }

    @FunctionalInterface
    public interface RoleAssignedByUuid {
        void onRoleAssigned(UUID playerUuid, Identifier roleId);
    }

    @FunctionalInterface
    public interface RoleReset {
        void onRoleReset(ServerPlayerEntity player);
    }
}
