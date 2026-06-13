package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkVeteranBridgeTest {
    @Test
    public void recognizesSparkWatheVeteranRoleId() {
        assertTrue(AdvancedSparkVeteranBridge.isVeteranRole(AdvancedSparkSourceIds.sparkWathe("veteran")));
        assertFalse(AdvancedSparkVeteranBridge.isVeteranRole(AdvancedSparkSourceIds.advancedSpark("veteran")));
    }

    @Test
    public void veteranStabsCanOnlyBeUsedWhileStateHasCharges() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000555");
        state.initializeVeteranStabs(playerId, 1);

        assertTrue(AdvancedSparkVeteranBridge.tryUseVeteranStab(state, playerId));
        assertFalse(AdvancedSparkVeteranBridge.tryUseVeteranStab(state, playerId));
    }
}
