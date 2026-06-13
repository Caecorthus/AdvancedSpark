package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleAppearanceCondition;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * English: Registers AdvancedSpark-owned roles with Wathe while keeping Spark metadata in AdvancedSpark.
 * Chinese: 将 AdvancedSpark 拥有的职业注册到 Wathe，同时把 Spark 元数据保留在 AdvancedSpark 内。
 */
public final class AdvancedSparkWatheRoleRegistry {
    private static final Map<Identifier, Role> WATHE_ROLES = new ConcurrentHashMap<>();

    private AdvancedSparkWatheRoleRegistry() {
    }

    public static Role register(Role role, Faction faction) {
        return register(role, faction, false, RoleAppearanceCondition.always());
    }

    public static Role register(
            Role role,
            Faction faction,
            boolean mapSpecific,
            RoleAppearanceCondition condition
    ) {
        return register(role, faction, mapSpecific, false, condition);
    }

    public static Role register(
            Role role,
            Faction faction,
            boolean mapSpecific,
            boolean special,
            RoleAppearanceCondition condition
    ) {
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(faction, "faction");
        Objects.requireNonNull(condition, "condition");

        Identifier roleId = role.identifier();
        registerMetadata(role, faction, mapSpecific, special, condition);
        return WATHE_ROLES.computeIfAbsent(roleId, ignored -> registerWithWathe(role));
    }

    static Role registerLocal(
            Role role,
            Faction faction,
            boolean mapSpecific,
            boolean special,
            RoleAppearanceCondition condition
    ) {
        Objects.requireNonNull(role, "role");
        registerMetadata(role, faction, mapSpecific, special, condition);
        return WATHE_ROLES.computeIfAbsent(role.identifier(), ignored -> role);
    }

    public static Optional<Role> role(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return Optional.ofNullable(WATHE_ROLES.get(roleId));
    }

    public static Map<Identifier, Role> snapshot() {
        return Map.copyOf(WATHE_ROLES);
    }

    private static Role registerWithWathe(Role role) {
        return WatheRoles.ROLES.stream()
                .filter(existing -> existing.identifier().equals(role.identifier()))
                .findFirst()
                .orElseGet(() -> WatheRoles.registerRole(role));
    }

    private static void registerMetadata(
            Role role,
            Faction faction,
            boolean mapSpecific,
            boolean special,
            RoleAppearanceCondition condition
    ) {
        Objects.requireNonNull(faction, "faction");
        Objects.requireNonNull(condition, "condition");

        AdvancedSparkRoles.register(role.identifier(), faction, mapSpecific, special, condition);
        AdvancedSparkRoleAnnouncementBridge.register(role);
    }
}
