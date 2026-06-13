package dev.doctor4t.wathe.api;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleAppearanceConditionTest {
    @Test
    public void supportsSparkConditionFactoriesAndAssignedRoleChecks() {
        Role role = new Role(
                Identifier.of("advancedspark_test", "assigned"),
                0xffffff,
                true,
                false,
                Role.MoodType.REAL,
                20,
                false
        );
        RoleSelectionContext context = new RoleSelectionContext(8, 2, 1, Set.of(role));

        assertTrue(RoleAppearanceCondition.minPlayers(8).shouldAppear(context));
        assertTrue(RoleAppearanceCondition.minKillers(2).shouldAppear(context));
        assertTrue(context.isRoleAssigned(role));
        assertFalse(RoleAppearanceCondition.maxNeutrals(0).shouldAppear(context));
    }

    @Test
    public void supportsSparkWatheSelectionContextShape() {
        RoleSelectionContext context = new RoleSelectionContext(null, null, List.of(), 5, 1, 2, 1);

        assertEquals(5, context.getTotalPlayerCount());
        assertEquals(1, context.getTargetKillerCount());
        assertEquals(2, context.getTargetNeutralCount());
        assertEquals(1, context.getTargetVigilanteCount());
        assertEquals(List.of(), context.getPlayers());
    }
}
