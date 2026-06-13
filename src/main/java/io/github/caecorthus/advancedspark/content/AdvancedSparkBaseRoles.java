package io.github.caecorthus.advancedspark.content;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.index.WatheItems;
import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoleLifecycleBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkWatheRoleRegistry;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import net.minecraft.item.ItemStack;

/**
 * English: Spark-wathe baseline roles recreated with source-compatible Wathe ids.
 * Chinese: 使用源兼容 Wathe ID 重建 Spark-wathe 基础职业。
 */
public final class AdvancedSparkBaseRoles {
    public static final int VETERAN_STAB_USES = 2;
    public static final int VETERAN_MAX_SPRINT_TICKS = 200;
    public static final Role VETERAN = new Role(
            AdvancedSparkSourceIds.sparkWathe("veteran"),
            0x4A7023,
            true,
            false,
            Role.MoodType.REAL,
            VETERAN_MAX_SPRINT_TICKS,
            false
    );

    private static boolean registered;

    private AdvancedSparkBaseRoles() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        AdvancedSparkWatheRoleRegistry.register(VETERAN, Faction.CREWMATE);
        AdvancedSparkRoleLifecycleBridge.registerRoleAssigned(VETERAN.identifier(), player -> {
            AdvancedSparkComponents.get(player.getServer())
                    .initializeVeteranStabs(player.getUuid(), VETERAN_STAB_USES);
            player.giveItemStack(new ItemStack(WatheItems.KNIFE));
        });
    }
}
