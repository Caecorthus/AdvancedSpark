package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.component.AbilityPlayerComponent;
import io.github.caecorthus.advancedspark.component.ConfigWorldComponent;
import io.github.caecorthus.advancedspark.component.HiddenBodiesWorldComponent;
import io.github.caecorthus.advancedspark.component.KillHistoryWorldComponent;
import io.github.caecorthus.advancedspark.component.WorldMusicComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * English: Resets migrated shared components at Wathe lifecycle boundaries.
 * Chinese: 在 Wathe 生命周期边界重置已迁移的共享组件。
 */
public final class AdvancedSparkComponentLifecycleBridge {
    private static boolean initialized;

    private AdvancedSparkComponentLifecycleBridge() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        AdvancedSparkRoleLifecycleBridge.registerPlayerReset(AdvancedSparkComponentLifecycleBridge::resetServerPlayerComponents);
    }

    public static <TPlayer> void resetPlayerComponents(PlayerComponentBridge<TPlayer> bridge, TPlayer player) {
        Objects.requireNonNull(bridge, "bridge");
        bridge.ability(player).reset();
    }

    public static <TWorld> void resetWorldComponents(WorldComponentBridge<TWorld> bridge, TWorld world) {
        Objects.requireNonNull(bridge, "bridge");
        bridge.config(world).reset();
        bridge.worldMusic(world).reset();
        bridge.hiddenBodies(world).reset();
        bridge.killHistory(world).reset();
    }

    public static void resetWorldComponents(World world) {
        resetWorldComponents(WorldBridge.INSTANCE, world);
    }

    private static void resetServerPlayerComponents(ServerPlayerEntity player) {
        resetPlayerComponents(PlayerBridge.INSTANCE, player);
    }

    public interface PlayerComponentBridge<TPlayer> {
        AbilityPlayerComponent ability(TPlayer player);
    }

    public interface WorldComponentBridge<TWorld> {
        ConfigWorldComponent config(TWorld world);

        WorldMusicComponent worldMusic(TWorld world);

        HiddenBodiesWorldComponent hiddenBodies(TWorld world);

        KillHistoryWorldComponent killHistory(TWorld world);
    }

    private enum PlayerBridge implements PlayerComponentBridge<ServerPlayerEntity> {
        INSTANCE;

        @Override
        public AbilityPlayerComponent ability(ServerPlayerEntity player) {
            return AbilityPlayerComponent.KEY.get(player);
        }
    }

    private enum WorldBridge implements WorldComponentBridge<World> {
        INSTANCE;

        @Override
        public ConfigWorldComponent config(World world) {
            return ConfigWorldComponent.KEY.get(world);
        }

        @Override
        public WorldMusicComponent worldMusic(World world) {
            return WorldMusicComponent.KEY.get(world);
        }

        @Override
        public HiddenBodiesWorldComponent hiddenBodies(World world) {
            return HiddenBodiesWorldComponent.KEY.get(world);
        }

        @Override
        public KillHistoryWorldComponent killHistory(World world) {
            return KillHistoryWorldComponent.KEY.get(world);
        }
    }
}
