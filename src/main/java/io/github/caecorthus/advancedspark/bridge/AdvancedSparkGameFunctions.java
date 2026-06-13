package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * English: Migration-safe wrappers for Spark-wathe GameFunctions additions.
 * Chinese: 面向 Spark-wathe GameFunctions 新增能力的迁移安全包装层。
 */
public final class AdvancedSparkGameFunctions {
    private static final ThreadLocal<Boolean> FORCE_KILL = ThreadLocal.withInitial(() -> false);

    private AdvancedSparkGameFunctions() {
    }

    public static boolean isPlayerAliveAndSurvival(@Nullable PlayerEntity player) {
        return GameFunctions.isPlayerAliveAndSurvival(player);
    }

    public static boolean isPlayerPlayingAndAlive(@Nullable PlayerEntity player) {
        if (player == null || player.getServer() == null || !isPlayerAliveAndSurvival(player)) {
            return false;
        }
        GameWorldComponent gameComponent = GameWorldComponent.KEY.get(player.getWorld());
        return gameComponent.isRunning()
                && gameComponent.getRole(player) != null
                && !AdvancedSparkComponents.get(player.getServer()).isDead(player.getUuid());
    }

    public static void killPlayer(
            ServerPlayerEntity victim,
            boolean spawnBody,
            @Nullable ServerPlayerEntity killer
    ) {
        killPlayer(victim, spawnBody, killer, GameConstants.DeathReasons.GENERIC, false);
    }

    public static void killPlayer(
            ServerPlayerEntity victim,
            boolean spawnBody,
            @Nullable ServerPlayerEntity killer,
            Identifier deathReason
    ) {
        killPlayer(victim, spawnBody, killer, deathReason, false);
    }

    public static void killPlayer(
            ServerPlayerEntity victim,
            boolean spawnBody,
            @Nullable ServerPlayerEntity killer,
            Identifier deathReason,
            boolean force
    ) {
        Objects.requireNonNull(victim, "victim");
        Objects.requireNonNull(deathReason, "deathReason");
        if (!force) {
            GameFunctions.killPlayer(victim, spawnBody, killer, deathReason);
            return;
        }

        runWithForceKill(() -> {
            PlayerPsychoComponent.KEY.get(victim).stopPsycho();
            GameFunctions.killPlayer(victim, spawnBody, killer, deathReason);
        });
    }

    public static boolean isForceKillActive() {
        return FORCE_KILL.get();
    }

    static void runWithForceKill(Runnable action) {
        Objects.requireNonNull(action, "action");
        boolean previous = FORCE_KILL.get();
        FORCE_KILL.set(true);
        try {
            action.run();
        } finally {
            FORCE_KILL.set(previous);
        }
    }
}
