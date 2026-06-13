package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.component.KillHistoryWorldComponent;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkKillHistoryBridgeTest {
    private static final Identifier KNIFE = Identifier.of("advancedspark_test", "knife");
    private static final UUID KILLER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID VICTIM_UUID = UUID.fromString("00000000-0000-0000-0000-000000000102");

    @Test
    public void recordsKillHistoryWhenKillerIsPresent() {
        KillHistoryWorldComponent history = new KillHistoryWorldComponent(null);
        TestPlayer killer = new TestPlayer(KILLER_UUID, 10L, history);
        TestPlayer victim = new TestPlayer(VICTIM_UUID, 45L, history);

        boolean recorded = AdvancedSparkKillHistoryBridge.recordKill(new TestBridge(), victim, killer, KNIFE);

        assertTrue(recorded);
        assertEquals(1, history.records().size());
        KillHistoryWorldComponent.KillRecord record = history.records().getFirst();
        assertEquals(KILLER_UUID, record.killerUuid());
        assertEquals(VICTIM_UUID, record.victimUuid());
        assertEquals(KNIFE, record.deathReason());
        assertEquals(45L, record.timestampTicks());
    }

    @Test
    public void ignoresKillHistoryWhenKillerIsMissing() {
        KillHistoryWorldComponent history = new KillHistoryWorldComponent(null);
        TestPlayer victim = new TestPlayer(VICTIM_UUID, 45L, history);

        boolean recorded = AdvancedSparkKillHistoryBridge.recordKill(new TestBridge(), victim, null, KNIFE);

        assertFalse(recorded);
        assertTrue(history.records().isEmpty());
    }

    private record TestPlayer(UUID uuid, long currentTick, KillHistoryWorldComponent history) {
    }

    private static final class TestBridge implements AdvancedSparkKillHistoryBridge.Bridge<TestPlayer> {
        @Override
        public UUID uuid(TestPlayer player) {
            return player.uuid();
        }

        @Override
        public long currentTick(TestPlayer player) {
            return player.currentTick();
        }

        @Override
        public KillHistoryWorldComponent history(TestPlayer player) {
            return player.history();
        }
    }
}
