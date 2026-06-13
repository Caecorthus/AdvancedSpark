package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * English: Spark-wathe per-player shop build event shim.
 * Chinese: Spark-wathe 按玩家构建商店事件垫片。
 */
public interface BuildShopEntries {
    Event<BuildShopEntries> EVENT = EventFactory.createArrayBacked(
            BuildShopEntries.class,
            callbacks -> (player, context) -> {
                for (BuildShopEntries callback : callbacks) {
                    callback.buildEntries(player, context);
                }
            }
    );

    void buildEntries(PlayerEntity player, ShopContext context);

    class ShopContext {
        private final List<ShopEntry> entries;

        public ShopContext(List<ShopEntry> defaultEntries) {
            this.entries = new ArrayList<>(Objects.requireNonNull(defaultEntries, "defaultEntries"));
        }

        public List<ShopEntry> getEntries() {
            return this.entries;
        }

        public void addEntry(ShopEntry entry) {
            this.entries.add(entry);
        }

        public void addEntry(int index, ShopEntry entry) {
            this.entries.add(index, entry);
        }

        public ShopEntry removeEntry(int index) {
            return this.entries.remove(index);
        }

        public void clearEntries() {
            this.entries.clear();
        }

        public int size() {
            return this.entries.size();
        }

        public ShopEntry getEntry(int index) {
            return this.entries.get(index);
        }

        public ShopEntry setEntry(int index, ShopEntry entry) {
            return this.entries.set(index, entry);
        }
    }
}
