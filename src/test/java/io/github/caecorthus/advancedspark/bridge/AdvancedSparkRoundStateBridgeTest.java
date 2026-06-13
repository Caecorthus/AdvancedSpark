package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRoundStateBridgeTest {
    @Test
    public void killedPlayersAreMarkedDeadInRoundState() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID victimId = UUID.fromString("00000000-0000-0000-0000-000000000777");

        AdvancedSparkRoundStateBridge.markKilled(state, victimId);

        assertTrue(state.isDead(victimId));
    }

    @Test
    public void resetPlayersAreMarkedAliveInRoundState() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000888");
        state.markDead(playerId);

        AdvancedSparkRoundStateBridge.resetPlayer(state, playerId);

        assertFalse(state.isDead(playerId));
    }
}
