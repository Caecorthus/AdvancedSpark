package dev.doctor4t.wathe.cca;

import dev.doctor4t.wathe.Wathe;
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
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfigurationManager;
import dev.doctor4t.wathe.config.datapack.RoomConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * English: Minimal Spark-wathe map enhancement component shim.
 * Chinese: 最小 Spark-wathe 地图增强组件垫片。
 */
public class MapEnhancementsWorldComponent implements AutoSyncedComponent {
    public static final ComponentKey<MapEnhancementsWorldComponent> KEY =
            ComponentRegistry.getOrCreate(Wathe.id("map_enhancements"), MapEnhancementsWorldComponent.class);

    private final @Nullable World world;
    private @Nullable MapEnhancementsConfiguration configuration;

    public MapEnhancementsWorldComponent(@Nullable World world) {
        this.world = world;
    }

    public void sync() {
        if (this.world == null) {
            return;
        }
        KEY.sync(this.world);
    }

    public void setConfiguration(MapEnhancementsConfiguration configuration) {
        this.configuration = configuration;
        this.sync();
    }

    public void setRooms(List<RoomConfig> rooms) {
        this.configuration = new MapEnhancementsConfiguration(
                List.copyOf(rooms),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        this.sync();
    }

    public List<RoomConfig> getRooms() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? List.of() : config.rooms();
    }

    public SceneryConfig getSceneryConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? SceneryConfig.DEFAULT : config.getSceneryOrDefault();
    }

    public VisibilityConfig getVisibilityConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? VisibilityConfig.DEFAULT : config.getVisibilityOrDefault();
    }

    public FogConfig getFogConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? FogConfig.DEFAULT : config.getFogOrDefault();
    }

    public CameraShakeConfig getCameraShakeConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? CameraShakeConfig.DEFAULT : config.getCameraShakeOrDefault();
    }

    public InteractionBlacklistConfig getInteractionBlacklistConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? InteractionBlacklistConfig.DEFAULT : config.getInteractionBlacklistOrDefault();
    }

    public GravityConfig getGravityConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? GravityConfig.DEFAULT : config.getGravityOrDefault();
    }

    public MovementConfig getMovementConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? MovementConfig.DEFAULT : config.getMovementOrDefault();
    }

    public JumpConfig getJumpConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? JumpConfig.DEFAULT : config.getJumpOrDefault();
    }

    public AmbienceConfig getAmbienceConfig() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? AmbienceConfig.DEFAULT : config.getAmbienceOrDefault();
    }

    public List<String> getEnabledSpecialRoles() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? List.of() : config.getSpecialRolesOrDefault().enabledRoles();
    }

    public int getRoomCount() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? 0 : config.getRoomCount();
    }

    public Optional<RoomConfig> getRoomConfig(int roomNumber) {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? Optional.empty() : config.getRoomConfig(roomNumber);
    }

    public Optional<RoomConfig.SpawnPoint> getSpawnPointForPlayer(int roomNumber, int playerIndexInRoom) {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? Optional.empty() : config.getSpawnPointForPlayer(roomNumber, playerIndexInRoom);
    }

    public int getTotalRoomCapacity() {
        MapEnhancementsConfiguration config = this.getConfigForCurrentWorld();
        return config == null ? 0 : config.getTotalCapacity();
    }

    public boolean hasAreaConfiguration() {
        return this.getConfigForCurrentWorld() != null;
    }

    private @Nullable MapEnhancementsConfiguration getConfigForCurrentWorld() {
        if (this.configuration != null) {
            return this.configuration;
        }
        if (this.world == null) {
            return null;
        }
        MapEnhancementsConfiguration config = MapEnhancementsConfigurationManager.getInstance()
                .getConfiguration(this.world.getRegistryKey().getValue());
        return config != null ? config : MapEnhancementsConfigurationManager.getInstance().getConfiguration();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.configuration = MapEnhancementsConfiguration.EMPTY;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
    }
}
