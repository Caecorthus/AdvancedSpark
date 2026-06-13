package io.github.caecorthus.advancedspark.api.event;

import org.junit.jupiter.api.Test;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BridgeEventDispatchTest {
    @Test
    public void killBridgeCancelsBeforeVanillaAndSkipsAfterCallbacks() {
        KillEvents.Bridge<String> bridge = new KillEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforeKill((killer, victim) -> {
            calls.add("before-cancel");
            return KillEvents.Decision.CANCEL;
        });
        bridge.registerAfterKill((killer, victim) -> calls.add("after"));

        KillEvents.Decision decision = bridge.dispatchKill("killer", "victim", () -> calls.add("vanilla"));

        assertEquals(KillEvents.Decision.CANCEL, decision);
        assertEquals(List.of("before-cancel"), calls);
    }

    @Test
    public void killBridgeStopsAtAllowThenRunsVanillaAndAfterCallbacks() {
        KillEvents.Bridge<String> bridge = new KillEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforeKill((killer, victim) -> {
            calls.add("before-pass");
            return KillEvents.Decision.PASS;
        });
        bridge.registerBeforeKill((killer, victim) -> {
            calls.add("before-allow");
            return KillEvents.Decision.ALLOW;
        });
        bridge.registerBeforeKill((killer, victim) -> {
            calls.add("before-late");
            return KillEvents.Decision.CANCEL;
        });
        bridge.registerAfterKill((killer, victim) -> calls.add("after"));

        KillEvents.Decision decision = bridge.dispatchKill("killer", "victim", () -> calls.add("vanilla"));

        assertEquals(KillEvents.Decision.ALLOW, decision);
        assertEquals(List.of("before-pass", "before-allow", "vanilla", "after"), calls);
    }

    @Test
    public void killRuntimeBridgeOnlyCancelsVanillaOnFail() {
        assertEquals(true, KillEvents.shouldCancelVanillaKill(ActionResult.FAIL));
        assertEquals(false, KillEvents.shouldCancelVanillaKill(ActionResult.SUCCESS));
        assertEquals(false, KillEvents.shouldCancelVanillaKill(ActionResult.CONSUME));
        assertEquals(false, KillEvents.shouldCancelVanillaKill(ActionResult.PASS));
    }

    @Test
    public void winBridgePrefersNeutralThenBlockThenAllow() {
        WinEvents.Bridge<String, String> bridge = new WinEvents.Bridge<>();

        bridge.registerWinnerCheck(server -> WinEvents.Decision.allow());
        bridge.registerWinnerCheck(server -> WinEvents.Decision.block());
        bridge.registerWinnerCheck(server -> WinEvents.Decision.neutral("solo"));

        assertEquals(WinEvents.Decision.neutral("solo"), bridge.resolveWinner("server"));
    }

    @Test
    public void winBridgeBlocksAllowWhenNoNeutralWinnerExists() {
        WinEvents.Bridge<String, String> bridge = new WinEvents.Bridge<>();

        bridge.registerWinnerCheck(server -> WinEvents.Decision.allow());
        bridge.registerWinnerCheck(server -> WinEvents.Decision.block());

        assertEquals(WinEvents.Decision.block(), bridge.resolveWinner("server"));
    }

    @Test
    public void doorBridgeReturnsFirstNonPassDecision() {
        DoorEvents.Bridge<String, String> bridge = new DoorEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforeDoorUse((player, door) -> {
            calls.add("pass");
            return DoorEvents.Decision.PASS;
        });
        bridge.registerBeforeDoorUse((player, door) -> {
            calls.add("allow");
            return DoorEvents.Decision.ALLOW;
        });
        bridge.registerBeforeDoorUse((player, door) -> {
            calls.add("late");
            return DoorEvents.Decision.CANCEL;
        });

        assertEquals(DoorEvents.Decision.ALLOW, bridge.dispatchBeforeDoorUse("player", "door"));
        assertEquals(List.of("pass", "allow"), calls);
    }

    @Test
    public void doorBridgeDispatchesAfterCallbacksInOrder() {
        DoorEvents.Bridge<String, String> bridge = new DoorEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerAfterDoorUse((player, door) -> calls.add("first:" + player + ":" + door));
        bridge.registerAfterDoorUse((player, door) -> calls.add("second:" + player + ":" + door));

        bridge.dispatchAfterDoorUse("player", "door");

        assertEquals(List.of("first:player:door", "second:player:door"), calls);
    }

    @Test
    public void shopBridgeLetsBuildCallbacksMutateEntries() {
        ShopEvents.Bridge<String, String> bridge = new ShopEvents.Bridge<>();
        List<String> entries = new ArrayList<>(List.of("base"));
        List<String> calls = new ArrayList<>();

        bridge.registerShopBuild((player, shopEntries) -> {
            calls.add("first:" + player);
            shopEntries.add("spark_item");
        });
        bridge.registerShopBuild((player, shopEntries) -> {
            calls.add("second:" + shopEntries.size());
            shopEntries.add("spark_item_2");
        });

        bridge.dispatchShopBuild("player", entries);

        assertEquals(List.of("base", "spark_item", "spark_item_2"), entries);
        assertEquals(List.of("first:player", "second:2"), calls);
    }

    @Test
    public void shopBridgePurchaseCancelSkipsVanillaAndAfterCallbacks() {
        ShopEvents.Bridge<String, String> bridge = new ShopEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforePurchase((player, item) -> {
            calls.add("before-cancel:" + item);
            return ShopEvents.Decision.CANCEL;
        });
        bridge.registerAfterPurchase((player, item) -> calls.add("after:" + item));

        ShopEvents.Decision decision = bridge.dispatchPurchase("player", "spark_item", () -> calls.add("vanilla"));

        assertEquals(ShopEvents.Decision.CANCEL, decision);
        assertEquals(List.of("before-cancel:spark_item"), calls);
    }

    @Test
    public void shopRuntimeBridgeOnlyCancelsVanillaOnFail() {
        assertEquals(true, ShopEvents.shouldCancelVanillaPurchase(ActionResult.FAIL));
        assertEquals(false, ShopEvents.shouldCancelVanillaPurchase(ActionResult.SUCCESS));
        assertEquals(false, ShopEvents.shouldCancelVanillaPurchase(ActionResult.CONSUME));
        assertEquals(false, ShopEvents.shouldCancelVanillaPurchase(ActionResult.PASS));
    }

    @Test
    public void shopBridgePurchaseAllowRunsVanillaAndAfterCallbacks() {
        ShopEvents.Bridge<String, String> bridge = new ShopEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforePurchase((player, item) -> {
            calls.add("before-pass");
            return ShopEvents.Decision.PASS;
        });
        bridge.registerBeforePurchase((player, item) -> {
            calls.add("before-allow:" + item);
            return ShopEvents.Decision.ALLOW;
        });
        bridge.registerBeforePurchase((player, item) -> {
            calls.add("before-late");
            return ShopEvents.Decision.CANCEL;
        });
        bridge.registerAfterPurchase((player, item) -> calls.add("after:" + player + ":" + item));

        ShopEvents.Decision decision = bridge.dispatchPurchase("player", "spark_item", () -> calls.add("vanilla"));

        assertEquals(ShopEvents.Decision.ALLOW, decision);
        assertEquals(List.of("before-pass", "before-allow:spark_item", "vanilla", "after:player:spark_item"), calls);
    }

    @Test
    public void taskBridgeCompleteCancelSkipsVanillaAndAfterCallbacks() {
        TaskEvents.Bridge<String, String> bridge = new TaskEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforeTaskComplete((player, task) -> {
            calls.add("before-cancel:" + task);
            return TaskEvents.Decision.CANCEL;
        });
        bridge.registerAfterTaskComplete((player, task) -> calls.add("after:" + task));

        TaskEvents.Decision decision = bridge.dispatchTaskComplete("player", "wire_task", () -> calls.add("vanilla"));

        assertEquals(TaskEvents.Decision.CANCEL, decision);
        assertEquals(List.of("before-cancel:wire_task"), calls);
    }

    @Test
    public void taskBridgeCompleteAllowRunsVanillaAndAfterCallbacks() {
        TaskEvents.Bridge<String, String> bridge = new TaskEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerBeforeTaskComplete((player, task) -> {
            calls.add("before-pass");
            return TaskEvents.Decision.PASS;
        });
        bridge.registerBeforeTaskComplete((player, task) -> {
            calls.add("before-allow:" + task);
            return TaskEvents.Decision.ALLOW;
        });
        bridge.registerBeforeTaskComplete((player, task) -> {
            calls.add("before-late");
            return TaskEvents.Decision.CANCEL;
        });
        bridge.registerAfterTaskComplete((player, task) -> calls.add("after:" + player + ":" + task));

        TaskEvents.Decision decision = bridge.dispatchTaskComplete("player", "wire_task", () -> calls.add("vanilla"));

        assertEquals(TaskEvents.Decision.ALLOW, decision);
        assertEquals(List.of("before-pass", "before-allow:wire_task", "vanilla", "after:player:wire_task"), calls);
    }

    @Test
    public void roleBridgeDispatchesAssignmentAndResetInOrder() {
        RoleEvents.Bridge<String, String> bridge = new RoleEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerRoleAssigned((player, role) -> calls.add("assign:" + player + ":" + role));
        bridge.registerRoleReset(player -> calls.add("reset:" + player));

        bridge.dispatchRoleAssigned("player", "detective");
        bridge.dispatchRoleReset("player");

        assertEquals(List.of("assign:player:detective", "reset:player"), calls);
    }

    @Test
    public void roleBridgeDispatchesResetCallbacksInOrder() {
        RoleEvents.Bridge<String, String> bridge = new RoleEvents.Bridge<>();
        List<String> calls = new ArrayList<>();

        bridge.registerRoleReset(player -> calls.add("first:" + player));
        bridge.registerRoleReset(player -> calls.add("second:" + player));

        bridge.dispatchRoleReset("player");

        assertEquals(List.of("first:player", "second:player"), calls);
    }

    @Test
    public void roleBridgeDispatchesUuidAssignmentWithIdentifierRole() {
        RoleEvents.Bridge<UUID, Identifier> bridge = new RoleEvents.Bridge<>();
        List<String> calls = new ArrayList<>();
        UUID playerUuid = UUID.fromString("00000000-0000-0000-0000-000000000123");
        Identifier roleId = Identifier.of("wathe", "detective");

        bridge.registerRoleAssigned((player, role) -> calls.add(player + ":" + role));

        bridge.dispatchRoleAssigned(playerUuid, roleId);

        assertEquals(List.of("00000000-0000-0000-0000-000000000123:wathe:detective"), calls);
    }

    @Test
    public void roleUuidEventDispatchesAssignmentWithIdentifierRole() {
        List<String> calls = new ArrayList<>();
        UUID playerUuid = UUID.fromString("00000000-0000-0000-0000-000000000456");
        Identifier roleId = Identifier.of("wathe", "killer");

        RoleEvents.ROLE_ASSIGNED_BY_UUID.register((player, role) -> calls.add(player + ":" + role));

        RoleEvents.ROLE_ASSIGNED_BY_UUID.invoker().onRoleAssigned(playerUuid, roleId);

        assertEquals(List.of("00000000-0000-0000-0000-000000000456:wathe:killer"), calls);
    }
}
