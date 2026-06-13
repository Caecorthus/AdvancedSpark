package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.content.AdvancedSparkBaseRoles;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * English: Applies Spark-style Veteran limited knife uses.
 * Chinese: 应用 Spark 风格的老兵有限刀击次数。
 */
public final class AdvancedSparkVeteranBridge {
    private AdvancedSparkVeteranBridge() {
    }

    public static boolean isVeteranRole(Identifier roleId) {
        return AdvancedSparkBaseRoles.VETERAN.identifier().equals(roleId);
    }

    public static boolean tryUseVeteranStab(AdvancedSparkGameState state, UUID playerId) {
        Objects.requireNonNull(state, "state");
        return state.useVeteranStab(playerId);
    }
}
