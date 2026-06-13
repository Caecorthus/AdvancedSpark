package dev.doctor4t.wathe.cca;

import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration;
import dev.doctor4t.wathe.config.datapack.RoomConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapEnhancementsWorldComponentTest {
    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void delegatesToExplicitConfigurationWhenPresent() {
        RoomConfig room = new RoomConfig(
                List.of(new RoomConfig.SpawnPoint(1.0, 2.0, 3.0, 4.0f, 5.0f)),
                Optional.of(2),
                Optional.empty()
        );
        MapEnhancementsConfiguration config = new MapEnhancementsConfiguration(
                List.of(room),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new MapEnhancementsConfiguration.JumpConfig(true, 12.5f)),
                Optional.empty(),
                Optional.empty()
        );
        MapEnhancementsWorldComponent component = new MapEnhancementsWorldComponent(null);

        component.setConfiguration(config);

        assertEquals(1, component.getRoomCount());
        assertEquals(2, component.getTotalRoomCapacity());
        assertEquals(room, component.getRoomConfig(0).orElseThrow());
        assertEquals(12.5f, component.getJumpConfig().staminaCost());
    }
}
