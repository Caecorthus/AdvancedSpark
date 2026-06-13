package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.util.ShopEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkShopEntryTest {
    @Test
    public void builderStoresSparkShopMetadata() {
        AdvancedSparkShopEntry entry = new AdvancedSparkShopEntry(
                        "poison_needle",
                        null,
                        null,
                        100,
                        ShopEntry.Type.WEAPON,
                        200,
                        40,
                        1,
                        null
                );

        assertEquals("poison_needle", AdvancedSparkShopEntries.id(entry));
        assertEquals(200, AdvancedSparkShopEntries.cooldownTicks(entry));
        assertEquals(40, AdvancedSparkShopEntries.initialCooldownTicks(entry));
        assertEquals(1, AdvancedSparkShopEntries.maxStock(entry));
        assertTrue(AdvancedSparkShopEntries.hasCooldown(entry));
        assertTrue(AdvancedSparkShopEntries.hasInitialCooldown(entry));
        assertTrue(AdvancedSparkShopEntries.hasStockLimit(entry));
    }

    @Test
    public void basicEntryHasNoCooldownOrStockLimit() {
        AdvancedSparkShopEntry entry = new AdvancedSparkShopEntry(
                "basic",
                null,
                null,
                50,
                ShopEntry.Type.TOOL,
                0,
                0,
                -1,
                null
        );

        assertEquals("basic", AdvancedSparkShopEntries.id(entry));
        assertEquals(0, AdvancedSparkShopEntries.cooldownTicks(entry));
        assertEquals(0, AdvancedSparkShopEntries.initialCooldownTicks(entry));
        assertEquals(-1, AdvancedSparkShopEntries.maxStock(entry));
        assertFalse(AdvancedSparkShopEntries.hasCooldown(entry));
        assertFalse(AdvancedSparkShopEntries.hasInitialCooldown(entry));
        assertFalse(AdvancedSparkShopEntries.hasStockLimit(entry));
    }
}
