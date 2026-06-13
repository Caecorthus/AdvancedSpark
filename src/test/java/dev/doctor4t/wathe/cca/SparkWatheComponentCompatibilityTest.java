package dev.doctor4t.wathe.cca;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparkWatheComponentCompatibilityTest {
    @Test
    public void veteranComponentTracksLimitedStabUsesAndPersists() {
        PlayerVeteranComponent veteran = new PlayerVeteranComponent(null);

        veteran.initialize();
        assertEquals(PlayerVeteranComponent.MAX_STAB_USES, veteran.getStabUsesLeft());
        assertTrue(veteran.hasStabUsesLeft());
        assertTrue(veteran.useStab());

        NbtCompound tag = new NbtCompound();
        veteran.writeToNbt(tag, null);

        PlayerVeteranComponent restored = new PlayerVeteranComponent(null);
        restored.readFromNbt(tag, null);

        assertEquals(PlayerVeteranComponent.MAX_STAB_USES - 1, restored.getStabUsesLeft());
        restored.reset();
        assertFalse(restored.hasStabUsesLeft());
    }

    @Test
    public void mapVotingComponentRestoresVotingStateAndRecordsVotes() {
        MapVotingComponent voting = new MapVotingComponent(null, null);
        NbtCompound tag = votingTagWithTwoMaps();
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000123");

        voting.readFromNbt(tag, null);
        voting.castVote(playerId, 1);

        assertTrue(voting.isVotingActive());
        assertEquals(1, voting.getVotedMapIndex(playerId));
        assertEquals(1, voting.getPlayerVoteCount());
        assertArrayEquals(new int[]{0, 1}, voting.getVoteCounts());

        Identifier selected = Identifier.of("wathe", "map_two");
        voting.setLastSelectedDimensionDirect(selected);

        NbtCompound saved = new NbtCompound();
        voting.writeToNbt(saved, null);

        assertEquals(selected.toString(), saved.getString("LastSelectedDimension"));
    }

    private static NbtCompound votingTagWithTwoMaps() {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean("VotingActive", true);
        tag.putInt("VotingTicksRemaining", 200);
        tag.putInt("SelectedMapIndex", -1);
        tag.putBoolean("RoulettePhase", false);
        tag.putInt("RouletteTicksRemaining", 0);

        NbtList availableMaps = new NbtList();
        availableMaps.add(mapEntry("wathe:map_one", "Map One"));
        availableMaps.add(mapEntry("wathe:map_two", "Map Two"));
        tag.put("AvailableMaps", availableMaps);
        tag.putIntArray("VoteCounts", new int[]{0, 0});

        return tag;
    }

    private static NbtCompound mapEntry(String dimensionId, String displayName) {
        NbtCompound tag = new NbtCompound();
        tag.putString("DimensionId", dimensionId);
        tag.putString("DisplayName", displayName);
        tag.putString("Description", "");
        tag.putInt("MinPlayers", 1);
        tag.putInt("MaxPlayers", 16);
        return tag;
    }
}
