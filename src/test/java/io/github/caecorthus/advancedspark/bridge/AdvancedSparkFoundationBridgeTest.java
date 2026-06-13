package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.event.PlayerPoisoned;
import dev.doctor4t.wathe.api.event.PsychoModeEvents;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkFoundationBridgeTest {
    @Test
    public void specialRoleBridgeTracksAdvancedSparkSpecialRolesByRoleObject() {
        Identifier roleId = Identifier.of("advancedspark_test", "special_bridge_role");

        Role role = AdvancedSparkRoleBuilder.fromSparkRole(
                        roleId,
                        0xffffff,
                        false,
                        false,
                        Role.MoodType.REAL,
                        20,
                        false
                )
                .special(true)
                .registerLocal();

        assertTrue(AdvancedSparkWatheRolesBridge.specialRoles().contains(role));
    }

    @Test
    public void poisonBridgeUsesBeforeAndAfterSparkEvents() {
        UUID poisoner = UUID.fromString("00000000-0000-0000-0000-000000000901");
        Identifier source = Identifier.of("advancedspark_test", "poison");
        AtomicInteger afterCalls = new AtomicInteger();

        PlayerPoisoned.BEFORE.register((player, ticks, sourcePlayer) -> PlayerPoisoned.PoisonResult.cancel());
        PlayerPoisoned.AFTER.register((player, ticks, sourcePlayer) -> afterCalls.incrementAndGet());

        assertFalse(AdvancedSparkPoisonBridge.shouldApplyPoison(null, 120, poisoner, source));
        AdvancedSparkPoisonBridge.dispatchPoisonApplied(null, 120, poisoner, source);
        assertTrue(afterCalls.get() > 0);
    }

    @Test
    public void psychoBridgeCanDispatchSilentStartAndStopEvents() {
        AtomicBoolean sawSilentStart = new AtomicBoolean(false);
        AtomicBoolean sawSilentStop = new AtomicBoolean(false);

        PsychoModeEvents.ON_PSYCHO_START.register((player, trackActive) -> sawSilentStart.set(!trackActive));
        PsychoModeEvents.ON_PSYCHO_END.register((player, trackActive) -> sawSilentStop.set(!trackActive));

        AdvancedSparkPsychoBridge.dispatchPsychoStart(null, false);
        AdvancedSparkPsychoBridge.dispatchPsychoStop(null, false);

        assertTrue(sawSilentStart.get());
        assertTrue(sawSilentStop.get());
    }
}
