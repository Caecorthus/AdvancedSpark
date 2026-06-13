package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.event.PsychoModeEvents;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * English: Migration-safe replacement for Spark-wathe psycho boolean overloads.
 * Chinese: 面向迁移的 Spark-wathe 疯魔模式布尔重载替代层。
 */
public final class AdvancedSparkPsychoBridge {
    private AdvancedSparkPsychoBridge() {
    }

    public static boolean startPsycho(PlayerPsychoComponent component, ServerPlayerEntity player, boolean trackActive) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(player, "player");
        boolean started;
        if (trackActive) {
            started = component.startPsycho();
        } else {
            started = ShopEntry.insertStackInFreeSlot(player, new ItemStack(WatheItems.BAT));
            if (started) {
                component.setPsychoTicks(GameConstants.PSYCHO_TIMER);
                component.setArmour(GameConstants.PSYCHO_MODE_ARMOUR);
            }
        }
        if (started) {
            dispatchPsychoStart(player, trackActive);
        }
        return started;
    }

    public static void stopPsycho(PlayerPsychoComponent component, ServerPlayerEntity player, boolean trackActive) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(player, "player");
        if (trackActive) {
            component.stopPsycho();
        } else {
            component.psychoTicks = 0;
            player.getInventory().remove(
                    itemStack -> itemStack.isOf(WatheItems.BAT),
                    Integer.MAX_VALUE,
                    player.playerScreenHandler.getCraftingInput()
            );
            component.sync();
        }
        dispatchPsychoStop(player, trackActive);
    }

    public static void dispatchPsychoStart(@Nullable ServerPlayerEntity player, boolean trackActive) {
        PsychoModeEvents.ON_PSYCHO_START.invoker().onPsychoStart(player, trackActive);
    }

    public static void dispatchPsychoStop(@Nullable ServerPlayerEntity player, boolean trackActive) {
        PsychoModeEvents.ON_PSYCHO_END.invoker().onPsychoEnd(player, trackActive);
    }
}
