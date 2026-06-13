package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoleAnnouncementBridge;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.OptionalInt;

/**
 * English: Re-sends Wathe's welcome announcement with AdvancedSpark role text after vanilla Wathe sends its base-role text.
 * Chinese: 在 Wathe 发送原版基础职业入场文本后，用 AdvancedSpark 职业文本重新发送一次入场公告。
 */
@Mixin(MurderGameMode.class)
public abstract class MurderGameModeRoleAnnouncementBridgeMixin {
    @Inject(
            method = "initializeGame(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/cca/GameWorldComponent;Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void advancedspark$sendAdvancedRoleAnnouncements(
            ServerWorld serverWorld,
            GameWorldComponent gameWorldComponent,
            List<ServerPlayerEntity> players,
            CallbackInfo ci
    ) {
        int killerCount = gameWorldComponent.getAllKillerTeamPlayers().size();
        int targetCount = Math.max(0, players.size() - killerCount);

        for (ServerPlayerEntity player : players) {
            Role role = gameWorldComponent.getRole(player);
            if (role == null) {
                continue;
            }

            OptionalInt roleAnnouncement = AdvancedSparkRoleAnnouncementBridge.announcementIndex(role.identifier());
            if (roleAnnouncement.isEmpty()) {
                continue;
            }

            ServerPlayNetworking.send(
                    player,
                    new AnnounceWelcomePayload(roleAnnouncement.getAsInt(), killerCount, targetCount)
            );
        }
    }
}
