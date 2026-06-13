package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoleSelectionBridge;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * English: Runs AdvancedSpark role selection after Wathe's vanilla murder roles are assigned.
 * Chinese: 在 Wathe 原版谋杀模式职业分配完成后运行 AdvancedSpark 职业选择。
 */
@Mixin(MurderGameMode.class)
public abstract class MurderGameModeRoleSelectionBridgeMixin {
    @Inject(
            method = "assignRolesAndGetKillerCount(Lnet/minecraft/server/world/ServerWorld;Ljava/util/List;Ldev/doctor4t/wathe/cca/GameWorldComponent;)I",
            at = @At("RETURN")
    )
    private static void advancedspark$afterVanillaRoleSelection(
            ServerWorld world,
            List<ServerPlayerEntity> players,
            GameWorldComponent gameComponent,
            CallbackInfoReturnable<Integer> cir
    ) {
        AdvancedSparkRoleSelectionBridge.assignAdvancedRoles(world, gameComponent, players);
    }
}
