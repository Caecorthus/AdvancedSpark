package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRoleAnnouncementBridgeTest {
    @Test
    public void registersAnnouncementIndexForAdvancedSparkRoles() {
        Role role = new Role(
                AdvancedSparkConstants.id("announcement_test"),
                0x123456,
                true,
                false,
                Role.MoodType.REAL,
                20,
                false
        );

        int index = AdvancedSparkRoleAnnouncementBridge.register(role);

        assertEquals(index, AdvancedSparkRoleAnnouncementBridge.register(role));
        OptionalInt storedIndex = AdvancedSparkRoleAnnouncementBridge.announcementIndex(role.identifier());
        assertTrue(storedIndex.isPresent());
        assertEquals(index, storedIndex.getAsInt());
    }

    @Test
    public void unknownRolesDoNotHaveAdvancedSparkAnnouncementIndices() {
        Identifier roleId = Identifier.of("advancedspark_test", "missing_role");

        assertTrue(AdvancedSparkRoleAnnouncementBridge.announcementIndex(roleId).isEmpty());
    }
}
