package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.RoleSelectionContext;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.ScoreboardRoleSelectorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * English: Safe bridge for Spark-wathe ScoreboardRoleSelector forced-role additions.
 * Chinese: Spark-wathe 记分板职业选择器强制职业扩展的安全桥接层。
 */
public final class AdvancedSparkScoreboardRoleSelectionBridge {
    private AdvancedSparkScoreboardRoleSelectionBridge() {
    }

    public static Map<Role, List<UUID>> getForcedRoles(ScoreboardRoleSelectorComponent component) {
        Map<Role, List<UUID>> copy = new LinkedHashMap<>();
        access(component).advancedspark$getForcedRoles()
                .forEach((role, players) -> copy.put(role, List.copyOf(players)));
        return Map.copyOf(copy);
    }

    public static List<UUID> getForcedForRole(ScoreboardRoleSelectorComponent component, Role role) {
        return List.copyOf(access(component).advancedspark$getForcedForRole(Objects.requireNonNull(role, "role")));
    }

    public static void addForcedRole(ScoreboardRoleSelectorComponent component, Role role, UUID playerId) {
        access(component).advancedspark$addForcedRole(
                Objects.requireNonNull(role, "role"),
                Objects.requireNonNull(playerId, "playerId")
        );
    }

    public static void removeForcedRole(ScoreboardRoleSelectorComponent component, UUID playerId) {
        access(component).advancedspark$removeForcedRole(Objects.requireNonNull(playerId, "playerId"));
    }

    @Nullable
    public static Role getForcedRoleForPlayer(ScoreboardRoleSelectorComponent component, UUID playerId) {
        return access(component).advancedspark$getForcedRoleForPlayer(Objects.requireNonNull(playerId, "playerId"));
    }

    public static void assignForcedRoles(
            ScoreboardRoleSelectorComponent component,
            ServerWorld world,
            GameWorldComponent gameComponent,
            List<ServerPlayerEntity> players
    ) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(gameComponent, "gameComponent");
        Objects.requireNonNull(players, "players");
        AdvancedSparkForcedRoleAccess access = access(component);
        for (Map.Entry<Role, List<UUID>> entry : access.advancedspark$getForcedRoles().entrySet()) {
            Role role = entry.getKey();
            for (UUID playerId : entry.getValue()) {
                PlayerEntity player = world.getPlayerByUuid(playerId);
                if (player instanceof ServerPlayerEntity serverPlayer && players.contains(serverPlayer)) {
                    gameComponent.addRole(player, role);
                }
            }
        }
        access.advancedspark$clearForcedRoles();
    }

    public static RoleSelectionContext createSelectionContext(
            ServerWorld world,
            GameWorldComponent gameComponent,
            List<ServerPlayerEntity> players
    ) {
        Objects.requireNonNull(players, "players");
        int totalPlayerCount = players.size();
        int targetKillerCount = targetCount(totalPlayerCount, Objects.requireNonNull(gameComponent, "gameComponent").getKillerDividend());
        int targetVigilanteCount = targetCount(totalPlayerCount, gameComponent.getVigilanteDividend());
        return new RoleSelectionContext(world, gameComponent, List.copyOf(players), totalPlayerCount, targetKillerCount, 0, targetVigilanteCount);
    }

    private static int targetCount(int playerCount, int dividend) {
        if (dividend <= 0) {
            return 0;
        }
        return Math.max(0, (int) Math.floor((double) playerCount / dividend));
    }

    private static AdvancedSparkForcedRoleAccess access(ScoreboardRoleSelectorComponent component) {
        Objects.requireNonNull(component, "component");
        if (component instanceof AdvancedSparkForcedRoleAccess forcedRoleAccess) {
            return forcedRoleAccess;
        }
        throw new IllegalStateException("AdvancedSpark forced-role mixin is not applied");
    }
}
