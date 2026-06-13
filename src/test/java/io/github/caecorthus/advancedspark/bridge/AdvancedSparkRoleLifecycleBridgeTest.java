package io.github.caecorthus.advancedspark.bridge;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSparkRoleLifecycleBridgeTest {
    @Test
    public void dispatchesOnlyMatchingRoleAssignmentCallbacks() {
        AdvancedSparkRoleLifecycleBridge.Bridge<String> bridge = new AdvancedSparkRoleLifecycleBridge.Bridge<>();
        Identifier bartender = Identifier.of("advancedspark_test", "bartender");
        Identifier bomber = Identifier.of("advancedspark_test", "bomber");
        List<String> calls = new ArrayList<>();

        bridge.registerRoleAssigned(bartender, player -> calls.add("bartender:" + player));
        bridge.registerRoleAssigned(bomber, player -> calls.add("bomber:" + player));

        bridge.dispatchRoleAssigned("player", bartender);

        assertEquals(List.of("bartender:player"), calls);
    }

    @Test
    public void dispatchesResetCallbacksInRegistrationOrder() {
        AdvancedSparkRoleLifecycleBridge.Bridge<String> bridge = new AdvancedSparkRoleLifecycleBridge.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerPlayerReset(player -> calls.add("first:" + player));
        bridge.registerPlayerReset(player -> calls.add("second:" + player));

        bridge.dispatchPlayerReset("player");

        assertEquals(List.of("first:player", "second:player"), calls);
    }
}
