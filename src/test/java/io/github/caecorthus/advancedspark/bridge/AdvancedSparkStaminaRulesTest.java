package io.github.caecorthus.advancedspark.bridge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkStaminaRulesTest {
    @Test
    public void finiteStaminaDrainsWhileSprintingAndRecoversWhileResting() {
        AdvancedSparkStaminaRules.State initial =
                new AdvancedSparkStaminaRules.State(10.0f, 20, false);

        AdvancedSparkStaminaRules.TickResult sprinting =
                AdvancedSparkStaminaRules.tick(initial, 20, true, true);
        AdvancedSparkStaminaRules.TickResult resting =
                AdvancedSparkStaminaRules.tick(sprinting.state(), 20, false, true);

        assertTrue(sprinting.sprintingAllowed());
        assertEquals(9.0f, sprinting.state().sprintingTicks());
        assertEquals(9.25f, resting.state().sprintingTicks());
    }

    @Test
    public void exhaustedPlayersCannotSprintUntilEnoughStaminaRecovers() {
        AdvancedSparkStaminaRules.State empty =
                new AdvancedSparkStaminaRules.State(0.0f, 200, false);
        AdvancedSparkStaminaRules.TickResult exhausted =
                AdvancedSparkStaminaRules.tick(empty, 200, true, true);
        AdvancedSparkStaminaRules.State recovered =
                new AdvancedSparkStaminaRules.State(
                        AdvancedSparkStaminaRules.EXHAUSTION_RECOVERY_THRESHOLD,
                        200,
                        true
                );
        AdvancedSparkStaminaRules.TickResult ableAgain =
                AdvancedSparkStaminaRules.tick(
                        AdvancedSparkStaminaRules.tick(recovered, 200, true, true).state(),
                        200,
                        true,
                        true
                );

        assertFalse(exhausted.sprintingAllowed());
        assertTrue(exhausted.state().exhausted());
        assertTrue(ableAgain.sprintingAllowed());
        assertFalse(ableAgain.state().exhausted());
    }

    @Test
    public void jumpCostConsumesFiniteStaminaButLeavesInfiniteStaminaUntouched() {
        AdvancedSparkStaminaRules.State finite =
                new AdvancedSparkStaminaRules.State(12.0f, 20, false);
        AdvancedSparkStaminaRules.State infinite =
                new AdvancedSparkStaminaRules.State(-1.0f, -1, false);

        assertEquals(7.0f, AdvancedSparkStaminaRules.spendForJump(finite, 5.0f).sprintingTicks());
        assertSame(infinite, AdvancedSparkStaminaRules.spendForJump(infinite, 5.0f));
    }
}
