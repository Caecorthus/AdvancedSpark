package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * English: Spark-style shop entry that can run inside original Wathe's ShopEntry list.
 * Chinese: 可放入原版 Wathe ShopEntry 列表中的 Spark 风格商店条目。
 */
public class AdvancedSparkShopEntry extends ShopEntry {
    private final String id;
    private final @Nullable ItemStack displayStack;
    private final @Nullable ItemStack actualStack;
    private final int cooldownTicks;
    private final int initialCooldownTicks;
    private final int maxStock;
    private final @Nullable Predicate<PlayerEntity> customBuyHandler;

    public AdvancedSparkShopEntry(ItemStack stack, int price, Type type) {
        this(AdvancedSparkShopEntries.generatedId(stack), stack, null, price, type, 0, 0, -1, null);
    }

    protected AdvancedSparkShopEntry(
            String id,
            ItemStack displayStack,
            @Nullable ItemStack actualStack,
            int price,
            Type type,
            int cooldownTicks,
            int initialCooldownTicks,
            int maxStock,
            @Nullable Predicate<PlayerEntity> customBuyHandler
    ) {
        super(displayStack, price, type);
        this.id = Objects.requireNonNull(id, "id");
        this.displayStack = displayStack;
        this.actualStack = actualStack;
        this.cooldownTicks = Math.max(0, cooldownTicks);
        this.initialCooldownTicks = Math.max(0, initialCooldownTicks);
        this.maxStock = maxStock;
        this.customBuyHandler = customBuyHandler;
    }

    public String id() {
        return this.id;
    }

    @Override
    public ItemStack stack() {
        return this.displayStack;
    }

    public ItemStack displayStack() {
        return this.displayStack;
    }

    public ItemStack getActualStack() {
        return this.actualStack != null ? this.actualStack : this.displayStack;
    }

    public int cooldownTicks() {
        return this.cooldownTicks;
    }

    public int initialCooldownTicks() {
        return this.initialCooldownTicks;
    }

    public int maxStock() {
        return this.maxStock;
    }

    public boolean hasStockLimit() {
        return this.maxStock > 0;
    }

    public boolean hasCooldown() {
        return this.cooldownTicks > 0;
    }

    public boolean hasInitialCooldown() {
        return this.initialCooldownTicks > 0;
    }

    @Override
    public boolean onBuy(@NotNull PlayerEntity player) {
        if (this.customBuyHandler != null) {
            return this.customBuyHandler.test(player);
        }
        return insertStackInFreeSlot(player, this.getActualStack().copy());
    }

    public static class Builder {
        private final String id;
        private final ItemStack displayStack;
        private final int price;
        private final Type type;
        private @Nullable ItemStack actualStack;
        private int cooldownTicks;
        private int initialCooldownTicks;
        private int maxStock = -1;
        private @Nullable Predicate<PlayerEntity> customBuyHandler;

        public Builder(String id, ItemStack displayStack, int price, Type type) {
            this.id = Objects.requireNonNull(id, "id");
            this.displayStack = Objects.requireNonNull(displayStack, "displayStack");
            this.price = price;
            this.type = Objects.requireNonNull(type, "type");
        }

        public Builder actualStack(ItemStack stack) {
            this.actualStack = Objects.requireNonNull(stack, "stack");
            return this;
        }

        public Builder cooldown(int ticks) {
            this.cooldownTicks = ticks;
            return this;
        }

        public Builder initialCooldown(int ticks) {
            this.initialCooldownTicks = ticks;
            return this;
        }

        public Builder stock(int max) {
            this.maxStock = max;
            return this;
        }

        public Builder onBuy(Predicate<PlayerEntity> handler) {
            this.customBuyHandler = Objects.requireNonNull(handler, "handler");
            return this;
        }

        public AdvancedSparkShopEntry build() {
            return new AdvancedSparkShopEntry(
                    id,
                    displayStack,
                    actualStack,
                    price,
                    type,
                    cooldownTicks,
                    initialCooldownTicks,
                    maxStock,
                    customBuyHandler
            );
        }
    }
}
