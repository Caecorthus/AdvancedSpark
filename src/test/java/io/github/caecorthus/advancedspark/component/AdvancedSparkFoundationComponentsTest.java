package io.github.caecorthus.advancedspark.component;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkFoundationComponentsTest {
    @Test
    public void abilityCooldownTicksResetsAndPersists() {
        AbilityPlayerComponent ability = new AbilityPlayerComponent(null);
        ability.setCooldown(3);

        ability.clientTick();
        ability.serverTick();

        assertEquals(1, ability.getCooldown());

        NbtCompound tag = new NbtCompound();
        ability.writeToNbt(tag, null);

        AbilityPlayerComponent restored = new AbilityPlayerComponent(null);
        restored.readFromNbt(tag, null);

        assertEquals(1, restored.getCooldown());

        restored.reset();

        assertEquals(0, restored.getCooldown());
    }

    @Test
    public void configDefaultsPersistAndReset() {
        ConfigWorldComponent config = new ConfigWorldComponent(null);
        config.insaneSeesMorphs = false;
        config.naturalVoodoosAllowed = true;

        NbtCompound tag = new NbtCompound();
        config.writeToNbt(tag, null);

        ConfigWorldComponent restored = new ConfigWorldComponent(null);
        restored.readFromNbt(tag, null);

        assertFalse(restored.insaneSeesMorphs);
        assertTrue(restored.naturalVoodoosAllowed);

        restored.reset();

        assertFalse(restored.insaneSeesMorphs);
        assertTrue(restored.naturalVoodoosAllowed);
    }

    @Test
    public void worldMusicStartsStopsAndHandlesUnknownMomentNames() {
        WorldMusicComponent music = new WorldMusicComponent(null);
        music.startMusic(MusicMomentType.JESTER_MOMENT, 2);

        NbtCompound tag = new NbtCompound();
        music.writeToNbt(tag, null);

        WorldMusicComponent restored = new WorldMusicComponent(null);
        restored.readFromNbt(tag, null);

        assertEquals(MusicMomentType.JESTER_MOMENT, restored.getCurrentMoment());
        assertEquals(2, restored.getMusicIndex());
        assertEquals(MusicMomentType.NONE, MusicMomentType.fromString("missing"));

        restored.stopMusic();

        assertEquals(MusicMomentType.NONE, restored.getCurrentMoment());
        assertEquals(0, restored.getMusicIndex());
    }

    @Test
    public void hiddenBodiesTrackPersistAndResetBodyIds() {
        UUID bodyUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        HiddenBodiesWorldComponent hiddenBodies = new HiddenBodiesWorldComponent(null);

        assertTrue(hiddenBodies.addHiddenBody(bodyUuid));
        assertFalse(hiddenBodies.addHiddenBody(bodyUuid));
        assertTrue(hiddenBodies.isHidden(bodyUuid));

        NbtCompound tag = new NbtCompound();
        hiddenBodies.writeToNbt(tag, null);

        HiddenBodiesWorldComponent restored = new HiddenBodiesWorldComponent(null);
        restored.readFromNbt(tag, null);

        assertTrue(restored.isHidden(bodyUuid));

        restored.reset();

        assertFalse(restored.isHidden(bodyUuid));
    }

    @Test
    public void killHistoryTracksRecentNonImmuneKillsAndPersists() {
        UUID killerUuid = UUID.fromString("00000000-0000-0000-0000-000000000010");
        UUID victimUuid = UUID.fromString("00000000-0000-0000-0000-000000000011");
        Identifier deathReason = Identifier.of("advancedspark_test", "knife");
        KillHistoryWorldComponent killHistory = new KillHistoryWorldComponent(null);

        killHistory.recordKill(killerUuid, victimUuid, deathReason, 100L);

        assertTrue(killHistory.hasRecentNonImmuneKill(killerUuid, 50, 125L));
        assertFalse(killHistory.hasRecentNonImmuneKill(killerUuid, 20, 125L));

        killHistory.addImmuneDeathReason(deathReason);

        assertFalse(killHistory.hasRecentNonImmuneKill(killerUuid, 50, 125L));

        NbtCompound tag = new NbtCompound();
        killHistory.writeToNbt(tag, null);

        KillHistoryWorldComponent restored = new KillHistoryWorldComponent(null);
        restored.readFromNbt(tag, null);

        assertEquals(1, restored.records().size());
        assertEquals(victimUuid, restored.records().getFirst().victimUuid());

        restored.pruneExpired(100L + KillHistoryWorldComponent.DEFAULT_LOOKBACK_TICKS + 1L);

        assertTrue(restored.records().isEmpty());
    }
}
