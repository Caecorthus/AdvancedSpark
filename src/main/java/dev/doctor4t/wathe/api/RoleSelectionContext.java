package dev.doctor4t.wathe.api;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * English: Spark-wathe role selection context shim used by appearance conditions.
 * Chinese: 供出现条件使用的 Spark-wathe 职业选择上下文垫片。
 */
public record RoleSelectionContext(
        ServerWorld world,
        GameWorldComponent gameComponent,
        List<ServerPlayerEntity> players,
        int totalPlayerCount,
        int targetKillerCount,
        int targetNeutralCount,
        int targetVigilanteCount,
        Set<Role> assignedRoles
) {
    public RoleSelectionContext {
        players = List.copyOf(players == null ? List.of() : players);
        assignedRoles = Set.copyOf(Objects.requireNonNull(assignedRoles, "assignedRoles"));
    }

    public RoleSelectionContext(
            ServerWorld world,
            GameWorldComponent gameComponent,
            List<ServerPlayerEntity> players,
            int totalPlayerCount,
            int targetKillerCount,
            int targetNeutralCount,
            int targetVigilanteCount
    ) {
        this(world, gameComponent, players, totalPlayerCount, targetKillerCount, targetNeutralCount, targetVigilanteCount, Set.of());
    }

    public RoleSelectionContext(
            int totalPlayerCount,
            int targetKillerCount,
            int targetNeutralCount,
            int targetVigilanteCount,
            Set<Role> assignedRoles
    ) {
        this(null, null, List.of(), totalPlayerCount, targetKillerCount, targetNeutralCount, targetVigilanteCount, assignedRoles);
    }

    public RoleSelectionContext(
            int totalPlayerCount,
            int targetKillerCount,
            int targetNeutralCount,
            Set<Role> assignedRoles
    ) {
        this(null, null, List.of(), totalPlayerCount, targetKillerCount, targetNeutralCount, 0, assignedRoles);
    }

    public int getTotalPlayerCount() {
        return totalPlayerCount;
    }

    public int getTargetKillerCount() {
        return targetKillerCount;
    }

    public int getTargetNeutralCount() {
        return targetNeutralCount;
    }

    public int getTargetVigilanteCount() {
        return targetVigilanteCount;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public List<ServerPlayerEntity> getPlayers() {
        return players;
    }

    public boolean isRoleAssigned(Role role) {
        if (this.gameComponent != null) {
            return !this.gameComponent.getAllWithRole(role).isEmpty();
        }
        return assignedRoles.contains(role);
    }
}
