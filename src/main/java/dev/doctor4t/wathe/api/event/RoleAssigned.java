package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.api.Role;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

/**
 * English: Spark-wathe role assignment event.
 * Chinese: Spark-wathe 职业分配事件。
 */
public interface RoleAssigned {
    Event<RoleAssigned> EVENT = EventFactory.createArrayBacked(
            RoleAssigned.class,
            callbacks -> (player, role) -> {
                for (RoleAssigned callback : callbacks) {
                    callback.assignRole(player, role);
                }
            }
    );

    void assignRole(PlayerEntity player, Role role);
}
