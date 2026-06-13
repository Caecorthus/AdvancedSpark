package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Spark-wathe compatibility hook for overriding killer cohort visibility hints.
 * Chinese: 面向 Spark-wathe 的杀手同伙提示可见性覆盖事件。
 */
public interface ShouldShowCohort {
    Event<ShouldShowCohort> EVENT = createArrayBacked(ShouldShowCohort.class, listeners -> (viewer, target) -> {
        CohortResult bestResult = null;
        for (ShouldShowCohort listener : listeners) {
            CohortResult result = listener.getCohortResult(viewer, target);
            if (result != null && (bestResult == null || result.priority() > bestResult.priority())) {
                bestResult = result;
            }
        }
        return bestResult;
    });

    @Nullable
    CohortResult getCohortResult(PlayerEntity viewer, PlayerEntity target);

    record CohortResult(boolean shouldShow, int priority) {
        public static final int PRIORITY_DEFAULT = 0;
        public static final int PRIORITY_HIGH = 100;
        public static final int PRIORITY_LOW = -100;

        public static CohortResult show() {
            return new CohortResult(true, PRIORITY_DEFAULT);
        }

        public static CohortResult show(int priority) {
            return new CohortResult(true, priority);
        }

        public static CohortResult hide() {
            return new CohortResult(false, PRIORITY_HIGH);
        }

        public static CohortResult hide(int priority) {
            return new CohortResult(false, priority);
        }
    }
}
