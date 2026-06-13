package io.github.caecorthus.advancedspark.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Shop callbacks for future buy/deny hooks.
 * Chinese: 面向未来购买与拦截逻辑的商店回调。
 */
public final class ShopEvents {
    public static final Event<BeforePurchase> BEFORE_PURCHASE = EventFactory.createArrayBacked(
            BeforePurchase.class,
            callbacks -> (player, itemId) -> {
                for (BeforePurchase callback : callbacks) {
                    ActionResult result = callback.beforePurchase(player, itemId);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    public static final Event<AfterPurchase> AFTER_PURCHASE = EventFactory.createArrayBacked(
            AfterPurchase.class,
            callbacks -> (player, itemId) -> {
                for (AfterPurchase callback : callbacks) {
                    callback.afterPurchase(player, itemId);
                }
            }
    );

    private ShopEvents() {
    }

    public static boolean shouldCancelVanillaPurchase(ActionResult result) {
        return result == ActionResult.FAIL;
    }

    public enum Decision {
        PASS,
        ALLOW,
        CANCEL
    }

    public static final class Bridge<TPlayer, TEntry> {
        private final List<BridgeShopBuild<TPlayer, TEntry>> shopBuildCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeBeforePurchase<TPlayer, TEntry>> beforePurchaseCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeAfterPurchase<TPlayer, TEntry>> afterPurchaseCallbacks = new CopyOnWriteArrayList<>();

        public void registerShopBuild(BridgeShopBuild<TPlayer, TEntry> callback) {
            shopBuildCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerBeforePurchase(BridgeBeforePurchase<TPlayer, TEntry> callback) {
            beforePurchaseCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerAfterPurchase(BridgeAfterPurchase<TPlayer, TEntry> callback) {
            afterPurchaseCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void dispatchShopBuild(TPlayer player, List<TEntry> entries) {
            Objects.requireNonNull(entries, "entries");
            for (BridgeShopBuild<TPlayer, TEntry> callback : shopBuildCallbacks) {
                callback.onShopBuild(player, entries);
            }
        }

        public Decision dispatchBeforePurchase(TPlayer player, TEntry entry) {
            for (BridgeBeforePurchase<TPlayer, TEntry> callback : beforePurchaseCallbacks) {
                Decision decision = Objects.requireNonNull(callback.beforePurchase(player, entry), "decision");
                if (decision != Decision.PASS) {
                    return decision;
                }
            }
            return Decision.PASS;
        }

        public Decision dispatchPurchase(TPlayer player, TEntry entry, Runnable vanillaPurchase) {
            Objects.requireNonNull(vanillaPurchase, "vanillaPurchase");
            Decision decision = dispatchBeforePurchase(player, entry);
            if (decision == Decision.CANCEL) {
                return decision;
            }
            vanillaPurchase.run();
            dispatchAfterPurchase(player, entry);
            return decision;
        }

        public void dispatchAfterPurchase(TPlayer player, TEntry entry) {
            for (BridgeAfterPurchase<TPlayer, TEntry> callback : afterPurchaseCallbacks) {
                callback.afterPurchase(player, entry);
            }
        }
    }

    @FunctionalInterface
    public interface BridgeShopBuild<TPlayer, TEntry> {
        void onShopBuild(TPlayer player, List<TEntry> entries);
    }

    @FunctionalInterface
    public interface BridgeBeforePurchase<TPlayer, TEntry> {
        Decision beforePurchase(TPlayer player, TEntry entry);
    }

    @FunctionalInterface
    public interface BridgeAfterPurchase<TPlayer, TEntry> {
        void afterPurchase(TPlayer player, TEntry entry);
    }

    @FunctionalInterface
    public interface BeforePurchase {
        ActionResult beforePurchase(ServerPlayerEntity player, Identifier itemId);
    }

    @FunctionalInterface
    public interface AfterPurchase {
        void afterPurchase(ServerPlayerEntity player, Identifier itemId);
    }
}
