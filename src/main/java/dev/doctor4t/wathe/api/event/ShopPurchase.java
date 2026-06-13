package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Spark-wathe compatibility hooks for shop purchase validation and tracking.
 * Chinese: 面向 Spark-wathe 的商店购买校验与追踪兼容事件。
 */
public final class ShopPurchase {
    private ShopPurchase() {
    }

    public static final Event<Before> BEFORE = createArrayBacked(Before.class, listeners -> (player, entry, index) -> {
        for (Before listener : listeners) {
            PurchaseResult result = listener.beforePurchase(player, entry, index);
            if (result != null) {
                return result;
            }
        }
        return null;
    });

    public static final Event<After> AFTER = createArrayBacked(After.class, listeners -> (player, entry, index, pricePaid) -> {
        for (After listener : listeners) {
            listener.afterPurchase(player, entry, index, pricePaid);
        }
    });

    @FunctionalInterface
    public interface Before {
        @Nullable
        PurchaseResult beforePurchase(ServerPlayerEntity player, ShopEntry entry, int index);
    }

    @FunctionalInterface
    public interface After {
        void afterPurchase(ServerPlayerEntity player, ShopEntry entry, int index, int pricePaid);
    }

    public record PurchaseResult(boolean allowed, int modifiedPrice, @Nullable String denyReason) {
        public static PurchaseResult allow() {
            return new PurchaseResult(true, -1, null);
        }

        public static PurchaseResult allow(int newPrice) {
            return new PurchaseResult(true, newPrice, null);
        }

        public static PurchaseResult deny() {
            return new PurchaseResult(false, -1, null);
        }

        public static PurchaseResult deny(String reason) {
            return new PurchaseResult(false, -1, reason);
        }

        public boolean hasModifiedPrice() {
            return modifiedPrice >= 0;
        }
    }
}
