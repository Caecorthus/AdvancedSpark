package dev.doctor4t.wathe.config.datapack;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapEnhancementsConfigurationTest {
    @Test
    public void exposesDefaultsAndRoomHelpers() {
        RoomConfig room = new RoomConfig(
                List.of(new RoomConfig.SpawnPoint(1.0, 2.0, 3.0, 4.0f, 5.0f)),
                Optional.of(3),
                Optional.of("Cabin")
        );
        MapEnhancementsConfiguration config = new MapEnhancementsConfiguration(
                List.of(room),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new MapEnhancementsConfiguration.GravityConfig(0.7f)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new MapEnhancementsConfiguration.SpecialRolesConfig(List.of("noellesroles:jester")))
        );

        assertEquals(1, config.getRoomCount());
        assertEquals(3, config.getTotalCapacity());
        assertEquals(room, config.getRoomConfig(0).orElseThrow());
        assertEquals(room.getSpawnPoint(0), config.getSpawnPointForPlayer(0, 4).orElseThrow());
        assertEquals(0.7f, config.getGravityOrDefault().gravityMultiplier());
        assertEquals(MapEnhancementsConfiguration.JumpConfig.DEFAULT, config.getJumpOrDefault());
        assertEquals(List.of("noellesroles:jester"), config.getSpecialRolesOrDefault().enabledRoles());
        assertTrue(config.getRoomConfig(9).isEmpty());
    }

    @Test
    public void registryStoresMapsInInsertionOrderAndFiltersByPlayerCount() {
        MapRegistry registry = new MapRegistry();
        MapRegistryEntry first = new MapRegistryEntry(
                Identifier.of("advancedspark_test", "first"),
                "First",
                Optional.empty(),
                MapEnhancementsConfiguration.EMPTY,
                2,
                4
        );
        MapRegistryEntry second = new MapRegistryEntry(
                Identifier.of("advancedspark_test", "second"),
                "Second",
                Optional.empty(),
                MapEnhancementsConfiguration.EMPTY,
                5,
                8
        );

        registry.register(Identifier.of("advancedspark_test", "first"), first);
        registry.register(Identifier.of("advancedspark_test", "second"), second);

        assertSame(first, registry.getMap(Identifier.of("advancedspark_test", "first")));
        assertEquals(List.of(first, second), registry.getAllMaps());
        assertEquals(List.of(first), registry.getEligibleMaps(3));
        assertFalse(second.isEligible(3));
        assertTrue(second.isEligible(5));
    }
}
