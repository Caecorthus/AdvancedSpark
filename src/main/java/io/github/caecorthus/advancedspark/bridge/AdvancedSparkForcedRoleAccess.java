package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * English: Mixin-backed access to Spark-wathe forced role assignments.
 * Chinese: 由 mixin 支撑的 Spark-wathe 强制职业分配访问口。
 */
public interface AdvancedSparkForcedRoleAccess {
    Map<Role, List<UUID>> advancedspark$getForcedRoles();

    List<UUID> advancedspark$getForcedForRole(Role role);

    void advancedspark$addForcedRole(Role role, UUID playerId);

    void advancedspark$removeForcedRole(UUID playerId);

    @Nullable Role advancedspark$getForcedRoleForPlayer(UUID playerId);

    void advancedspark$clearForcedRoles();
}
