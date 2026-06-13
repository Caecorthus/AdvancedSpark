package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.event.KillPlayer;
import io.github.caecorthus.advancedspark.component.KillHistoryWorldComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * English: Connects Spark-wathe kill events to the migrated kill-history component.
 * Chinese: 将 Spark-wathe 击杀事件接入已迁移的击杀历史组件。
 */
public final class AdvancedSparkKillHistoryBridge {
    private static boolean initialized;

    private AdvancedSparkKillHistoryBridge() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        KillPlayer.AFTER.register(AdvancedSparkKillHistoryBridge::recordServerKill);
    }

    public static <TPlayer> boolean recordKill(
            Bridge<TPlayer> bridge,
            TPlayer victim,
            @Nullable TPlayer killer,
            Identifier deathReason
    ) {
        Objects.requireNonNull(bridge, "bridge");
        Objects.requireNonNull(victim, "victim");
        Objects.requireNonNull(deathReason, "deathReason");
        if (killer == null) {
            return false;
        }

        KillHistoryWorldComponent history = Objects.requireNonNull(bridge.history(victim), "history");
        history.recordKill(
                bridge.uuid(killer),
                bridge.uuid(victim),
                deathReason,
                bridge.currentTick(victim)
        );
        return true;
    }

    private static void recordServerKill(
            ServerPlayerEntity victim,
            @Nullable ServerPlayerEntity killer,
            Identifier deathReason
    ) {
        recordKill(ServerBridge.INSTANCE, victim, killer, deathReason);
    }

    public interface Bridge<TPlayer> {
        UUID uuid(TPlayer player);

        long currentTick(TPlayer player);

        KillHistoryWorldComponent history(TPlayer player);
    }

    private enum ServerBridge implements Bridge<ServerPlayerEntity> {
        INSTANCE;

        @Override
        public UUID uuid(ServerPlayerEntity player) {
            return player.getUuid();
        }

        @Override
        public long currentTick(ServerPlayerEntity player) {
            return player.getWorld().getTime();
        }

        @Override
        public KillHistoryWorldComponent history(ServerPlayerEntity player) {
            return KillHistoryWorldComponent.KEY.get(player.getWorld());
        }
    }
}
