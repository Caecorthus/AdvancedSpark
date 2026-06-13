package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.Entity;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: OR-style hook for allowing a viewer to see a body role label.
 * Chinese: OR 规则钩子，用于允许观察者看到尸体职业标签。
 */
public interface CanSeeBodyRole {
    Event<CanSeeBodyRole> EVENT = createArrayBacked(CanSeeBodyRole.class, listeners -> player -> {
        for (CanSeeBodyRole listener : listeners) {
            if (listener.canSee(player)) {
                return true;
            }
        }
        return false;
    });

    boolean canSee(Entity player);
}
