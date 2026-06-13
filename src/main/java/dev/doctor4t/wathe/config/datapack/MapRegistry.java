package dev.doctor4t.wathe.config.datapack;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * English: In-memory Spark-wathe map registry loaded by data-pack glue.
 * Chinese: 由数据包胶水层加载的 Spark-wathe 地图内存注册表。
 */
public class MapRegistry {
    private static final MapRegistry INSTANCE = new MapRegistry();

    private final Map<Identifier, MapRegistryEntry> maps = new LinkedHashMap<>();

    public static MapRegistry getInstance() {
        return INSTANCE;
    }

    public void register(Identifier id, MapRegistryEntry entry) {
        this.maps.put(id, entry);
    }

    public void clear() {
        this.maps.clear();
    }

    public Map<Identifier, MapRegistryEntry> getMaps() {
        return Collections.unmodifiableMap(this.maps);
    }

    public MapRegistryEntry getMap(Identifier id) {
        return this.maps.get(id);
    }

    public int getMapCount() {
        return this.maps.size();
    }

    public Set<Identifier> getMapIds() {
        return Collections.unmodifiableSet(this.maps.keySet());
    }

    public List<MapRegistryEntry> getEligibleMaps(int playerCount) {
        List<MapRegistryEntry> eligible = new ArrayList<>();
        for (MapRegistryEntry entry : this.maps.values()) {
            if (entry.isEligible(playerCount)) {
                eligible.add(entry);
            }
        }
        return eligible;
    }

    public List<MapRegistryEntry> getAllMaps() {
        return new ArrayList<>(this.maps.values());
    }
}
