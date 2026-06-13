package io.github.caecorthus.advancedspark.bridge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * English: Cardinal Components registration helpers for migrated NoellesRoles components.
 * Chinese: 面向已迁移 NoellesRoles 组件的 Cardinal Components 注册辅助方法。
 */
public final class AdvancedSparkComponentRegistration {
    private AdvancedSparkComponentRegistration() {
    }

    public static <T extends Component> void registerPlayerComponent(
            EntityComponentFactoryRegistry registry,
            ComponentKey<T> key,
            Function<PlayerEntity, T> factory
    ) {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(factory, "factory");
        registry.beginRegistration(PlayerEntity.class, key)
                .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
                .end(factory::apply);
    }

    public static <T extends Component> void registerWorldComponent(
            WorldComponentFactoryRegistry registry,
            ComponentKey<T> key,
            Function<World, T> factory
    ) {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(factory, "factory");
        registry.register(key, factory::apply);
    }

    public static <T extends Component> void registerScoreboardComponent(
            ScoreboardComponentFactoryRegistry registry,
            ComponentKey<T> key,
            BiFunction<Scoreboard, MinecraftServer, T> factory
    ) {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(factory, "factory");
        registry.registerScoreboardComponent(key, factory::apply);
    }
}
