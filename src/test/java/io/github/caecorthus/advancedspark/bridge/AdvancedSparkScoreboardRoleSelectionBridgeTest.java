package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.ScoreboardRoleSelectorComponent;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdvancedSparkScoreboardRoleSelectionBridgeTest {
    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void exposesForcedRoleBridgeMethods() {
        ScoreboardRoleSelectorComponent component = new ScoreboardRoleSelectorComponent(null, null);
        Role role = new Role(
                Identifier.of("advancedspark_test", "forced"),
                0xffffff,
                true,
                false,
                Role.MoodType.REAL,
                20,
                false
        );
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000301");

        assertThrows(IllegalStateException.class, () -> AdvancedSparkScoreboardRoleSelectionBridge.addForcedRole(component, role, playerId));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkScoreboardRoleSelectionBridge.getForcedForRole(component, role));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkScoreboardRoleSelectionBridge.getForcedRoleForPlayer(component, playerId));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkScoreboardRoleSelectionBridge.removeForcedRole(component, playerId));
    }
}
