package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.RoleAppearanceCondition;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleSelectionContext;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRoleBuilderTest {
    private static final UUID CREWMATE = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID IMPOSTOR = UUID.fromString("00000000-0000-0000-0000-000000000102");

    @Test
    public void registersSparkRoleMetadataBesideOriginalWatheRole() {
        Identifier serialId = Identifier.of("advancedspark_test", "builder_serial_killer");
        Identifier bodyguardId = Identifier.of("advancedspark_test", "builder_bodyguard");
        AdvancedSparkRoles.unregister(serialId);
        AdvancedSparkRoles.unregister(bodyguardId);

        Role serialKiller = AdvancedSparkRoleBuilder.fromSparkRole(
                serialId,
                0x662222,
                false,
                true,
                Role.MoodType.FAKE,
                Integer.MAX_VALUE,
                true
        ).registerLocal();

        Role bodyguard = AdvancedSparkRoleBuilder.fromSparkRole(
                        bodyguardId,
                        0x4682FA,
                        true,
                        false,
                        Role.MoodType.REAL,
                        200,
                        false,
                        RoleAppearanceCondition.minKillers(2).and(context -> context.isRoleAssigned(serialKiller))
                )
                .mapSpecific(true)
                .special(true)
                .registerLocal();

        RoleSelectionContext oneKiller = context(1, Set.of(serialId));
        RoleSelectionContext twoKillers = context(2, Set.of(serialId));

        assertEquals(-1, serialKiller.getMaxSprintTime());
        assertEquals(bodyguardId, bodyguard.identifier());
        assertFalse(AdvancedSparkRoles.canAppear(bodyguardId, oneKiller));
        assertTrue(AdvancedSparkRoles.canAppear(bodyguardId, twoKillers));
        assertTrue(AdvancedSparkRoles.specialRoles().contains(bodyguardId));
        assertTrue(AdvancedSparkRoles.metadata(bodyguardId).orElseThrow().mapSpecific());
    }

    @Test
    public void selectionEvaluatesLaterFactionConditionsAfterEarlierAssignments() {
        Identifier serialId = Identifier.of("advancedspark_test", "ordered_serial_killer");
        Identifier bodyguardId = Identifier.of("advancedspark_test", "ordered_bodyguard");
        AdvancedSparkRoles.unregister(serialId);
        AdvancedSparkRoles.unregister(bodyguardId);

        Role serialKiller = AdvancedSparkRoleBuilder.fromSparkRole(
                serialId,
                0x662222,
                false,
                true,
                Role.MoodType.FAKE,
                Integer.MAX_VALUE,
                true
        ).registerLocal();
        AdvancedSparkRoleBuilder.fromSparkRole(
                        bodyguardId,
                        0x4682FA,
                        true,
                        false,
                        Role.MoodType.REAL,
                        200,
                        false,
                        context -> context.isRoleAssigned(serialKiller)
                )
                .registerLocal();

        List<AdvancedSparkRoles.RoleMetadata> roles = List.of(
                AdvancedSparkRoles.metadata(bodyguardId).orElseThrow(),
                AdvancedSparkRoles.metadata(serialId).orElseThrow()
        );
        List<AdvancedSparkRoleSelectionBridge.Candidate> candidates = List.of(
                new AdvancedSparkRoleSelectionBridge.Candidate(CREWMATE, Faction.CREWMATE),
                new AdvancedSparkRoleSelectionBridge.Candidate(IMPOSTOR, Faction.IMPOSTOR)
        );

        List<AdvancedSparkRoleSelectionBridge.Assignment> assignments = AdvancedSparkRoleSelectionBridge.selectAssignments(
                roles,
                candidates,
                context(2, Set.of()),
                new java.util.Random(1L)
        );

        assertEquals(
                Set.of(
                        new AdvancedSparkRoleSelectionBridge.Assignment(IMPOSTOR, serialId),
                        new AdvancedSparkRoleSelectionBridge.Assignment(CREWMATE, bodyguardId)
                ),
                Set.copyOf(assignments)
        );
    }

    private static RoleSelectionContext context(int targetKillerCount, Set<Identifier> assignedRoleIds) {
        return new RoleSelectionContext(
                Identifier.of("advancedspark_test", "map"),
                8,
                2,
                Set.of(CREWMATE, IMPOSTOR),
                1L,
                targetKillerCount,
                0,
                0,
                assignedRoleIds
        );
    }
}
