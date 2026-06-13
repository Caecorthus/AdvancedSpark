package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Tri-state hook for overriding whether money should be visible.
 * Chinese: 三态钩子，用于覆盖金币余额是否可见。
 */
public interface CanSeeMoney {
    Event<CanSeeMoney> EVENT = createArrayBacked(CanSeeMoney.class, listeners -> player -> {
        for (CanSeeMoney listener : listeners) {
            Result result = listener.canSee(player);
            if (result != null) {
                return result;
            }
        }
        return Result.DENY;
    });

    @Nullable
    Result canSee(PlayerEntity player);

    enum Result {
        ALLOW,
        DENY
    }
}
