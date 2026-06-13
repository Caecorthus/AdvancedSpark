package io.github.caecorthus.advancedspark.api.role;

import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * English: Lightweight role metadata sidecar for future Spark and NoellesRoles ports.
 * Chinese: 面向未来 Spark 与 NoellesRoles 移植的轻量职业元数据侧表。
 */
public final class AdvancedSparkRoles {
    private static final Map<Identifier, RoleMetadata> ROLES = new ConcurrentHashMap<>();

    private AdvancedSparkRoles() {
    }

    public static RoleMetadata register(Identifier roleId, Faction faction) {
        return register(roleId, faction, false, RoleAppearanceCondition.always());
    }

    public static RoleMetadata register(
            Identifier roleId,
            Faction faction,
            boolean mapSpecific,
            RoleAppearanceCondition condition
    ) {
        return register(roleId, faction, mapSpecific, false, condition);
    }

    public static RoleMetadata register(
            Identifier roleId,
            Faction faction,
            boolean mapSpecific,
            boolean special,
            RoleAppearanceCondition condition
    ) {
        Objects.requireNonNull(roleId, "roleId");
        Objects.requireNonNull(faction, "faction");
        Objects.requireNonNull(condition, "condition");
        return ROLES.compute(roleId, (id, existing) -> {
            if (existing == null) {
                return new RoleMetadata(id, faction, mapSpecific, special, condition, true);
            }
            return existing.withDefinition(faction, mapSpecific, special, condition);
        });
    }

    public static Optional<RoleMetadata> metadata(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return Optional.ofNullable(ROLES.get(roleId));
    }

    public static Map<Identifier, RoleMetadata> snapshot() {
        return Map.copyOf(ROLES);
    }

    public static Map<Identifier, RoleMetadata> enabledRoles() {
        Map<Identifier, RoleMetadata> enabled = new ConcurrentHashMap<>();
        ROLES.forEach((roleId, metadata) -> {
            if (metadata.enabled()) {
                enabled.put(roleId, metadata);
            }
        });
        return Map.copyOf(enabled);
    }

    public static Set<Identifier> specialRoles() {
        return ROLES.values().stream()
                .filter(RoleMetadata::special)
                .map(RoleMetadata::roleId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static RoleMetadata setEnabled(Identifier roleId, boolean enabled) {
        Objects.requireNonNull(roleId, "roleId");
        return ROLES.compute(roleId, (id, existing) -> {
            if (existing == null) {
                throw new IllegalArgumentException("Unknown role: " + id);
            }
            return existing.withEnabled(enabled);
        });
    }

    public static boolean isEnabled(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return metadata(roleId).map(RoleMetadata::enabled).orElse(false);
    }

    public static boolean canAppear(Identifier roleId, RoleSelectionContext context) {
        Objects.requireNonNull(roleId, "roleId");
        Objects.requireNonNull(context, "context");
        return metadata(roleId).map(metadata -> metadata.canAppear(context)).orElse(false);
    }

    public static void unregister(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        ROLES.remove(roleId);
    }

    /**
     * English: Immutable metadata for one registered role.
     * Chinese: 单个已注册职业的不可变元数据。
     */
    public record RoleMetadata(
            Identifier roleId,
            Faction faction,
            boolean mapSpecific,
            boolean special,
            RoleAppearanceCondition condition,
            boolean enabled
    ) {
        public RoleMetadata {
            Objects.requireNonNull(roleId, "roleId");
            Objects.requireNonNull(faction, "faction");
            Objects.requireNonNull(condition, "condition");
        }

        public RoleMetadata(
                Identifier roleId,
                Faction faction,
                boolean mapSpecific,
                RoleAppearanceCondition condition,
                boolean enabled
        ) {
            this(roleId, faction, mapSpecific, false, condition, enabled);
        }

        public boolean canAppear(RoleSelectionContext context) {
            Objects.requireNonNull(context, "context");
            return enabled && condition.canAppear(context);
        }

        public RoleMetadata withEnabled(boolean enabled) {
            return new RoleMetadata(roleId, faction, mapSpecific, special, condition, enabled);
        }

        public RoleMetadata withDefinition(
                Faction faction,
                boolean mapSpecific,
                boolean special,
                RoleAppearanceCondition condition
        ) {
            return new RoleMetadata(roleId, faction, mapSpecific, special, condition, enabled);
        }
    }
}
