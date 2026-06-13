package dev.doctor4t.wathe.config.datapack;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * English: Lookup facade for Spark-wathe map enhancement configurations.
 * Chinese: Spark-wathe 地图增强配置查询门面。
 */
public final class MapEnhancementsConfigurationManager {
    private static final MapEnhancementsConfigurationManager INSTANCE = new MapEnhancementsConfigurationManager();

    private MapEnhancementsConfigurationManager() {
    }

    public static MapEnhancementsConfigurationManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    public MapEnhancementsConfiguration getConfiguration(Identifier dimensionId) {
        MapRegistryEntry directEntry = MapRegistry.getInstance().getMap(dimensionId);
        if (directEntry != null) {
            return directEntry.enhancements();
        }
        for (MapRegistryEntry entry : MapRegistry.getInstance().getMaps().values()) {
            if (entry.dimensionId().equals(dimensionId)) {
                return entry.enhancements();
            }
        }
        return null;
    }

    @Nullable
    public MapEnhancementsConfiguration getConfiguration() {
        return this.getConfiguration(Identifier.ofVanilla("overworld"));
    }

    public boolean hasConfiguration() {
        return MapRegistry.getInstance().getMapCount() > 0;
    }

    public boolean hasConfiguration(Identifier dimensionId) {
        return this.getConfiguration(dimensionId) != null;
    }
}
