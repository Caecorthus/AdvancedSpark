package dev.doctor4t.wathe.api.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparkWatheEventCompatibilityTest {
    @Test
    public void booleanCompatibilityEventsUseSparkWatheAggregationRules() {
        AllowPlayerPunching.EVENT.register((attacker, victim) -> false);
        AllowPlayerPunching.EVENT.register((attacker, victim) -> true);
        CanSeePoison.EVENT.register(player -> true);
        ShouldDropOnDeath.EVENT.register((stack, victim) -> true);

        assertTrue(AllowPlayerPunching.EVENT.invoker().allowPunching(null, null));
        assertTrue(CanSeePoison.EVENT.invoker().visible(null));
        assertTrue(ShouldDropOnDeath.EVENT.invoker().shouldDrop(null, null));
    }

    @Test
    public void playerPoisonedEventsExposeBeforeAndAfterHooks() {
        UUID poisoner = UUID.randomUUID();
        AtomicInteger afterCalls = new AtomicInteger();

        PlayerPoisoned.BEFORE.register((player, ticks, source) -> null);
        PlayerPoisoned.BEFORE.register((player, ticks, source) -> PlayerPoisoned.PoisonResult.cancel());
        PlayerPoisoned.AFTER.register((player, ticks, source) -> afterCalls.incrementAndGet());

        PlayerPoisoned.PoisonResult result = PlayerPoisoned.BEFORE.invoker()
                .beforePlayerPoisoned(null, 200, poisoner);
        PlayerPoisoned.AFTER.invoker().afterPlayerPoisoned(null, 200, poisoner);

        assertTrue(result.cancelled());
        assertEquals(1, afterCalls.get());
    }

    @Test
    public void shopPurchaseEventsExposePriceOverrideAndAfterHook() {
        AtomicInteger afterCalls = new AtomicInteger();

        ShopPurchase.BEFORE.register((player, entry, index) -> null);
        ShopPurchase.BEFORE.register((player, entry, index) -> ShopPurchase.PurchaseResult.allow(3));
        ShopPurchase.AFTER.register((player, entry, index, pricePaid) -> afterCalls.addAndGet(pricePaid));

        ShopPurchase.PurchaseResult result = ShopPurchase.BEFORE.invoker().beforePurchase(null, null, 4);
        ShopPurchase.AFTER.invoker().afterPurchase(null, null, 4, 3);

        assertTrue(result.allowed());
        assertTrue(result.hasModifiedPrice());
        assertEquals(3, result.modifiedPrice());
        assertEquals(3, afterCalls.get());
        assertEquals("no", ShopPurchase.PurchaseResult.deny("no").denyReason());
    }

    @Test
    public void cohortResultUsesHighestPriorityListener() {
        ShouldShowCohort.EVENT.register((viewer, target) -> ShouldShowCohort.CohortResult.show());
        ShouldShowCohort.EVENT.register((viewer, target) -> ShouldShowCohort.CohortResult.hide(200));

        ShouldShowCohort.CohortResult result = ShouldShowCohort.EVENT.invoker().getCohortResult(null, null);

        assertFalse(result.shouldShow());
        assertEquals(200, result.priority());
    }

    @Test
    public void remainingSparkWatheEventsExposeTheirAggregationRules() {
        AtomicInteger recordEndCalls = new AtomicInteger();

        AllowPlayerChat.EVENT.register(player -> true);
        CanSeeBodyRole.EVENT.register(player -> true);
        CanSeeMoney.EVENT.register(player -> null);
        CanSeeMoney.EVENT.register(player -> CanSeeMoney.Result.ALLOW);
        CanTargetBody.EVENT.register((player, body) -> true);
        CanTargetBody.EVENT.register((player, body) -> false);
        RecordEvents.ON_RECORD_END.register((world, record) -> recordEndCalls.incrementAndGet());
        ShouldAllowSuppressedKey.EVENT.register(keyBinding -> true);

        assertTrue(AllowPlayerChat.EVENT.invoker().allowChat(null));
        assertTrue(CanSeeBodyRole.EVENT.invoker().canSee(null));
        assertSame(CanSeeMoney.Result.ALLOW, CanSeeMoney.EVENT.invoker().canSee(null));
        assertFalse(CanTargetBody.EVENT.invoker().canTarget(null, null));
        RecordEvents.ON_RECORD_END.invoker().onRecordEnd(null, null);
        assertEquals(1, recordEndCalls.get());
        assertTrue(ShouldAllowSuppressedKey.EVENT.invoker().shouldAllow(null));
    }
}
