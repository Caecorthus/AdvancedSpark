package dev.doctor4t.wathe.cca;

import dev.doctor4t.wathe.Wathe;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * English: Spark-wathe map voting state component without the later map-teleport wiring.
 * Chinese: Spark-wathe 地图投票状态组件，暂不包含后续地图传送接线。
 */
public class MapVotingComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<MapVotingComponent> KEY =
            SparkWatheComponentKeyFactory.create(Wathe.id("map_voting"), MapVotingComponent.class);

    private static final int VOTING_DURATION_TICKS = 30 * 20;
    private static final int ROULETTE_DURATION_TICKS = 8 * 20;
    private static final int ALL_VOTED_REMAINING_TICKS = 5 * 20;

    private final Scoreboard scoreboard;
    @Nullable
    private final MinecraftServer server;
    private final Random random = new Random();

    private boolean votingActive;
    private int votingTicksRemaining;
    private final List<VotingMapEntry> availableMaps = new ArrayList<>();
    private final List<UnavailableMapEntry> unavailableMaps = new ArrayList<>();
    private int[] voteCounts = new int[0];
    private final Map<UUID, Integer> playerVotes = new HashMap<>();
    private int selectedMapIndex = -1;
    private boolean roulettePhase;
    private int rouletteTicksRemaining;
    @Nullable
    private Identifier lastSelectedDimension;

    public record VotingMapEntry(
            Identifier dimensionId,
            String displayName,
            String description,
            int minPlayers,
            int maxPlayers
    ) {
    }

    public record UnavailableMapEntry(
            Identifier dimensionId,
            String displayName,
            String reason
    ) {
    }

    public MapVotingComponent(Scoreboard scoreboard, @Nullable MinecraftServer server) {
        this.scoreboard = scoreboard;
        this.server = server;
    }

    public void sync() {
        if (KEY != null && this.scoreboard != null) {
            KEY.sync(this.scoreboard);
        }
    }

    public boolean isVotingActive() {
        return this.votingActive;
    }

    public int getVotingTicksRemaining() {
        return this.votingTicksRemaining;
    }

    public List<VotingMapEntry> getAvailableMaps() {
        return this.availableMaps;
    }

    public List<UnavailableMapEntry> getUnavailableMaps() {
        return this.unavailableMaps;
    }

    public int[] getVoteCounts() {
        return this.voteCounts;
    }

    public int getVotedMapIndex(UUID playerId) {
        return this.playerVotes.getOrDefault(playerId, -1);
    }

    public int getPlayerVoteCount() {
        return this.playerVotes.size();
    }

    public int getSelectedMapIndex() {
        return this.selectedMapIndex;
    }

    public boolean isRoulettePhase() {
        return this.roulettePhase;
    }

    public int getRouletteTicksRemaining() {
        return this.rouletteTicksRemaining;
    }

    @Nullable
    public Identifier getLastSelectedDimension() {
        return this.lastSelectedDimension;
    }

    public void setLastSelectedDimensionDirect(@Nullable Identifier dimensionId) {
        this.lastSelectedDimension = dimensionId;
        this.sync();
    }

    public void startVoting() {
        this.votingActive = !this.availableMaps.isEmpty();
        this.votingTicksRemaining = this.votingActive ? VOTING_DURATION_TICKS : 0;
        this.playerVotes.clear();
        this.voteCounts = new int[this.availableMaps.size()];
        this.selectedMapIndex = -1;
        this.roulettePhase = false;
        this.rouletteTicksRemaining = 0;
        this.sync();
    }

    public void castVote(UUID playerId, int mapIndex) {
        if (!this.votingActive || this.roulettePhase || mapIndex < 0 || mapIndex >= this.availableMaps.size()) {
            return;
        }

        Integer oldVote = this.playerVotes.put(playerId, mapIndex);
        if (oldVote != null && oldVote >= 0 && oldVote < this.voteCounts.length) {
            this.voteCounts[oldVote] = Math.max(0, this.voteCounts[oldVote] - 1);
        }
        this.voteCounts[mapIndex]++;

        if (this.server != null && this.votingTicksRemaining > ALL_VOTED_REMAINING_TICKS
                && this.playerVotes.size() >= this.server.getCurrentPlayerCount()) {
            this.votingTicksRemaining = ALL_VOTED_REMAINING_TICKS;
        }
        this.sync();
    }

    public void reset() {
        this.votingActive = false;
        this.votingTicksRemaining = 0;
        this.availableMaps.clear();
        this.unavailableMaps.clear();
        this.voteCounts = new int[0];
        this.playerVotes.clear();
        this.selectedMapIndex = -1;
        this.roulettePhase = false;
        this.rouletteTicksRemaining = 0;
        this.sync();
    }

    public void onPlayerJoin() {
        if (this.votingActive && !this.roulettePhase && this.votingTicksRemaining <= 0) {
            this.votingTicksRemaining = VOTING_DURATION_TICKS;
            this.sync();
        }
    }

    @Override
    public void serverTick() {
        if (!this.votingActive) {
            return;
        }

        if (this.roulettePhase) {
            if (--this.rouletteTicksRemaining <= 0) {
                this.finishSelection();
            }
            return;
        }

        if (--this.votingTicksRemaining <= 0) {
            this.endVoting();
        } else if (this.votingTicksRemaining % 20 == 0) {
            this.sync();
        }
    }

    private void endVoting() {
        this.selectedMapIndex = this.selectMapWeighted();
        if (this.selectedMapIndex < 0) {
            this.reset();
            return;
        }
        this.roulettePhase = true;
        this.rouletteTicksRemaining = ROULETTE_DURATION_TICKS;
        this.sync();
    }

    private int selectMapWeighted() {
        if (this.availableMaps.isEmpty()) {
            return -1;
        }

        boolean hasAnyVotes = false;
        int totalWeight = 0;
        int[] weights = new int[this.availableMaps.size()];
        for (int voteCount : this.voteCounts) {
            if (voteCount > 0) {
                hasAnyVotes = true;
                break;
            }
        }
        for (int index = 0; index < weights.length; index++) {
            weights[index] = hasAnyVotes ? this.voteCounts[index] : 1;
            totalWeight += weights[index];
        }

        int roll = this.random.nextInt(Math.max(1, totalWeight));
        int cumulative = 0;
        for (int index = 0; index < weights.length; index++) {
            cumulative += weights[index];
            if (roll < cumulative) {
                return index;
            }
        }
        return 0;
    }

    private void finishSelection() {
        if (this.selectedMapIndex < 0 || this.selectedMapIndex >= this.availableMaps.size()) {
            this.reset();
            return;
        }
        this.lastSelectedDimension = this.availableMaps.get(this.selectedMapIndex).dimensionId();
        this.votingActive = false;
        this.sync();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.votingActive = tag.getBoolean("VotingActive");
        this.votingTicksRemaining = tag.getInt("VotingTicksRemaining");
        this.selectedMapIndex = tag.getInt("SelectedMapIndex");
        this.roulettePhase = tag.getBoolean("RoulettePhase");
        this.rouletteTicksRemaining = tag.getInt("RouletteTicksRemaining");
        this.lastSelectedDimension = tag.contains("LastSelectedDimension", NbtElement.STRING_TYPE)
                ? Identifier.tryParse(tag.getString("LastSelectedDimension"))
                : null;

        this.availableMaps.clear();
        if (tag.contains("AvailableMaps", NbtElement.LIST_TYPE)) {
            NbtList maps = tag.getList("AvailableMaps", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : maps) {
                this.readAvailableMap((NbtCompound) element);
            }
        }

        this.unavailableMaps.clear();
        if (tag.contains("UnavailableMaps", NbtElement.LIST_TYPE)) {
            NbtList maps = tag.getList("UnavailableMaps", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : maps) {
                this.readUnavailableMap((NbtCompound) element);
            }
        }

        this.voteCounts = tag.contains("VoteCounts", NbtElement.INT_ARRAY_TYPE)
                ? tag.getIntArray("VoteCounts")
                : new int[this.availableMaps.size()];

        this.playerVotes.clear();
        if (tag.contains("PlayerVotes", NbtElement.LIST_TYPE)) {
            NbtList votes = tag.getList("PlayerVotes", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : votes) {
                NbtCompound vote = (NbtCompound) element;
                this.playerVotes.put(vote.getUuid("PlayerId"), vote.getInt("MapIndex"));
            }
        }
    }

    private void readAvailableMap(NbtCompound tag) {
        Identifier dimensionId = Identifier.tryParse(tag.getString("DimensionId"));
        if (dimensionId == null) {
            return;
        }
        this.availableMaps.add(new VotingMapEntry(
                dimensionId,
                tag.getString("DisplayName"),
                tag.getString("Description"),
                tag.getInt("MinPlayers"),
                tag.getInt("MaxPlayers")
        ));
    }

    private void readUnavailableMap(NbtCompound tag) {
        Identifier dimensionId = Identifier.tryParse(tag.getString("DimensionId"));
        if (dimensionId == null) {
            return;
        }
        this.unavailableMaps.add(new UnavailableMapEntry(
                dimensionId,
                tag.getString("DisplayName"),
                tag.getString("Reason")
        ));
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putBoolean("VotingActive", this.votingActive);
        tag.putInt("VotingTicksRemaining", this.votingTicksRemaining);
        tag.putInt("SelectedMapIndex", this.selectedMapIndex);
        tag.putBoolean("RoulettePhase", this.roulettePhase);
        tag.putInt("RouletteTicksRemaining", this.rouletteTicksRemaining);
        if (this.lastSelectedDimension != null) {
            tag.putString("LastSelectedDimension", this.lastSelectedDimension.toString());
        }

        NbtList available = new NbtList();
        for (VotingMapEntry entry : this.availableMaps) {
            NbtCompound map = new NbtCompound();
            map.putString("DimensionId", entry.dimensionId().toString());
            map.putString("DisplayName", entry.displayName());
            map.putString("Description", entry.description());
            map.putInt("MinPlayers", entry.minPlayers());
            map.putInt("MaxPlayers", entry.maxPlayers());
            available.add(map);
        }
        tag.put("AvailableMaps", available);

        NbtList unavailable = new NbtList();
        for (UnavailableMapEntry entry : this.unavailableMaps) {
            NbtCompound map = new NbtCompound();
            map.putString("DimensionId", entry.dimensionId().toString());
            map.putString("DisplayName", entry.displayName());
            map.putString("Reason", entry.reason());
            unavailable.add(map);
        }
        tag.put("UnavailableMaps", unavailable);

        tag.putIntArray("VoteCounts", this.voteCounts);

        NbtList votes = new NbtList();
        for (Map.Entry<UUID, Integer> entry : this.playerVotes.entrySet()) {
            NbtCompound vote = new NbtCompound();
            vote.putUuid("PlayerId", entry.getKey());
            vote.putInt("MapIndex", entry.getValue());
            votes.add(vote);
        }
        tag.put("PlayerVotes", votes);
    }
}
