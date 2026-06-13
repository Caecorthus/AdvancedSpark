package io.github.caecorthus.advancedspark.state;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;

/**
 * English: Lightweight round state until a persisted component backend is chosen.
 * Chinese: 在选择持久化组件后端前使用的轻量回合状态。
 */
public final class AdvancedSparkGameState {
    private final Set<Identifier> enabledRoles = ConcurrentHashMap.newKeySet();
    private final Set<Identifier> disabledRoles = ConcurrentHashMap.newKeySet();
    private final Set<UUID> deadPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Identifier> mapVotes = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerRoundState> playerRoundStates = new ConcurrentHashMap<>();
    private final Map<Integer, RoomData> rooms = new ConcurrentHashMap<>();
    private final Map<UUID, GameProfile> gameProfiles = new ConcurrentHashMap<>();
    private volatile Identifier neutralWinner;

    public void setRoleEnabled(Identifier roleId, boolean enabled) {
        Objects.requireNonNull(roleId, "roleId");
        if (enabled) {
            enabledRoles.add(roleId);
        } else {
            enabledRoles.remove(roleId);
        }
    }

    public boolean isRoleEnabled(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return enabledRoles.contains(roleId);
    }

    public Set<Identifier> enabledRoles() {
        return Set.copyOf(enabledRoles);
    }

    public void setRoleDisabled(Identifier roleId, boolean disabled) {
        Objects.requireNonNull(roleId, "roleId");
        if (disabled) {
            disabledRoles.add(roleId);
        } else {
            disabledRoles.remove(roleId);
        }
    }

    public boolean isRoleDisabled(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return disabledRoles.contains(roleId);
    }

    public Set<Identifier> disabledRoles() {
        return Set.copyOf(disabledRoles);
    }

    public void markDead(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        deadPlayers.add(playerId);
    }

    public void markAlive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        deadPlayers.remove(playerId);
    }

    public boolean isDead(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return deadPlayers.contains(playerId);
    }

    public Set<UUID> deadPlayers() {
        return Set.copyOf(deadPlayers);
    }

    public void resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        deadPlayers.remove(playerId);
        mapVotes.remove(playerId);
        playerRoundStates.remove(playerId);
    }

    public void setNeutralWinner(Identifier winnerId) {
        neutralWinner = Objects.requireNonNull(winnerId, "winnerId");
    }

    public void clearNeutralWinner() {
        neutralWinner = null;
    }

    public Optional<Identifier> neutralWinner() {
        return Optional.ofNullable(neutralWinner);
    }

    public void recordMapVote(UUID playerId, Identifier mapId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(mapId, "mapId");
        mapVotes.put(playerId, mapId);
    }

    public void clearMapVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        mapVotes.remove(playerId);
    }

    public Map<UUID, Identifier> mapVotes() {
        return Map.copyOf(mapVotes);
    }

    public RoomData getOrCreateRoom(int roomIndex, String roomName) {
        if (roomIndex < 0) {
            throw new IllegalArgumentException("roomIndex cannot be negative");
        }
        Objects.requireNonNull(roomName, "roomName");
        return rooms.computeIfAbsent(roomIndex, ignored -> new RoomData(roomIndex, roomName));
    }

    public Optional<RoomData> room(int roomIndex) {
        return Optional.ofNullable(rooms.get(roomIndex));
    }

    public Map<Integer, RoomData> rooms() {
        return Map.copyOf(rooms);
    }

    public void clearRooms() {
        rooms.clear();
    }

    public void putGameProfile(GameProfile profile) {
        Objects.requireNonNull(profile, "profile");
        gameProfiles.put(profile.getId(), profile);
    }

    public Map<UUID, GameProfile> gameProfiles() {
        return Map.copyOf(gameProfiles);
    }

    public PlayerRoundState playerState(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerRoundStates.computeIfAbsent(playerId, PlayerRoundState::new);
    }

    public PlayerRoundState setStaminaPlaceholder(UUID playerId, boolean active) {
        Objects.requireNonNull(playerId, "playerId");
        return playerRoundStates.compute(playerId, (id, existing) -> {
            PlayerRoundState state = existing == null ? new PlayerRoundState(id) : existing;
            return state.withStaminaActive(active);
        });
    }

    public PlayerRoundState setVeteranPlaceholder(UUID playerId, boolean active) {
        Objects.requireNonNull(playerId, "playerId");
        return playerRoundStates.compute(playerId, (id, existing) -> {
            PlayerRoundState state = existing == null ? new PlayerRoundState(id) : existing;
            return state.withVeteranAlertActive(active);
        });
    }

    public PlayerRoundState setStaminaTicks(UUID playerId, int ticksRemaining, int maxTicks) {
        Objects.requireNonNull(playerId, "playerId");
        if (ticksRemaining < 0 || maxTicks < 0) {
            throw new IllegalArgumentException("stamina ticks cannot be negative");
        }
        return playerRoundStates.compute(playerId, (id, existing) -> {
            PlayerRoundState state = existing == null ? new PlayerRoundState(id) : existing;
            return state.withStaminaTicks(Math.min(ticksRemaining, maxTicks), maxTicks);
        });
    }

    public PlayerRoundState initializeVeteranStabs(UUID playerId, int stabsRemaining) {
        Objects.requireNonNull(playerId, "playerId");
        if (stabsRemaining < 0) {
            throw new IllegalArgumentException("stabsRemaining cannot be negative");
        }
        return playerRoundStates.compute(playerId, (id, existing) -> {
            PlayerRoundState state = existing == null ? new PlayerRoundState(id) : existing;
            return state.withVeteranStabs(stabsRemaining);
        });
    }

    public boolean useVeteranStab(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        AtomicBoolean used = new AtomicBoolean(false);
        playerRoundStates.compute(playerId, (id, existing) -> {
            PlayerRoundState state = existing == null ? new PlayerRoundState(id) : existing;
            if (state.veteranStabsRemaining() <= 0) {
                return state;
            }
            used.set(true);
            return state.withVeteranStabs(state.veteranStabsRemaining() - 1);
        });
        return used.get();
    }

    public Map<UUID, PlayerRoundState> playerRoundStates() {
        return Map.copyOf(playerRoundStates);
    }

    public void resetRound() {
        deadPlayers.clear();
        mapVotes.clear();
        playerRoundStates.clear();
        rooms.clear();
        gameProfiles.clear();
        clearNeutralWinner();
    }

    /**
     * English: Reserved per-player flags for stamina and veteran mechanics.
     * Chinese: 为耐力与老兵机制预留的玩家级标记。
     */
    public record PlayerRoundState(
            UUID playerId,
            boolean staminaActive,
            boolean veteranAlertActive,
            int staminaTicksRemaining,
            int maxStaminaTicks,
            int veteranStabsRemaining
    ) {
        public PlayerRoundState(UUID playerId) {
            this(playerId, false, false, 0, 0, 0);
        }

        public PlayerRoundState {
            Objects.requireNonNull(playerId, "playerId");
            if (staminaTicksRemaining < 0 || maxStaminaTicks < 0 || veteranStabsRemaining < 0) {
                throw new IllegalArgumentException("round state counters cannot be negative");
            }
        }

        public PlayerRoundState withStaminaActive(boolean staminaActive) {
            return new PlayerRoundState(playerId, staminaActive, veteranAlertActive, staminaTicksRemaining, maxStaminaTicks, veteranStabsRemaining);
        }

        public PlayerRoundState withVeteranAlertActive(boolean veteranAlertActive) {
            return new PlayerRoundState(playerId, staminaActive, veteranAlertActive, staminaTicksRemaining, maxStaminaTicks, veteranStabsRemaining);
        }

        public PlayerRoundState withStaminaTicks(int staminaTicksRemaining, int maxStaminaTicks) {
            return new PlayerRoundState(playerId, staminaTicksRemaining > 0, veteranAlertActive, staminaTicksRemaining, maxStaminaTicks, veteranStabsRemaining);
        }

        public PlayerRoundState withVeteranStabs(int veteranStabsRemaining) {
            return new PlayerRoundState(playerId, staminaActive, veteranStabsRemaining > 0, staminaTicksRemaining, maxStaminaTicks, veteranStabsRemaining);
        }
    }

    /**
     * English: Room assignment side state for Spark-wathe room-aware role logic.
     * Chinese: 面向 Spark-wathe 房间感知职业逻辑的房间分配侧状态。
     */
    public static final class RoomData {
        private final int index;
        private final String name;
        private final Set<UUID> players = ConcurrentHashMap.newKeySet();

        private RoomData(int index, String name) {
            this.index = index;
            this.name = Objects.requireNonNull(name, "name");
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return this.name;
        }

        public Set<UUID> getPlayers() {
            return Set.copyOf(this.players);
        }

        public boolean addPlayer(UUID playerId) {
            return this.players.add(Objects.requireNonNull(playerId, "playerId"));
        }

        public boolean hasPlayer(UUID playerId) {
            return this.players.contains(Objects.requireNonNull(playerId, "playerId"));
        }
    }
}
