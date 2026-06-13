package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe instinct highlight override event.
 * Chinese: Spark-wathe 本能高亮覆盖事件。
 */
public interface GetInstinctHighlight {
    Event<GetInstinctHighlight> EVENT = EventFactory.createArrayBacked(
            GetInstinctHighlight.class,
            callbacks -> target -> {
                HighlightResult best = null;
                for (GetInstinctHighlight callback : callbacks) {
                    HighlightResult result = callback.getHighlight(target);
                    if (result != null && (best == null || result.priority() > best.priority())) {
                        best = result;
                    }
                }
                return best;
            }
    );

    @Nullable
    HighlightResult getHighlight(Entity target);

    record HighlightResult(int color, boolean requiresKeybind, int priority) {
        public static final int PRIORITY_DEFAULT = 0;
        public static final int PRIORITY_HIGH = 100;
        public static final int PRIORITY_LOW = -100;

        public static HighlightResult withKeybind(int color) {
            return new HighlightResult(color, true, PRIORITY_DEFAULT);
        }

        public static HighlightResult withKeybind(int color, int priority) {
            return new HighlightResult(color, true, priority);
        }

        public static HighlightResult always(int color) {
            return new HighlightResult(color, false, PRIORITY_DEFAULT);
        }

        public static HighlightResult always(int color, int priority) {
            return new HighlightResult(color, false, priority);
        }

        public static HighlightResult skip() {
            return new HighlightResult(-1, false, PRIORITY_HIGH);
        }

        public boolean isSkip() {
            return this.color == -1;
        }
    }
}
