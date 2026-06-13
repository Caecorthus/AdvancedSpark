package io.github.caecorthus.advancedspark.api.role;

import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * English: Immutable inputs used to decide whether a role can appear in a round.
 * Chinese: 用于判断职业是否能在本局出现的不可变输入。
 */
public record RoleSelectionContext(
        Identifier mapId,
        int playerCount,
        int enabledRoleCount,
        Set<UUID> playerIds,
        long seed,
        int targetKillerCount,
        int targetNeutralCount,
        int targetVigilanteCount,
        Set<Identifier> assignedRoleIds
) {
    public RoleSelectionContext {
        Objects.requireNonNull(playerIds, "playerIds");
        Objects.requireNonNull(assignedRoleIds, "assignedRoleIds");
        if (playerCount < 0) {
            throw new IllegalArgumentException("playerCount cannot be negative");
        }
        if (enabledRoleCount < 0) {
            throw new IllegalArgumentException("enabledRoleCount cannot be negative");
        }
        if (targetKillerCount < 0) {
            throw new IllegalArgumentException("targetKillerCount cannot be negative");
        }
        if (targetNeutralCount < 0) {
            throw new IllegalArgumentException("targetNeutralCount cannot be negative");
        }
        if (targetVigilanteCount < 0) {
            throw new IllegalArgumentException("targetVigilanteCount cannot be negative");
        }
        playerIds = Set.copyOf(playerIds);
        assignedRoleIds = Set.copyOf(assignedRoleIds);
    }

    public RoleSelectionContext(
            Identifier mapId,
            int playerCount,
            int enabledRoleCount,
            Set<UUID> playerIds,
            long seed
    ) {
        this(mapId, playerCount, enabledRoleCount, playerIds, seed, 0, 0, 0, Set.of());
    }

    public static RoleSelectionContext empty(Identifier mapId) {
        return new RoleSelectionContext(mapId, 0, 0, Set.of(), 0L);
    }

    public RoleSelectionContext withAssignedRole(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        Set<Identifier> nextAssignedRoleIds = new java.util.HashSet<>(assignedRoleIds);
        nextAssignedRoleIds.add(roleId);
        return new RoleSelectionContext(
                mapId,
                playerCount,
                enabledRoleCount,
                playerIds,
                seed,
                targetKillerCount,
                targetNeutralCount,
                targetVigilanteCount,
                nextAssignedRoleIds
        );
    }
}
