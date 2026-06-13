package io.github.caecorthus.advancedspark.bridge;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import io.github.caecorthus.advancedspark.state.AdvancedSparkComponents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * English: Stores Spark-wathe style rich round-end data without replacing original Wathe classes.
 * Chinese: 在不替换原版 Wathe 类的前提下保存 Spark-wathe 风格的富回合结算数据。
 */
public final class AdvancedSparkRoundEndBridge {
    private AdvancedSparkRoundEndBridge() {
    }

    public static List<RoundEndData> getPlayers(GameRoundEndComponent component) {
        return List.copyOf(access(component).advancedspark$getPlayers());
    }

    public static GameFunctions.WinStatus getWinStatus(GameRoundEndComponent component) {
        return access(component).advancedspark$getWinStatus();
    }

    @Nullable
    public static GameMode getRoundGameMode(GameRoundEndComponent component) {
        Identifier gameModeId = access(component).advancedspark$getGameModeId();
        return gameModeId == null ? null : WatheGameModes.GAME_MODES.get(gameModeId);
    }

    public static void captureRoundEndData(ServerWorld world, GameFunctions.WinStatus winStatus) {
        captureRoundEndData(world, winStatus, null);
    }

    public static void captureRoundEndData(ServerWorld world, UUID neutralWinner) {
        captureRoundEndData(world, GameFunctions.WinStatus.LOOSE_END, Objects.requireNonNull(neutralWinner, "neutralWinner"));
    }

    private static void captureRoundEndData(
            ServerWorld world,
            GameFunctions.WinStatus winStatus,
            @Nullable UUID neutralWinner
    ) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(winStatus, "winStatus");
        GameWorldComponent gameComponent = GameWorldComponent.KEY.get(world);
        AdvancedSparkGameState state = AdvancedSparkComponents.get(world.getServer());
        List<RoundEndData> players = new ArrayList<>();

        for (Map.Entry<UUID, Role> entry : gameComponent.getRoles().entrySet()) {
            UUID playerId = entry.getKey();
            Role role = entry.getValue();
            if (role == null) {
                continue;
            }
            players.add(new RoundEndData(
                    profileFor(world, state, playerId),
                    role.identifier(),
                    endStatusFor(world, state, playerId),
                    isWinner(playerId, role, winStatus, neutralWinner)
            ));
        }

        Identifier gameModeId = gameComponent.getGameMode() == null ? null : gameComponent.getGameMode().identifier;
        access(GameRoundEndComponent.KEY.get(world)).advancedspark$setRoundEndData(players, winStatus, gameModeId);
    }

    private static GameProfile profileFor(ServerWorld world, AdvancedSparkGameState state, UUID playerId) {
        GameProfile profile = state.gameProfiles().get(playerId);
        if (profile != null) {
            return profile;
        }
        PlayerEntity onlinePlayer = world.getPlayerByUuid(playerId);
        if (onlinePlayer != null) {
            return onlinePlayer.getGameProfile();
        }
        return new GameProfile(playerId, playerId.toString());
    }

    private static PlayerEndStatus endStatusFor(ServerWorld world, AdvancedSparkGameState state, UUID playerId) {
        boolean wasDead = state.isDead(playerId);
        boolean isOnline = world.getPlayerByUuid(playerId) != null;
        if (wasDead) {
            return isOnline ? PlayerEndStatus.DEAD : PlayerEndStatus.LEFT_DEAD;
        }
        return isOnline ? PlayerEndStatus.ALIVE : PlayerEndStatus.LEFT;
    }

    private static boolean isWinner(
            UUID playerId,
            Role role,
            GameFunctions.WinStatus winStatus,
            @Nullable UUID neutralWinner
    ) {
        if (neutralWinner != null) {
            return playerId.equals(neutralWinner);
        }
        return switch (winStatus) {
            case KILLERS -> role.canUseKiller();
            case PASSENGERS, TIME -> role.isInnocent();
            case NONE, LOOSE_END -> false;
        };
    }

    private static AdvancedSparkRoundEndAccess access(GameRoundEndComponent component) {
        Objects.requireNonNull(component, "component");
        if (component instanceof AdvancedSparkRoundEndAccess access) {
            return access;
        }
        throw new IllegalStateException("AdvancedSpark round-end mixin is not applied");
    }

    public enum PlayerEndStatus {
        ALIVE,
        DEAD,
        LEFT,
        LEFT_DEAD
    }

    public record RoundEndData(GameProfile player, Identifier role, PlayerEndStatus endStatus, boolean isWinner) {
        public RoundEndData {
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(role, "role");
            Objects.requireNonNull(endStatus, "endStatus");
        }

        public RoundEndData(@NotNull NbtCompound tag) {
            this(
                    new GameProfile(tag.getUuid("uuid"), tag.getString("name")),
                    Identifier.of(tag.getString("role")),
                    tag.contains("endStatus")
                            ? PlayerEndStatus.valueOf(tag.getString("endStatus"))
                            : (tag.getBoolean("wasDead") ? PlayerEndStatus.DEAD : PlayerEndStatus.ALIVE),
                    tag.getBoolean("isWinner")
            );
        }

        public @NotNull NbtCompound writeToNbt() {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("uuid", this.player.getId());
            tag.putString("name", this.player.getName());
            tag.putString("role", this.role.toString());
            tag.putString("endStatus", this.endStatus.name());
            tag.putBoolean("isWinner", this.isWinner);
            return tag;
        }

        public boolean wasDead() {
            return this.endStatus == PlayerEndStatus.DEAD || this.endStatus == PlayerEndStatus.LEFT_DEAD;
        }

        public boolean hasLeft() {
            return this.endStatus == PlayerEndStatus.LEFT || this.endStatus == PlayerEndStatus.LEFT_DEAD;
        }
    }
}
