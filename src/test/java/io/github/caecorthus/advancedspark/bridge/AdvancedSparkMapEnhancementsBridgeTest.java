package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.MapEnhancementsWorldComponent;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration;
import dev.doctor4t.wathe.config.datapack.RoomConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSparkMapEnhancementsBridgeTest {
    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void exposesMapEnhancementComponentConfiguration() {
        RoomConfig room = new RoomConfig(
                List.of(new RoomConfig.SpawnPoint(1.0, 2.0, 3.0, 4.0f, 5.0f)),
                Optional.of(4),
                Optional.of("Bridge Room")
        );
        MapEnhancementsWorldComponent component = new MapEnhancementsWorldComponent(null);

        AdvancedSparkMapEnhancementsBridge.setConfiguration(component, new MapEnhancementsConfiguration(
                List.of(room),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new MapEnhancementsConfiguration.MovementConfig(0.5f, 0.75f)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ));

        assertEquals(1, AdvancedSparkMapEnhancementsBridge.getRoomCount(component));
        assertEquals(4, AdvancedSparkMapEnhancementsBridge.getTotalRoomCapacity(component));
        assertEquals(room, AdvancedSparkMapEnhancementsBridge.getRoomConfig(component, 0).orElseThrow());
        assertEquals(0.5f, AdvancedSparkMapEnhancementsBridge.getMovementConfig(component).walkSpeedMultiplier());
    }
}
