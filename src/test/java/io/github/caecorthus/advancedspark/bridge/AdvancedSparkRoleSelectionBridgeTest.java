package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleAppearanceCondition;
import io.github.caecorthus.advancedspark.api.role.RoleSelectionContext;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRoleSelectionBridgeTest {
    private static final UUID CREWMATE = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID IMPOSTOR = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID EXTRA_CREWMATE = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Test
    public void assignsRolesToMatchingBaseFactions() {
        Identifier passengerRole = Identifier.of("advancedspark_test", "passenger_role");
        Identifier killerRole = Identifier.of("advancedspark_test", "killer_role");
        List<AdvancedSparkRoles.RoleMetadata> roles = List.of(
                new AdvancedSparkRoles.RoleMetadata(passengerRole, Faction.CREWMATE, false, RoleAppearanceCondition.always(), true),
                new AdvancedSparkRoles.RoleMetadata(killerRole, Faction.IMPOSTOR, false, RoleAppearanceCondition.always(), true)
        );
        List<AdvancedSparkRoleSelectionBridge.Candidate> candidates = List.of(
                new AdvancedSparkRoleSelectionBridge.Candidate(CREWMATE, Faction.CREWMATE),
                new AdvancedSparkRoleSelectionBridge.Candidate(IMPOSTOR, Faction.IMPOSTOR)
        );

        List<AdvancedSparkRoleSelectionBridge.Assignment> assignments = AdvancedSparkRoleSelectionBridge.selectAssignments(
                roles,
                candidates,
                context(2),
                new Random(1L)
        );

        assertEquals(
                Set.of(
                        new AdvancedSparkRoleSelectionBridge.Assignment(CREWMATE, passengerRole),
                        new AdvancedSparkRoleSelectionBridge.Assignment(IMPOSTOR, killerRole)
                ),
                Set.copyOf(assignments)
        );
    }

    @Test
    public void neutralRolesPreferNonImpostorCandidates() {
        Identifier neutralRole = Identifier.of("advancedspark_test", "neutral_role");
        List<AdvancedSparkRoles.RoleMetadata> roles = List.of(
                new AdvancedSparkRoles.RoleMetadata(neutralRole, Faction.NEUTRAL, false, RoleAppearanceCondition.always(), true)
        );
        List<AdvancedSparkRoleSelectionBridge.Candidate> candidates = List.of(
                new AdvancedSparkRoleSelectionBridge.Candidate(IMPOSTOR, Faction.IMPOSTOR),
                new AdvancedSparkRoleSelectionBridge.Candidate(CREWMATE, Faction.CREWMATE)
        );

        List<AdvancedSparkRoleSelectionBridge.Assignment> assignments = AdvancedSparkRoleSelectionBridge.selectAssignments(
                roles,
                candidates,
                context(2),
                new Random(1L)
        );

        assertEquals(List.of(new AdvancedSparkRoleSelectionBridge.Assignment(CREWMATE, neutralRole)), assignments);
    }

    @Test
    public void disabledAndUnavailableRolesAreSkipped() {
        Identifier disabledRole = Identifier.of("advancedspark_test", "disabled");
        Identifier unavailableRole = Identifier.of("advancedspark_test", "unavailable");
        Identifier availableRole = Identifier.of("advancedspark_test", "available");
        List<AdvancedSparkRoles.RoleMetadata> roles = List.of(
                new AdvancedSparkRoles.RoleMetadata(disabledRole, Faction.CREWMATE, false, RoleAppearanceCondition.always(), false),
                new AdvancedSparkRoles.RoleMetadata(unavailableRole, Faction.CREWMATE, false, RoleAppearanceCondition.minPlayers(10), true),
                new AdvancedSparkRoles.RoleMetadata(availableRole, Faction.CREWMATE, false, RoleAppearanceCondition.always(), true)
        );
        List<AdvancedSparkRoleSelectionBridge.Candidate> candidates = List.of(
                new AdvancedSparkRoleSelectionBridge.Candidate(CREWMATE, Faction.CREWMATE),
                new AdvancedSparkRoleSelectionBridge.Candidate(EXTRA_CREWMATE, Faction.CREWMATE)
        );

        List<AdvancedSparkRoleSelectionBridge.Assignment> assignments = AdvancedSparkRoleSelectionBridge.selectAssignments(
                roles,
                candidates,
                context(2),
                new Random(1L)
        );

        assertEquals(1, assignments.size());
        assertEquals(availableRole, assignments.getFirst().roleId());
        assertTrue(Set.of(CREWMATE, EXTRA_CREWMATE).contains(assignments.getFirst().playerId()));
    }

    @Test
    public void mapSpecificRolesWaitForMapContext() {
        Identifier mapRole = Identifier.of("advancedspark_test", "map_role");
        List<AdvancedSparkRoles.RoleMetadata> roles = List.of(
                new AdvancedSparkRoles.RoleMetadata(mapRole, Faction.CREWMATE, true, RoleAppearanceCondition.always(), true)
        );
        List<AdvancedSparkRoleSelectionBridge.Candidate> candidates = List.of(
                new AdvancedSparkRoleSelectionBridge.Candidate(CREWMATE, Faction.CREWMATE)
        );

        List<AdvancedSparkRoleSelectionBridge.Assignment> assignments = AdvancedSparkRoleSelectionBridge.selectAssignments(
                roles,
                candidates,
                context(1),
                new Random(1L)
        );

        assertEquals(List.of(), assignments);
    }

    private static RoleSelectionContext context(int playerCount) {
        return new RoleSelectionContext(null, playerCount, 1, Set.of(CREWMATE, IMPOSTOR, EXTRA_CREWMATE), 1L);
    }
}
