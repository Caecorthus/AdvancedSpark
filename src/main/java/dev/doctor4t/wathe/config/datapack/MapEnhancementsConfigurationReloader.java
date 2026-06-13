package dev.doctor4t.wathe.config.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.doctor4t.wathe.Wathe;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * English: Loads Spark-wathe map enhancement data-pack configs into MapRegistry.
 * Chinese: 将 Spark-wathe 地图增强数据包配置加载进 MapRegistry。
 */
public class MapEnhancementsConfigurationReloader implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String LEGACY_DATA_PATH = "areas";
    private static final String MAPS_DATA_PATH = "maps";

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new MapEnhancementsConfigurationReloader());
        Wathe.LOGGER.info("Registered AdvancedSpark map configuration reloader");
    }

    @Override
    public Identifier getFabricId() {
        return Wathe.id("area_configuration");
    }

    @Override
    public void reload(ResourceManager manager) {
        MapRegistry.getInstance().clear();
        this.loadMapRegistryEntries(manager);
        if (MapRegistry.getInstance().getMapCount() == 0) {
            this.loadLegacyAreaConfiguration(manager);
        }
        Wathe.LOGGER.info("AdvancedSpark map registry loaded: {} maps registered", MapRegistry.getInstance().getMapCount());
    }

    private void loadMapRegistryEntries(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(MAPS_DATA_PATH, id -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            if (!Wathe.MOD_ID.equals(resourceId.getNamespace())) {
                continue;
            }
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                Optional<MapRegistryEntry> result = MapRegistryEntry.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(error -> Wathe.LOGGER.error("Failed to parse map config {}: {}", resourceId, error));
                result.ifPresent(map -> MapRegistry.getInstance().register(mapIdFromResource(resourceId), map));
            } catch (Exception exception) {
                Wathe.LOGGER.error("Error loading map config from {}", resourceId, exception);
            }
        }
    }

    private void loadLegacyAreaConfiguration(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(LEGACY_DATA_PATH, id -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            if (!Wathe.MOD_ID.equals(resourceId.getNamespace())) {
                continue;
            }
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                Optional<MapEnhancementsConfiguration> result = MapEnhancementsConfiguration.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(error -> Wathe.LOGGER.error("Failed to parse legacy area config {}: {}", resourceId, error));
                if (result.isPresent()) {
                    MapRegistry.getInstance().register(
                            Wathe.id("legacy_overworld"),
                            new MapRegistryEntry(
                                    Identifier.ofVanilla("overworld"),
                                    "Overworld",
                                    Optional.empty(),
                                    result.get(),
                                    0,
                                    100
                            )
                    );
                    return;
                }
            } catch (Exception exception) {
                Wathe.LOGGER.error("Error loading legacy area config from {}", resourceId, exception);
            }
        }
    }

    private static Identifier mapIdFromResource(Identifier resourceId) {
        String path = resourceId.getPath();
        String name = path.substring(MAPS_DATA_PATH.length() + 1, path.length() - ".json".length());
        return Identifier.of(resourceId.getNamespace(), name);
    }
}
