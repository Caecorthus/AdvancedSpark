package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * English: Spark-wathe psycho-mode lifecycle event shim.
 * Chinese: Spark-wathe 疯魔模式生命周期事件垫片。
 */
public final class PsychoModeEvents {
    public static final Event<OnPsychoStart> ON_PSYCHO_START = EventFactory.createArrayBacked(
            OnPsychoStart.class,
            callbacks -> (player, trackActive) -> {
                for (OnPsychoStart callback : callbacks) {
                    callback.onPsychoStart(player, trackActive);
                }
            }
    );
    public static final Event<OnPsychoEnd> ON_PSYCHO_END = EventFactory.createArrayBacked(
            OnPsychoEnd.class,
            callbacks -> (player, trackActive) -> {
                for (OnPsychoEnd callback : callbacks) {
                    callback.onPsychoEnd(player, trackActive);
                }
            }
    );

    private PsychoModeEvents() {
    }

    @FunctionalInterface
    public interface OnPsychoStart {
        void onPsychoStart(ServerPlayerEntity player, boolean trackActive);
    }

    @FunctionalInterface
    public interface OnPsychoEnd {
        void onPsychoEnd(ServerPlayerEntity player, boolean trackActive);
    }
}
