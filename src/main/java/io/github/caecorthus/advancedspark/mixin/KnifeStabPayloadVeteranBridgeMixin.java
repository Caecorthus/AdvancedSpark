package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.KnifeStabPayload;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkVeteranBridge;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * English: Limits AdvancedSpark Veteran knife stabs without replacing Wathe's knife packet.
 * Chinese: 在不替换 Wathe 刀击网络包的情况下限制 AdvancedSpark 老兵刀击次数。
 */
@Mixin(KnifeStabPayload.Receiver.class)
public abstract class KnifeStabPayloadVeteranBridgeMixin {
    @Inject(
            method = "receive(Ldev/doctor4t/wathe/util/KnifeStabPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/game/GameFunctions;killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V"
            ),
            cancellable = true
    )
    private void advancedspark$beforeKnifeKill(
            KnifeStabPayload payload,
            ServerPlayNetworking.Context context,
            CallbackInfo ci
    ) {
        ServerPlayerEntity player = context.player();
        Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
        if (role == null || !AdvancedSparkVeteranBridge.isVeteranRole(role.identifier())) {
            return;
        }

        AdvancedSparkGameState state = AdvancedSparkComponents.get(player.getServer());
        if (!AdvancedSparkVeteranBridge.tryUseVeteranStab(state, player.getUuid())) {
            ci.cancel();
            return;
        }

        if (state.playerState(player.getUuid()).veteranStabsRemaining() == 0) {
            removeFirstKnife(player);
        }
    }

    private static void removeFirstKnife(PlayerEntity player) {
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isOf(WatheItems.KNIFE)) {
                player.getInventory().removeStack(slot);
                return;
            }
        }
    }
}
