package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.api.event.BuildShopEntries;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.util.ShopEntry;
import io.github.caecorthus.advancedspark.api.event.ShopEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * English: Applies AdvancedSpark shop-build callbacks to Wathe's mutable shop entry list.
 * Chinese: 将 AdvancedSpark 的商店构建回调应用到 Wathe 的可变商店条目列表。
 */
public final class AdvancedSparkShopBridge {
    private static final ShopEvents.Bridge<ServerPlayerEntity, ShopEntry> WATHE_SHOP_BUILD = new ShopEvents.Bridge<>();

    private AdvancedSparkShopBridge() {
    }

    public static void registerShopBuild(ShopEvents.BridgeShopBuild<ServerPlayerEntity, ShopEntry> callback) {
        WATHE_SHOP_BUILD.registerShopBuild(callback);
    }

    public static void buildWatheShopEntries() {
        dispatchShopBuild(null, GameConstants.SHOP_ENTRIES);
    }

    public static List<ShopEntry> buildShopEntries(PlayerEntity player, List<ShopEntry> defaultEntries) {
        BuildShopEntries.ShopContext context = new BuildShopEntries.ShopContext(defaultEntries);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            WATHE_SHOP_BUILD.dispatchShopBuild(serverPlayer, context.getEntries());
        }
        if (player != null) {
            BuildShopEntries.EVENT.invoker().buildEntries(player, context);
        }
        return List.copyOf(context.getEntries());
    }

    public static ActionResult dispatchBeforePurchase(@Nullable ServerPlayerEntity player, Identifier itemId) {
        return ShopEvents.BEFORE_PURCHASE.invoker().beforePurchase(player, Objects.requireNonNull(itemId, "itemId"));
    }

    public static void dispatchAfterPurchase(@Nullable ServerPlayerEntity player, Identifier itemId) {
        ShopEvents.AFTER_PURCHASE.invoker().afterPurchase(player, Objects.requireNonNull(itemId, "itemId"));
    }

    public static void dispatchShopBuild(ServerPlayerEntity player, List<ShopEntry> entries) {
        WATHE_SHOP_BUILD.dispatchShopBuild(player, entries);
    }

    public static void initializeShop(PlayerShopComponent component, List<ShopEntry> entries) {
        state(component).advancedspark$initializeShop(entries);
    }

    public static boolean isOnCooldown(PlayerShopComponent component, String entryId) {
        return state(component).advancedspark$isOnCooldown(entryId);
    }

    public static int getRemainingCooldown(PlayerShopComponent component, String entryId) {
        return state(component).advancedspark$getRemainingCooldown(entryId);
    }

    public static boolean isInStock(PlayerShopComponent component, String entryId) {
        return state(component).advancedspark$isInStock(entryId);
    }

    public static int getRemainingStock(PlayerShopComponent component, String entryId) {
        return state(component).advancedspark$getRemainingStock(entryId);
    }

    public static int getMaxStock(PlayerShopComponent component, String entryId) {
        return state(component).advancedspark$getMaxStock(entryId);
    }

    public static int getBalance(PlayerShopComponent component) {
        return Objects.requireNonNull(component, "component").balance;
    }

    public static void addToBalance(PlayerShopComponent component, int amount) {
        Objects.requireNonNull(component, "component").addToBalance(amount);
    }

    public static void setBalance(PlayerShopComponent component, int balance) {
        Objects.requireNonNull(component, "component").setBalance(balance);
    }

    public static boolean spendBalance(PlayerShopComponent component, int amount) {
        Objects.requireNonNull(component, "component");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        if (component.balance < amount) {
            return false;
        }
        component.setBalance(component.balance - amount);
        return true;
    }

    private static AdvancedSparkShopStateAccess state(PlayerShopComponent component) {
        Objects.requireNonNull(component, "component");
        if (component instanceof AdvancedSparkShopStateAccess access) {
            return access;
        }
        throw new IllegalStateException("AdvancedSpark shop state mixin is not applied");
    }
}
