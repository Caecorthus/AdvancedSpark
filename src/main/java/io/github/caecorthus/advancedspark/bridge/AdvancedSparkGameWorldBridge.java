package io.github.caecorthus.advancedspark.bridge;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import io.github.caecorthus.advancedspark.api.role.AdvancedSparkRoles;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * English: Migration helpers for Spark-wathe GameWorldComponent additions.
 * Chinese: 面向 Spark-wathe GameWorldComponent 新增能力的迁移辅助层。
 */
public final class AdvancedSparkGameWorldBridge {
    private AdvancedSparkGameWorldBridge() {
    }

    public static List<UUID> getAllPlayers(GameWorldComponent gameComponent) {
        Objects.requireNonNull(gameComponent, "gameComponent");
        return gameComponent.getRoles().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(java.util.Map.Entry::getKey)
                .toList();
    }

    public static boolean hasAnyRole(GameWorldComponent gameComponent, UUID playerId) {
        Objects.requireNonNull(gameComponent, "gameComponent");
        Objects.requireNonNull(playerId, "playerId");
        return gameComponent.getRole(playerId) != null;
    }

    public static RoomData addPlayerToRoom(AdvancedSparkGameState state, int roomIndex, String roomName, UUID playerId) {
        Objects.requireNonNull(state, "state");
        AdvancedSparkGameState.RoomData room = state.getOrCreateRoom(roomIndex, roomName);
        room.addPlayer(playerId);
        return RoomData.from(room);
    }

    public static Optional<RoomData> getRoom(AdvancedSparkGameState state, int roomIndex) {
        Objects.requireNonNull(state, "state");
        return state.room(roomIndex).map(RoomData::from);
    }

    public static HashMap<Integer, RoomData> getRooms(AdvancedSparkGameState state) {
        Objects.requireNonNull(state, "state");
        HashMap<Integer, RoomData> snapshot = new HashMap<>();
        state.rooms().forEach((index, room) -> snapshot.put(index, RoomData.from(room)));
        return snapshot;
    }

    public static int getPlayerRoomIndex(AdvancedSparkGameState state, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return getRooms(state).values().stream()
                .filter(room -> room.hasPlayer(playerId))
                .map(RoomData::getIndex)
                .findFirst()
                .orElse(-1);
    }

    public static void putGameProfile(AdvancedSparkGameState state, GameProfile profile) {
        Objects.requireNonNull(state, "state");
        state.putGameProfile(profile);
    }

    public static HashMap<UUID, GameProfile> getGameProfiles(AdvancedSparkGameState state) {
        Objects.requireNonNull(state, "state");
        return new HashMap<>(state.gameProfiles());
    }

    public static void setRoleEnabled(AdvancedSparkGameState state, Role role, boolean enabled) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(role, "role");
        state.setRoleDisabled(role.identifier(), !enabled);
    }

    public static void setMapSpecificRoleEnabled(AdvancedSparkGameState state, Role role, boolean enabled) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(role, "role");
        state.setRoleEnabled(role.identifier(), enabled);
    }

    public static boolean isRoleEnabled(AdvancedSparkGameState state, Role role) {
        Objects.requireNonNull(role, "role");
        return isRoleEnabled(state, role.identifier());
    }

    public static boolean isRoleEnabled(AdvancedSparkGameState state, AdvancedSparkRoles.RoleMetadata role) {
        Objects.requireNonNull(role, "role");
        return isRoleEnabled(state, role.roleId());
    }

    public static boolean isRoleEnabled(AdvancedSparkGameState state, Identifier roleId) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(roleId, "roleId");
        if (state.isRoleDisabled(roleId)) {
            return false;
        }
        return AdvancedSparkRoles.metadata(roleId)
                .map(metadata -> metadata.enabled() && (!metadata.mapSpecific() || state.isRoleEnabled(roleId)))
                .orElse(true);
    }

    /**
     * English: Spark-wathe-shaped room data returned by migration helpers.
     * Chinese: 迁移辅助方法返回的 Spark-wathe 形状房间数据。
     */
    public static final class RoomData {
        private final int index;
        private final String name;
        private final List<UUID> players;

        private RoomData(int index, String name, List<UUID> players) {
            this.index = index;
            this.name = Objects.requireNonNull(name, "name");
            this.players = List.copyOf(players);
        }

        private static RoomData from(AdvancedSparkGameState.RoomData room) {
            return new RoomData(room.getIndex(), room.getName(), List.copyOf(room.getPlayers()));
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return this.name;
        }

        public List<UUID> getPlayers() {
            return this.players;
        }

        public boolean hasPlayer(UUID playerId) {
            return this.players.contains(playerId);
        }
    }
}
