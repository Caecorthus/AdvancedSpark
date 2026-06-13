package dev.doctor4t.wathe.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.option.KeyBinding;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Client hook for letting a normally suppressed key press through.
 * Chinese: 客户端钩子，用于放行原本会被屏蔽的按键。
 */
@Environment(EnvType.CLIENT)
public interface ShouldAllowSuppressedKey {
    Event<ShouldAllowSuppressedKey> EVENT = createArrayBacked(ShouldAllowSuppressedKey.class, listeners -> keyBinding -> {
        for (ShouldAllowSuppressedKey listener : listeners) {
            if (listener.shouldAllow(keyBinding)) {
                return true;
            }
        }
        return false;
    });

    boolean shouldAllow(KeyBinding keyBinding);
}
