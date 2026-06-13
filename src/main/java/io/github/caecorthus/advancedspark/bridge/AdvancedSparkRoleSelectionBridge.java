package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleSelectionContext;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * English: Selects AdvancedSpark roles after Wathe has assigned its vanilla baseline roles.
 * Chinese: 在 Wathe 分配原版基础职业后选择 AdvancedSpark 职业。
 */
public final class AdvancedSparkRoleSelectionBridge {
    private AdvancedSparkRoleSelectionBridge() {
    }

    public static void assignAdvancedRoles(
            ServerWorld world,
            GameWorldComponent gameComponent,
            List<ServerPlayerEntity> players
    ) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(gameComponent, "gameComponent");
        Objects.requireNonNull(players, "players");

        Map<Identifier, Role> watheRoles = AdvancedSparkWatheRoleRegistry.snapshot();
        if (watheRoles.isEmpty()) {
            return;
        }

        List<Candidate> candidates = players.stream()
                .map(player -> new Candidate(player.getUuid(), baseFaction(gameComponent, player)))
                .toList();
        AdvancedSparkGameState state = AdvancedSparkComponents.get(world.getServer());
        List<AdvancedSparkRoles.RoleMetadata> enabledRoles = AdvancedSparkRoles.snapshot().values().stream()
                .filter(role -> AdvancedSparkGameWorldBridge.isRoleEnabled(state, role))
                .toList();
        RoleSelectionContext context = new RoleSelectionContext(
                null,
                players.size(),
                enabledRoles.size(),
                playerIds(players),
                world.getSeed(),
                targetCount(players.size(), gameComponent.getKillerDividend()),
                countEnabledRoles(enabledRoles, Faction.NEUTRAL),
                targetCount(players.size(), gameComponent.getVigilanteDividend()),
                Set.of()
        );
        Random random = new Random(world.getRandom().nextLong());

        for (Assignment assignment : selectAssignments(enabledRoles, candidates, context, random)) {
            Role role = watheRoles.get(assignment.roleId());
            if (role != null && world.getPlayerByUuid(assignment.playerId()) instanceof ServerPlayerEntity player) {
                gameComponent.addRole(player, role);
            }
        }
    }

    public static List<Assignment> selectAssignments(
            Collection<AdvancedSparkRoles.RoleMetadata> roles,
            List<Candidate> candidates,
            RoleSelectionContext context,
            Random random
    ) {
        Objects.requireNonNull(roles, "roles");
        Objects.requireNonNull(candidates, "candidates");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(random, "random");

        List<Candidate> availableCandidates = new ArrayList<>(candidates);
        List<AdvancedSparkRoles.RoleMetadata> orderedRoles = roles.stream()
                .sorted(Comparator
                        .comparingInt((AdvancedSparkRoles.RoleMetadata role) -> selectionPhase(role.faction()))
                        .thenComparing(role -> role.roleId().toString()))
                .toList();
        List<Assignment> assignments = new ArrayList<>();
        RoleSelectionContext currentContext = context;

        for (AdvancedSparkRoles.RoleMetadata role : orderedRoles) {
            if (!canAppear(role, currentContext)) {
                continue;
            }
            Optional<Candidate> candidate = chooseCandidate(role.faction(), availableCandidates, random);
            if (candidate.isEmpty()) {
                continue;
            }
            Candidate selected = candidate.get();
            assignments.add(new Assignment(selected.playerId(), role.roleId()));
            currentContext = currentContext.withAssignedRole(role.roleId());
            availableCandidates.remove(selected);
        }

        return List.copyOf(assignments);
    }

    private static boolean canAppear(AdvancedSparkRoles.RoleMetadata role, RoleSelectionContext context) {
        return (!role.mapSpecific() || context.mapId() != null) && role.canAppear(context);
    }

    private static Optional<Candidate> chooseCandidate(Faction roleFaction, List<Candidate> candidates, Random random) {
        List<Candidate> matching = candidates.stream()
                .filter(candidate -> canReceive(roleFaction, candidate.baseFaction()))
                .toList();
        if (matching.isEmpty() && roleFaction == Faction.NEUTRAL) {
            matching = candidates;
        }
        if (matching.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(matching.get(random.nextInt(matching.size())));
    }

    private static int selectionPhase(Faction faction) {
        return switch (faction) {
            case IMPOSTOR -> 0;
            case NEUTRAL -> 1;
            case CREWMATE -> 2;
            case UNKNOWN -> 3;
        };
    }

    private static int targetCount(int playerCount, int dividend) {
        if (dividend <= 0) {
            return 0;
        }
        return Math.max(0, (int) Math.floor((double) playerCount / dividend));
    }

    private static int countEnabledRoles(Collection<AdvancedSparkRoles.RoleMetadata> roles, Faction faction) {
        return (int) roles.stream()
                .filter(role -> role.faction() == faction)
                .count();
    }

    private static boolean canReceive(Faction roleFaction, Faction candidateFaction) {
        return switch (roleFaction) {
            case IMPOSTOR -> candidateFaction == Faction.IMPOSTOR;
            case CREWMATE -> candidateFaction == Faction.CREWMATE;
            case NEUTRAL -> candidateFaction != Faction.IMPOSTOR;
            case UNKNOWN -> true;
        };
    }

    private static Faction baseFaction(GameWorldComponent gameComponent, ServerPlayerEntity player) {
        Role currentRole = gameComponent.getRole(player);
        if (currentRole == null) {
            return Faction.UNKNOWN;
        }
        if (currentRole.canUseKiller()) {
            return Faction.IMPOSTOR;
        }
        if (currentRole.isInnocent()) {
            return Faction.CREWMATE;
        }
        return Faction.UNKNOWN;
    }

    private static Set<UUID> playerIds(List<ServerPlayerEntity> players) {
        Set<UUID> playerIds = new HashSet<>();
        for (ServerPlayerEntity player : players) {
            playerIds.add(player.getUuid());
        }
        return playerIds;
    }

    public record Candidate(UUID playerId, Faction baseFaction) {
        public Candidate {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(baseFaction, "baseFaction");
        }
    }

    public record Assignment(UUID playerId, Identifier roleId) {
        public Assignment {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(roleId, "roleId");
        }
    }
}
