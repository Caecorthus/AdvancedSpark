package io.github.caecorthus.advancedspark.bridge;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkGameFunctionsTest {
    @Test
    public void forceKillFlagIsScopedToCurrentAction() {
        AtomicBoolean activeInsideAction = new AtomicBoolean(false);

        assertFalse(AdvancedSparkGameFunctions.isForceKillActive());

        AdvancedSparkGameFunctions.runWithForceKill(() ->
                activeInsideAction.set(AdvancedSparkGameFunctions.isForceKillActive())
        );

        assertTrue(activeInsideAction.get());
        assertFalse(AdvancedSparkGameFunctions.isForceKillActive());
    }

    @Test
    public void nullPlayersAreNeverAliveOrPlaying() {
        assertFalse(AdvancedSparkGameFunctions.isPlayerAliveAndSurvival(null));
        assertFalse(AdvancedSparkGameFunctions.isPlayerPlayingAndAlive(null));
    }
}
