package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * English: Safe bridge for Spark-wathe MapVariablesWorldComponent snowflake collider APIs.
 * Chinese: Spark-wathe 地图变量组件雪花碰撞箱 API 的安全桥接层。
 */
public final class AdvancedSparkMapVariablesBridge {
    public static final Box DEFAULT_SNOWFLAKE_COLLIDER = new Box(-41.5, 126.0, -538.5, 169.5, 120.0, -532.5);

    private AdvancedSparkMapVariablesBridge() {
    }

    public static Box getSnowflakeCollider(World world) {
        return getSnowflakeCollider(component(world));
    }

    public static Box getSnowflakeCollider(MapVariablesWorldComponent component) {
        return access(component).advancedspark$getSnowflakeCollider();
    }

    public static void setSnowflakeCollider(World world, Box collider) {
        setSnowflakeCollider(component(world), collider);
    }

    public static void setSnowflakeCollider(MapVariablesWorldComponent component, Box collider) {
        access(component).advancedspark$setSnowflakeCollider(Objects.requireNonNull(collider, "collider"));
    }

    private static MapVariablesWorldComponent component(World world) {
        return MapVariablesWorldComponent.KEY.get(Objects.requireNonNull(world, "world"));
    }

    private static AdvancedSparkMapVariablesAccess access(MapVariablesWorldComponent component) {
        Objects.requireNonNull(component, "component");
        if (component instanceof AdvancedSparkMapVariablesAccess access) {
            return access;
        }
        throw new IllegalStateException("AdvancedSpark map variables mixin is not applied");
    }
}
