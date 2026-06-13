package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;

import java.util.Objects;
import java.util.UUID;

/**
 * English: Applies runtime Wathe callbacks to AdvancedSpark round state.
 * Chinese: 将运行时 Wathe 回调应用到 AdvancedSpark 回合状态。
 */
public final class AdvancedSparkRoundStateBridge {
    private AdvancedSparkRoundStateBridge() {
    }

    public static void markKilled(AdvancedSparkGameState state, UUID victimId) {
        Objects.requireNonNull(state, "state");
        state.markDead(victimId);
    }

    public static void resetPlayer(AdvancedSparkGameState state, UUID playerId) {
        Objects.requireNonNull(state, "state");
        state.resetPlayer(playerId);
    }
}
