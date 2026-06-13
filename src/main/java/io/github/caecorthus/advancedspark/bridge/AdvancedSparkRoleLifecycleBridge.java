package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.api.event.RoleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Central lifecycle hook registry for migrated role component setup and resets.
 * Chinese: 面向已迁移职业组件初始化与重置的集中生命周期钩子注册器。
 */
public final class AdvancedSparkRoleLifecycleBridge {
    private static final Bridge<ServerPlayerEntity> GLOBAL = new Bridge<>();
    private static boolean initialized;

    private AdvancedSparkRoleLifecycleBridge() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        RoleEvents.ROLE_ASSIGNED.register(GLOBAL::dispatchRoleAssigned);
        RoleEvents.ROLE_RESET.register(GLOBAL::dispatchPlayerReset);
    }

    public static void registerRoleAssigned(Identifier roleId, RoleAssigned<ServerPlayerEntity> callback) {
        GLOBAL.registerRoleAssigned(roleId, callback);
    }

    public static void registerPlayerReset(PlayerReset<ServerPlayerEntity> callback) {
        GLOBAL.registerPlayerReset(callback);
    }

    public static final class Bridge<TPlayer> {
        private final Map<Identifier, List<RoleAssigned<TPlayer>>> roleAssignedCallbacks = new ConcurrentHashMap<>();
        private final List<PlayerReset<TPlayer>> resetCallbacks = new CopyOnWriteArrayList<>();

        public void registerRoleAssigned(Identifier roleId, RoleAssigned<TPlayer> callback) {
            Objects.requireNonNull(roleId, "roleId");
            Objects.requireNonNull(callback, "callback");
            roleAssignedCallbacks.computeIfAbsent(roleId, ignored -> new CopyOnWriteArrayList<>()).add(callback);
        }

        public void registerPlayerReset(PlayerReset<TPlayer> callback) {
            resetCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void dispatchRoleAssigned(TPlayer player, Identifier roleId) {
            Objects.requireNonNull(roleId, "roleId");
            for (RoleAssigned<TPlayer> callback : roleAssignedCallbacks.getOrDefault(roleId, List.of())) {
                callback.onRoleAssigned(player);
            }
        }

        public void dispatchPlayerReset(TPlayer player) {
            for (PlayerReset<TPlayer> callback : resetCallbacks) {
                callback.onPlayerReset(player);
            }
        }
    }

    @FunctionalInterface
    public interface RoleAssigned<TPlayer> {
        void onRoleAssigned(TPlayer player);
    }

    @FunctionalInterface
    public interface PlayerReset<TPlayer> {
        void onPlayerReset(TPlayer player);
    }
}
