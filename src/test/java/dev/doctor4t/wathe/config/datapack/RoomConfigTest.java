package dev.doctor4t.wathe.config.datapack;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomConfigTest {
    @Test
    public void spawnPointsCycleByPlayerIndex() {
        RoomConfig room = new RoomConfig(
                List.of(
                        new RoomConfig.SpawnPoint(1.0, 2.0, 3.0, 4.0f, 5.0f),
                        new RoomConfig.SpawnPoint(6.0, 7.0, 8.0, 9.0f, 10.0f)
                ),
                Optional.empty(),
                Optional.empty()
        );

        assertEquals(new RoomConfig.SpawnPoint(1.0, 2.0, 3.0, 4.0f, 5.0f), room.getSpawnPoint(2));
        assertEquals("Room 3", room.getName(3));
        assertEquals(2, room.getMaxPlayers());
    }
}
