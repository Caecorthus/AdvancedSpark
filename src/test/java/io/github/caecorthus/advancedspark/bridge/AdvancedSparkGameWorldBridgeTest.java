package io.github.caecorthus.advancedspark.bridge;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.api.Role;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleAppearanceCondition;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkGameWorldBridgeTest {
    @Test
    public void regularRolesAreEnabledUnlessExplicitlyDisabled() {
        Identifier roleId = Identifier.of("advancedspark_test", "world_regular_role");
        Role role = role(roleId);
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        AdvancedSparkRoles.unregister(roleId);
        AdvancedSparkRoles.register(roleId, Faction.CREWMATE, false, RoleAppearanceCondition.always());

        assertTrue(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));

        AdvancedSparkGameWorldBridge.setRoleEnabled(state, role, false);

        assertFalse(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));

        AdvancedSparkGameWorldBridge.setRoleEnabled(state, role, true);

        assertTrue(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));
    }

    @Test
    public void mapSpecificRolesNeedExplicitMapEnablement() {
        Identifier roleId = Identifier.of("advancedspark_test", "world_map_role");
        Role role = role(roleId);
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        AdvancedSparkRoles.unregister(roleId);
        AdvancedSparkRoles.register(roleId, Faction.CREWMATE, true, RoleAppearanceCondition.always());

        assertFalse(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));

        AdvancedSparkGameWorldBridge.setMapSpecificRoleEnabled(state, role, true);

        assertTrue(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));
    }

    @Test
    public void globalRoleDisableStillWinsOverWorldState() {
        Identifier roleId = Identifier.of("advancedspark_test", "world_globally_disabled_role");
        Role role = role(roleId);
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        AdvancedSparkRoles.unregister(roleId);
        AdvancedSparkRoles.register(roleId, Faction.CREWMATE, false, RoleAppearanceCondition.always());

        AdvancedSparkRoles.setEnabled(roleId, false);

        assertFalse(AdvancedSparkGameWorldBridge.isRoleEnabled(state, role));
    }

    @Test
    public void roomAssignmentsAndProfilesLiveInAdvancedSparkState() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000701");
        GameProfile profile = new GameProfile(playerId, "Attendant Target");

        AdvancedSparkGameWorldBridge.addPlayerToRoom(state, 2, "Room Two", playerId);
        AdvancedSparkGameWorldBridge.putGameProfile(state, profile);

        AdvancedSparkGameWorldBridge.RoomData room = AdvancedSparkGameWorldBridge.getRoom(state, 2).orElseThrow();
        assertEquals(2, room.getIndex());
        assertEquals("Room Two", room.getName());
        assertEquals(java.util.List.of(playerId), room.getPlayers());
        assertEquals(2, AdvancedSparkGameWorldBridge.getPlayerRoomIndex(state, playerId));
        assertEquals(profile, AdvancedSparkGameWorldBridge.getGameProfiles(state).get(playerId));
    }

    private static Role role(Identifier roleId) {
        return new Role(roleId, 0xFFFFFF, true, false, Role.MoodType.REAL, 20, false);
    }
}
