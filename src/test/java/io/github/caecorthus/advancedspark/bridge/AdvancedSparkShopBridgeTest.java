package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.util.ShopEntry;
import io.github.caecorthus.advancedspark.api.event.ShopEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkShopBridgeTest {
    @Test
    public void dispatchesRuntimeAfterPurchaseEvent() {
        Identifier itemId = Identifier.of("advancedspark_test", "runtime_after_purchase");
        List<Identifier> purchasedItems = new ArrayList<>();

        ShopEvents.AFTER_PURCHASE.register((player, purchasedItemId) -> {
            if (itemId.equals(purchasedItemId)) {
                purchasedItems.add(purchasedItemId);
            }
        });

        AdvancedSparkShopBridge.dispatchAfterPurchase(null, itemId);

        assertEquals(List.of(itemId), purchasedItems);
    }

    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void managesWatheShopBalanceThroughBridge() {
        PlayerShopComponent component = new PlayerShopComponent(null);

        AdvancedSparkShopBridge.setBalance(component, 10);
        AdvancedSparkShopBridge.addToBalance(component, 5);

        assertEquals(15, AdvancedSparkShopBridge.getBalance(component));
        assertTrue(AdvancedSparkShopBridge.spendBalance(component, 7));
        assertEquals(8, AdvancedSparkShopBridge.getBalance(component));
        assertFalse(AdvancedSparkShopBridge.spendBalance(component, 9));
        assertEquals(8, AdvancedSparkShopBridge.getBalance(component));
    }

    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void exposesSparkShopStateBridgeMethods() {
        PlayerShopComponent component = new PlayerShopComponent(null);
        ShopEntry entry = new AdvancedSparkShopEntry.Builder(
                "test_entry",
                new ItemStack(Items.STICK),
                1,
                ShopEntry.Type.TOOL
        ).initialCooldown(20).stock(2).build();

        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.initializeShop(component, List.of(entry)));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.isOnCooldown(component, "test_entry"));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.getRemainingCooldown(component, "test_entry"));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.isInStock(component, "test_entry"));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.getRemainingStock(component, "test_entry"));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkShopBridge.getMaxStock(component, "test_entry"));
    }
}
