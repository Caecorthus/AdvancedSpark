package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.Entity;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: AND-style hook for deciding whether a body can be targeted.
 * Chinese: AND 规则钩子，用于判断尸体是否可被准星选中。
 */
public interface CanTargetBody {
    Event<CanTargetBody> EVENT = createArrayBacked(CanTargetBody.class, listeners -> (player, body) -> {
        for (CanTargetBody listener : listeners) {
            if (!listener.canTarget(player, body)) {
                return false;
            }
        }
        return true;
    });

    boolean canTarget(Entity player, PlayerBodyEntity body);
}
