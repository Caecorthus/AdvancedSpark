package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.event.PlayerPoisoned;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * English: Migration-safe replacement for Spark-wathe poison component overloads.
 * Chinese: 面向迁移的 Spark-wathe 中毒组件重载替代层。
 */
public final class AdvancedSparkPoisonBridge {
    private AdvancedSparkPoisonBridge() {
    }

    public static boolean shouldApplyPoison(
            @Nullable PlayerEntity player,
            int ticks,
            @Nullable UUID poisoner,
            Identifier sourceId
    ) {
        Objects.requireNonNull(sourceId, "sourceId");
        PlayerPoisoned.PoisonResult result = PlayerPoisoned.BEFORE.invoker()
                .beforePlayerPoisoned(player, ticks, poisoner);
        return result == null || !result.cancelled();
    }

    public static void dispatchPoisonApplied(
            @Nullable PlayerEntity player,
            int ticks,
            @Nullable UUID poisoner,
            Identifier sourceId
    ) {
        Objects.requireNonNull(sourceId, "sourceId");
        PlayerPoisoned.AFTER.invoker().afterPlayerPoisoned(player, ticks, poisoner);
    }

    public static boolean applyPoison(
            PlayerPoisonComponent component,
            PlayerEntity player,
            int ticks,
            @Nullable UUID poisoner,
            Identifier sourceId
    ) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(player, "player");
        if (!shouldApplyPoison(player, ticks, poisoner, sourceId)) {
            return false;
        }
        component.setPoisonTicks(ticks, poisoner);
        dispatchPoisonApplied(player, ticks, poisoner, sourceId);
        return true;
    }
}
