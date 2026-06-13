package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * English: Migration-safe replacement for Spark-wathe WatheRoles helper members.
 * Chinese: 面向迁移的 Spark-wathe WatheRoles 辅助成员替代层。
 */
public final class AdvancedSparkWatheRolesBridge {
    private AdvancedSparkWatheRolesBridge() {
    }

    public static Set<Role> specialRoles() {
        return AdvancedSparkRoles.specialRoles().stream()
                .map(AdvancedSparkWatheRoleRegistry::role)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static boolean isSpecial(Role role) {
        Objects.requireNonNull(role, "role");
        return AdvancedSparkRoles.specialRoles().contains(role.identifier());
    }

    public static Optional<Role> getRole(Identifier roleId) {
        return AdvancedSparkWatheRoleRegistry.role(roleId);
    }
}
