package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.ScoreboardRoleSelectorComponent;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkForcedRoleAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * English: Adds Spark-wathe forced-role storage to original Wathe's scoreboard selector.
 * Chinese: 给原版 Wathe 的记分板选择器补上 Spark-wathe 强制职业存储。
 */
@Mixin(ScoreboardRoleSelectorComponent.class)
public abstract class ScoreboardRoleSelectorComponentForcedRolesBridgeMixin implements AdvancedSparkForcedRoleAccess {
    @Unique
    private final Map<Role, List<UUID>> advancedspark$forcedRoles = new HashMap<>();

    @Override
    public Map<Role, List<UUID>> advancedspark$getForcedRoles() {
        return this.advancedspark$forcedRoles;
    }

    @Override
    public List<UUID> advancedspark$getForcedForRole(Role role) {
        return this.advancedspark$forcedRoles.computeIfAbsent(role, ignored -> new ArrayList<>());
    }

    @Override
    public void advancedspark$addForcedRole(Role role, UUID playerId) {
        this.advancedspark$removeForcedRole(playerId);
        this.advancedspark$getForcedForRole(role).add(playerId);
    }

    @Override
    public void advancedspark$removeForcedRole(UUID playerId) {
        for (List<UUID> players : this.advancedspark$forcedRoles.values()) {
            players.remove(playerId);
        }
        this.advancedspark$forcedRoles.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public @Nullable Role advancedspark$getForcedRoleForPlayer(UUID playerId) {
        for (Map.Entry<Role, List<UUID>> entry : this.advancedspark$forcedRoles.entrySet()) {
            if (entry.getValue().contains(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void advancedspark$clearForcedRoles() {
        this.advancedspark$forcedRoles.clear();
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void advancedspark$clearForcedRolesOnReset(CallbackInfoReturnable<Integer> cir) {
        this.advancedspark$clearForcedRoles();
    }
}
