package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.block.TrainDoorBlock;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkDoorBridge;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * English: Bridges Wathe train-door interactions into AdvancedSpark door events.
 * Chinese: 将 Wathe 火车门交互桥接到 AdvancedSpark 门事件。
 */
@Mixin(TrainDoorBlock.class)
public abstract class TrainDoorBlockBridgeMixin {
    @Inject(
            method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void advancedspark$beforeDoorUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        ActionResult result = AdvancedSparkDoorBridge.beforeUse(player, pos);
        if (result != ActionResult.PASS) {
            cir.setReturnValue(result);
        }
    }

    @Inject(
            method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
            at = @At("RETURN")
    )
    private void advancedspark$afterDoorUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        AdvancedSparkDoorBridge.afterUse(player, pos);
    }
}
