package io.github.caecorthus.advancedspark.bridge;

import net.minecraft.util.math.Box;

/**
 * English: Mixin-backed access to Spark-wathe map variable extensions.
 * Chinese: 由 mixin 支撑的 Spark-wathe 地图变量扩展访问口。
 */
public interface AdvancedSparkMapVariablesAccess {
    Box advancedspark$getSnowflakeCollider();

    void advancedspark$setSnowflakeCollider(Box collider);
}
