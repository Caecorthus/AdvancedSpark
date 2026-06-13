package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * English: Helper methods for migrated Spark/NoellesRoles shop code.
 * Chinese: 面向已迁移 Spark/NoellesRoles 商店代码的辅助方法。
 */
public final class AdvancedSparkShopEntries {
    private AdvancedSparkShopEntries() {
    }

    public static AdvancedSparkShopEntry entry(ItemStack stack, int price, ShopEntry.Type type) {
        return new AdvancedSparkShopEntry(stack, price, type);
    }

    public static AdvancedSparkShopEntry.Builder builder(String id, ItemStack stack, int price, ShopEntry.Type type) {
        return new AdvancedSparkShopEntry.Builder(id, stack, price, type);
    }

    public static String id(ShopEntry entry) {
        Objects.requireNonNull(entry, "entry");
        if (entry instanceof AdvancedSparkShopEntry advancedEntry) {
            return advancedEntry.id();
        }
        return generatedId(entry.stack());
    }

    public static int cooldownTicks(ShopEntry entry) {
        return entry instanceof AdvancedSparkShopEntry advancedEntry ? advancedEntry.cooldownTicks() : 0;
    }

    public static int initialCooldownTicks(ShopEntry entry) {
        return entry instanceof AdvancedSparkShopEntry advancedEntry ? advancedEntry.initialCooldownTicks() : 0;
    }

    public static int maxStock(ShopEntry entry) {
        return entry instanceof AdvancedSparkShopEntry advancedEntry ? advancedEntry.maxStock() : -1;
    }

    public static boolean hasCooldown(ShopEntry entry) {
        return cooldownTicks(entry) > 0;
    }

    public static boolean hasInitialCooldown(ShopEntry entry) {
        return initialCooldownTicks(entry) > 0;
    }

    public static boolean hasStockLimit(ShopEntry entry) {
        return maxStock(entry) > 0;
    }

    static String generatedId(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        if (stack.isEmpty()) {
            return "empty";
        }
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        if (itemId != null) {
            return itemId.getPath();
        }
        return stack.getItem().toString().replace(':', '_');
    }
}
