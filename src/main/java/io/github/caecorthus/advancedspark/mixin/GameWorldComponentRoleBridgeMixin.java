package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.event.RoleAssigned;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.record.GameRecordManager;
import io.github.caecorthus.advancedspark.api.event.RoleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * English: Bridges Wathe UUID role writes into AdvancedSpark role events.
 * Chinese: 将 Wathe 基于 UUID 的职业写入桥接到 AdvancedSpark 职业事件。
 */
@Mixin(GameWorldComponent.class)
public abstract class GameWorldComponentRoleBridgeMixin {
    @Shadow
    @Final
    private World world;

    @Inject(method = "addRole(Ljava/util/UUID;Ldev/doctor4t/wathe/api/Role;)V", at = @At("TAIL"))
    private void advancedspark$afterAddRole(UUID playerUuid, Role role, CallbackInfo ci) {
        RoleEvents.ROLE_ASSIGNED_BY_UUID.invoker().onRoleAssigned(playerUuid, role.identifier());
        if (this.world.getPlayerByUuid(playerUuid) instanceof ServerPlayerEntity player) {
            RoleEvents.ROLE_ASSIGNED.invoker().onRoleAssigned(player, role.identifier());
            RoleAssigned.EVENT.invoker().assignRole(player, role);
            GameRecordManager.recordRoleAssigned(player, role);
        }
    }
}
