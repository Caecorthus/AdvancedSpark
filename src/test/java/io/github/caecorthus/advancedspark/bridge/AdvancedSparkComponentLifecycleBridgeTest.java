package io.github.caecorthus.advancedspark.bridge;

import io.github.caecorthus.advancedspark.component.AbilityPlayerComponent;
import io.github.caecorthus.advancedspark.component.ConfigWorldComponent;
import io.github.caecorthus.advancedspark.component.HiddenBodiesWorldComponent;
import io.github.caecorthus.advancedspark.component.KillHistoryWorldComponent;
import io.github.caecorthus.advancedspark.component.MusicMomentType;
import io.github.caecorthus.advancedspark.component.WorldMusicComponent;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AdvancedSparkComponentLifecycleBridgeTest {
    @Test
    public void playerResetClearsSharedAbilityCooldown() {
        TestPlayer player = new TestPlayer(new AbilityPlayerComponent(null));
        player.ability().setCooldown(80);

        AdvancedSparkComponentLifecycleBridge.resetPlayerComponents(new TestPlayerBridge(), player);

        assertEquals(0, player.ability().getCooldown());
    }

    @Test
    public void worldResetClearsRoundScopedWorldComponents() {
        TestWorld world = new TestWorld(
                new ConfigWorldComponent(null),
                new WorldMusicComponent(null),
                new HiddenBodiesWorldComponent(null),
                new KillHistoryWorldComponent(null)
        );
        UUID bodyUuid = UUID.fromString("00000000-0000-0000-0000-000000000201");
        UUID killerUuid = UUID.fromString("00000000-0000-0000-0000-000000000202");
        UUID victimUuid = UUID.fromString("00000000-0000-0000-0000-000000000203");

        world.config().insaneSeesMorphs = false;
        world.worldMusic().startMusic(MusicMomentType.CORRUPT_COP_MOMENT, 1);
        world.hiddenBodies().addHiddenBody(bodyUuid);
        world.killHistory().recordKill(killerUuid, victimUuid, Identifier.of("advancedspark_test", "knife"), 12L);

        AdvancedSparkComponentLifecycleBridge.resetWorldComponents(new TestWorldBridge(), world);

        assertFalse(world.config().insaneSeesMorphs);
        assertEquals(MusicMomentType.NONE, world.worldMusic().getCurrentMoment());
        assertFalse(world.hiddenBodies().isHidden(bodyUuid));
        assertEquals(0, world.killHistory().records().size());
    }

    private record TestPlayer(AbilityPlayerComponent ability) {
    }

    private record TestWorld(
            ConfigWorldComponent config,
            WorldMusicComponent worldMusic,
            HiddenBodiesWorldComponent hiddenBodies,
            KillHistoryWorldComponent killHistory
    ) {
    }

    private static final class TestPlayerBridge implements AdvancedSparkComponentLifecycleBridge.PlayerComponentBridge<TestPlayer> {
        @Override
        public AbilityPlayerComponent ability(TestPlayer player) {
            return player.ability();
        }
    }

    private static final class TestWorldBridge implements AdvancedSparkComponentLifecycleBridge.WorldComponentBridge<TestWorld> {
        @Override
        public ConfigWorldComponent config(TestWorld world) {
            return world.config();
        }

        @Override
        public WorldMusicComponent worldMusic(TestWorld world) {
            return world.worldMusic();
        }

        @Override
        public HiddenBodiesWorldComponent hiddenBodies(TestWorld world) {
            return world.hiddenBodies();
        }

        @Override
        public KillHistoryWorldComponent killHistory(TestWorld world) {
            return world.killHistory();
        }
    }
}
