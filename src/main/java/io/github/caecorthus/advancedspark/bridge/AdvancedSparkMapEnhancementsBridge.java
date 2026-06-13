package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.MapEnhancementsWorldComponent;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.AmbienceConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.CameraShakeConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.FogConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.GravityConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.InteractionBlacklistConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.JumpConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.MovementConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.SceneryConfig;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfiguration.VisibilityConfig;
import dev.doctor4t.wathe.config.datapack.RoomConfig;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * English: AdvancedSpark-facing bridge for Spark-wathe map enhancement component APIs.
 * Chinese: 面向 AdvancedSpark 的 Spark-wathe 地图增强组件 API 桥接层。
 */
public final class AdvancedSparkMapEnhancementsBridge {
    private AdvancedSparkMapEnhancementsBridge() {
    }

    public static void setConfiguration(MapEnhancementsWorldComponent component, MapEnhancementsConfiguration configuration) {
        Objects.requireNonNull(component, "component").setConfiguration(Objects.requireNonNull(configuration, "configuration"));
    }

    public static SceneryConfig getSceneryConfig(World world) {
        return getSceneryConfig(component(world));
    }

    public static SceneryConfig getSceneryConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getSceneryConfig();
    }

    public static VisibilityConfig getVisibilityConfig(World world) {
        return getVisibilityConfig(component(world));
    }

    public static VisibilityConfig getVisibilityConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getVisibilityConfig();
    }

    public static FogConfig getFogConfig(World world) {
        return getFogConfig(component(world));
    }

    public static FogConfig getFogConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getFogConfig();
    }

    public static CameraShakeConfig getCameraShakeConfig(World world) {
        return getCameraShakeConfig(component(world));
    }

    public static CameraShakeConfig getCameraShakeConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getCameraShakeConfig();
    }

    public static InteractionBlacklistConfig getInteractionBlacklistConfig(World world) {
        return getInteractionBlacklistConfig(component(world));
    }

    public static InteractionBlacklistConfig getInteractionBlacklistConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getInteractionBlacklistConfig();
    }

    public static GravityConfig getGravityConfig(World world) {
        return getGravityConfig(component(world));
    }

    public static GravityConfig getGravityConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getGravityConfig();
    }

    public static MovementConfig getMovementConfig(World world) {
        return getMovementConfig(component(world));
    }

    public static MovementConfig getMovementConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getMovementConfig();
    }

    public static JumpConfig getJumpConfig(World world) {
        return getJumpConfig(component(world));
    }

    public static JumpConfig getJumpConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getJumpConfig();
    }

    public static AmbienceConfig getAmbienceConfig(World world) {
        return getAmbienceConfig(component(world));
    }

    public static AmbienceConfig getAmbienceConfig(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getAmbienceConfig();
    }

    public static List<String> getEnabledSpecialRoles(World world) {
        return getEnabledSpecialRoles(component(world));
    }

    public static List<String> getEnabledSpecialRoles(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getEnabledSpecialRoles();
    }

    public static int getRoomCount(World world) {
        return getRoomCount(component(world));
    }

    public static int getRoomCount(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getRoomCount();
    }

    public static Optional<RoomConfig> getRoomConfig(World world, int roomNumber) {
        return getRoomConfig(component(world), roomNumber);
    }

    public static Optional<RoomConfig> getRoomConfig(MapEnhancementsWorldComponent component, int roomNumber) {
        return Objects.requireNonNull(component, "component").getRoomConfig(roomNumber);
    }

    public static Optional<RoomConfig.SpawnPoint> getSpawnPointForPlayer(World world, int roomNumber, int playerIndexInRoom) {
        return getSpawnPointForPlayer(component(world), roomNumber, playerIndexInRoom);
    }

    public static Optional<RoomConfig.SpawnPoint> getSpawnPointForPlayer(
            MapEnhancementsWorldComponent component,
            int roomNumber,
            int playerIndexInRoom
    ) {
        return Objects.requireNonNull(component, "component").getSpawnPointForPlayer(roomNumber, playerIndexInRoom);
    }

    public static int getTotalRoomCapacity(World world) {
        return getTotalRoomCapacity(component(world));
    }

    public static int getTotalRoomCapacity(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").getTotalRoomCapacity();
    }

    public static boolean hasAreaConfiguration(World world) {
        return hasAreaConfiguration(component(world));
    }

    public static boolean hasAreaConfiguration(MapEnhancementsWorldComponent component) {
        return Objects.requireNonNull(component, "component").hasAreaConfiguration();
    }

    private static MapEnhancementsWorldComponent component(World world) {
        return MapEnhancementsWorldComponent.KEY.get(Objects.requireNonNull(world, "world"));
    }
}
