package io.github.caecorthus.advancedspark.api.role;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRolesTest {
    @Test
    public void registeredRoleHonorsEnabledStateAndAppearanceCondition() {
        Identifier roleId = Identifier.of("advancedspark_test", "conditional_role");
        RoleSelectionContext smallLobby = new RoleSelectionContext(null, 3, 1, Set.of(), 1L);
        RoleSelectionContext fullLobby = new RoleSelectionContext(null, 5, 1, Set.of(), 1L);

        AdvancedSparkRoles.unregister(roleId);
        AdvancedSparkRoles.register(roleId, Faction.NEUTRAL, false, RoleAppearanceCondition.minPlayers(5));

        assertFalse(AdvancedSparkRoles.canAppear(roleId, smallLobby));
        assertTrue(AdvancedSparkRoles.canAppear(roleId, fullLobby));

        AdvancedSparkRoles.setEnabled(roleId, false);

        assertFalse(AdvancedSparkRoles.canAppear(roleId, fullLobby));
    }
}
