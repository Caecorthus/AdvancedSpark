package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.api.event.DoorEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * English: Shared Wathe door hook adapter with only server player and block position context.
 * Chinese: Wathe 门钩子的共享适配器，只传递服务端玩家和方块坐标上下文。
 */
public final class AdvancedSparkDoorBridge {
    private AdvancedSparkDoorBridge() {
    }

    public static ActionResult beforeUse(PlayerEntity player, BlockPos doorPos) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }
        return DoorEvents.BEFORE_DOOR_USE.invoker().beforeDoorUse(serverPlayer, doorPos);
    }

    public static void afterUse(PlayerEntity player, BlockPos doorPos) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            DoorEvents.AFTER_DOOR_USE.invoker().afterDoorUse(serverPlayer, doorPos);
        }
    }
}
