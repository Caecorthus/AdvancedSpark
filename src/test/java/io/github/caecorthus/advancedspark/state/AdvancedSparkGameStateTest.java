package io.github.caecorthus.advancedspark.state;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkGameStateTest {
    @Test
    public void resetRoundClearsRoundScopedState() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        Identifier roleId = Identifier.of("advancedspark_test", "role");
        Identifier winnerId = Identifier.of("advancedspark_test", "neutral");
        UUID playerId = UUID.randomUUID();

        state.setRoleEnabled(roleId, true);
        state.markDead(playerId);
        state.recordMapVote(playerId, Identifier.of("advancedspark_test", "map"));
        state.setNeutralWinner(winnerId);
        state.setStaminaPlaceholder(playerId, true);
        state.setVeteranPlaceholder(playerId, true);

        state.resetRound();

        assertTrue(state.isRoleEnabled(roleId));
        assertFalse(state.isDead(playerId));
        assertTrue(state.mapVotes().isEmpty());
        assertTrue(state.neutralWinner().isEmpty());
        assertTrue(state.playerRoundStates().isEmpty());
    }

    @Test
    public void resetPlayerClearsOnlyPlayerScopedState() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID resetPlayer = UUID.fromString("00000000-0000-0000-0000-000000000111");
        UUID otherPlayer = UUID.fromString("00000000-0000-0000-0000-000000000222");
        Identifier mapId = Identifier.of("advancedspark_test", "map");

        state.markDead(resetPlayer);
        state.markDead(otherPlayer);
        state.recordMapVote(resetPlayer, mapId);
        state.setStaminaPlaceholder(resetPlayer, true);
        state.setNeutralWinner(Identifier.of("advancedspark_test", "neutral"));

        state.resetPlayer(resetPlayer);

        assertFalse(state.isDead(resetPlayer));
        assertTrue(state.isDead(otherPlayer));
        assertFalse(state.mapVotes().containsKey(resetPlayer));
        assertFalse(state.playerRoundStates().containsKey(resetPlayer));
        assertTrue(state.neutralWinner().isPresent());
    }

    @Test
    public void veteranStabsCanBeInitializedAndConsumed() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000333");

        state.initializeVeteranStabs(playerId, 2);

        assertEquals(2, state.playerState(playerId).veteranStabsRemaining());
        assertTrue(state.useVeteranStab(playerId));
        assertEquals(1, state.playerState(playerId).veteranStabsRemaining());
        assertTrue(state.useVeteranStab(playerId));
        assertFalse(state.useVeteranStab(playerId));
        assertEquals(0, state.playerState(playerId).veteranStabsRemaining());
    }

    @Test
    public void staminaTicksAreClampedToMaxTicks() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000444");

        state.setStaminaTicks(playerId, 40, 20);

        assertEquals(20, state.playerState(playerId).staminaTicksRemaining());
        assertEquals(20, state.playerState(playerId).maxStaminaTicks());
        assertTrue(state.playerState(playerId).staminaActive());
    }
}
