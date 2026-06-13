package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.util.ShopEntry;

import java.util.List;

/**
 * English: Mixin-backed access to Spark-wathe shop cooldown and stock state.
 * Chinese: 由 mixin 支撑的 Spark-wathe 商店冷却与库存状态访问口。
 */
public interface AdvancedSparkShopStateAccess {
    void advancedspark$initializeShop(List<ShopEntry> entries);

    boolean advancedspark$isOnCooldown(String entryId);

    int advancedspark$getRemainingCooldown(String entryId);

    boolean advancedspark$isInStock(String entryId);

    int advancedspark$getRemainingStock(String entryId);

    int advancedspark$getMaxStock(String entryId);
}
