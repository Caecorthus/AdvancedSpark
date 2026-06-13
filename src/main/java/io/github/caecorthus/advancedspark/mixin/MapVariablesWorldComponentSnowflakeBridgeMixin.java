package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkMapVariablesAccess;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkMapVariablesBridge;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Adds Spark-wathe snowflake collider state to original Wathe map variables.
 * Chinese: 给原版 Wathe 地图变量补上 Spark-wathe 雪花碰撞箱状态。
 */
@Mixin(MapVariablesWorldComponent.class)
public abstract class MapVariablesWorldComponentSnowflakeBridgeMixin implements AdvancedSparkMapVariablesAccess {
    @Unique
    private Box advancedspark$snowflakeCollider = AdvancedSparkMapVariablesBridge.DEFAULT_SNOWFLAKE_COLLIDER;

    @Shadow
    public abstract void sync();

    @Shadow
    public abstract void writeBoxToNbt(NbtCompound tag, Box box, String name);

    @Override
    public Box advancedspark$getSnowflakeCollider() {
        return this.advancedspark$snowflakeCollider;
    }

    @Override
    public void advancedspark$setSnowflakeCollider(Box collider) {
        this.advancedspark$snowflakeCollider = collider;
        this.sync();
    }

    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void advancedspark$readSnowflakeCollider(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        if (tag.contains("snowflakeColliderMinX")) {
            this.advancedspark$snowflakeCollider = MapVariablesWorldComponent.getBoxFromNbt(tag, "snowflakeCollider");
        }
    }

    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void advancedspark$writeSnowflakeCollider(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        this.writeBoxToNbt(tag, this.advancedspark$snowflakeCollider, "snowflakeCollider");
    }
}
