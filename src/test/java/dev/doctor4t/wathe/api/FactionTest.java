package dev.doctor4t.wathe.api;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactionTest {
    @Test
    public void derivesSparkFactionFromOriginalWatheRoleFlags() {
        Role civilian = role("civilian", true, false);
        Role killer = role("killer", false, true);
        Role neutral = role("neutral", false, false);

        assertEquals(Faction.NONE, Faction.fromRole(null));
        assertEquals(Faction.CIVILIAN, Faction.fromRole(civilian));
        assertEquals(Faction.KILLER, Faction.fromRole(killer));
        assertEquals(Faction.NEUTRAL, Faction.fromRole(neutral));
    }

    private static Role role(String path, boolean innocent, boolean killer) {
        return new Role(
                Identifier.of("advancedspark_test", path),
                0xffffff,
                innocent,
                killer,
                Role.MoodType.REAL,
                20,
                false
        );
    }
}
