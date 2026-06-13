package dev.doctor4t.wathe.record;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * English: Minimal Spark-wathe record manager used by AdvancedSpark and migrated roles.
 * Chinese: 供 AdvancedSpark 与迁移职业使用的最小 Spark-wathe 对局记录管理器。
 */
public final class GameRecordManager {
    private static MatchRecord currentMatch;
    private static MatchRecord lastFinishedMatch;

    private GameRecordManager() {
    }

    public static synchronized boolean hasActiveMatch() {
        return currentMatch != null && currentMatch.active;
    }

    public static synchronized @Nullable MatchRecord getCurrentMatch() {
        return currentMatch;
    }

    public static synchronized @Nullable MatchRecord getLastFinishedMatch() {
        return lastFinishedMatch;
    }

    public static synchronized void startMatch(ServerWorld world, GameWorldComponent gameComponent) {
        if (hasActiveMatch()) {
            endMatch(world);
        }
        Identifier gameMode = gameComponent.getGameMode() == null
                ? Identifier.of("wathe", "unknown")
                : gameComponent.getGameMode().identifier;
        Identifier mapEffect = gameComponent.getMapEffect() == null
                ? Identifier.of("wathe", "unknown")
                : gameComponent.getMapEffect().identifier;
        currentMatch = new MatchRecord(
                UUID.randomUUID(),
                world.getRegistryKey().getValue(),
                gameMode,
                mapEffect,
                world.getTime(),
                System.currentTimeMillis()
        );
    }

    public static synchronized void endMatch(ServerWorld world) {
        if (!hasActiveMatch()) {
            return;
        }
        addEvent(world, GameRecordTypes.MATCH_END, null, null, new NbtCompound());
        currentMatch.active = false;
        lastFinishedMatch = currentMatch;
        currentMatch = null;
    }

    public static void recordItemUse(
            ServerPlayerEntity player,
            Identifier itemId,
            @Nullable ServerPlayerEntity target,
            @Nullable NbtCompound extra
    ) {
        NbtCompound data = extra == null ? new NbtCompound() : extra.copy();
        data.putString("item", itemId.toString());
        addEvent(player.getServerWorld(), GameRecordTypes.ITEM_USE, player, target, data);
    }

    public static void recordRoleAssigned(ServerPlayerEntity player, Role role) {
        NbtCompound playerData = new NbtCompound();
        playerData.putUuid("uuid", player.getUuid());
        playerData.putString("name", player.getGameProfile().getName());
        playerData.putString("role", role.identifier().toString());
        playerData.putInt("role_color", role.color());

        NbtCompound data = new NbtCompound();
        data.put("player", playerData);
        addEvent(player.getServerWorld(), GameRecordTypes.ROLE_ASSIGNED, player, null, data);
    }

    public static void recordPlatterTake(
            ServerPlayerEntity player,
            Identifier itemId,
            BlockPos platterPos,
            @Nullable String poisoner
    ) {
        recordPlatterTake(player, itemId, platterPos, poisoner, null);
    }

    public static void recordPlatterTake(
            ServerPlayerEntity player,
            Identifier itemId,
            BlockPos platterPos,
            @Nullable String poisoner,
            @Nullable NbtCompound extra
    ) {
        NbtCompound data = extra == null ? new NbtCompound() : extra.copy();
        data.putString("item", itemId.toString());
        putBlockPos(data, "pos", platterPos);
        if (poisoner != null) {
            data.putUuid("poisoner", UUID.fromString(poisoner));
        }
        addEvent(player.getServerWorld(), GameRecordTypes.PLATTER_TAKE, player, null, data);
    }

    public static void recordSkillUse(
            ServerPlayerEntity player,
            Identifier skillId,
            @Nullable ServerPlayerEntity target,
            @Nullable NbtCompound extra
    ) {
        NbtCompound data = extra == null ? new NbtCompound() : extra.copy();
        data.putString("skill", skillId.toString());
        addEvent(player.getServerWorld(), GameRecordTypes.SKILL_USE, player, target, data);
    }

    public static void recordGlobalEvent(
            ServerWorld world,
            Identifier eventId,
            @Nullable ServerPlayerEntity source,
            @Nullable NbtCompound extra
    ) {
        NbtCompound data = extra == null ? new NbtCompound() : extra.copy();
        data.putString("event", eventId.toString());
        addEvent(world, GameRecordTypes.GLOBAL_EVENT, source, null, data);
    }

    public static void recordDoorInteraction(
            ServerPlayerEntity player,
            BlockPos doorPos,
            String interactionType,
            String doorType,
            boolean success
    ) {
        NbtCompound data = new NbtCompound();
        data.putString("interaction_type", interactionType);
        data.putString("door_type", doorType);
        data.putBoolean("success", success);
        putBlockPos(data, "pos", doorPos);
        addEvent(player.getServerWorld(), GameRecordTypes.DOOR_INTERACTION, player, null, data);
    }

    public static void recordShopPurchase(ServerPlayerEntity player, ShopEntry entry, int index, int pricePaid) {
        NbtCompound data = new NbtCompound();
        data.putString("item", Registries.ITEM.getId(entry.stack().getItem()).toString());
        data.putInt("index", index);
        data.putInt("price", entry.price());
        data.putInt("price_paid", pricePaid);
        addEvent(player.getServerWorld(), GameRecordTypes.SHOP_PURCHASE, player, null, data);
    }

    public static void recordTaskComplete(ServerPlayerEntity player, String taskName) {
        NbtCompound data = new NbtCompound();
        data.putString("task", taskName);
        addEvent(player.getServerWorld(), GameRecordTypes.TASK_COMPLETE, player, null, data);
    }

    public static void recordDeath(ServerPlayerEntity victim, @Nullable ServerPlayerEntity killer, Identifier deathReason) {
        NbtCompound data = new NbtCompound();
        data.putString("death_reason", deathReason.toString());
        addEvent(victim.getServerWorld(), GameRecordTypes.DEATH, killer, victim, data);
    }

    public static void recordItemPickup(ServerPlayerEntity player, ItemStack stack, int count) {
        NbtCompound data = new NbtCompound();
        data.putString("item", Registries.ITEM.getId(stack.getItem()).toString());
        data.putInt("count", count);
        addEvent(player.getServerWorld(), GameRecordTypes.ITEM_PICKUP, player, null, data);
    }

    public static EventBuilder event(String type) {
        return new EventBuilder(type);
    }

    public static void putPos(NbtCompound data, String key, Vec3d pos) {
        NbtCompound posTag = new NbtCompound();
        posTag.putDouble("x", pos.x);
        posTag.putDouble("y", pos.y);
        posTag.putDouble("z", pos.z);
        data.put(key, posTag);
    }

    public static void putBlockPos(NbtCompound data, String key, BlockPos pos) {
        NbtCompound posTag = new NbtCompound();
        posTag.putInt("x", pos.getX());
        posTag.putInt("y", pos.getY());
        posTag.putInt("z", pos.getZ());
        data.put(key, posTag);
    }

    private static synchronized void addEvent(
            ServerWorld world,
            String type,
            @Nullable ServerPlayerEntity actor,
            @Nullable ServerPlayerEntity target,
            @Nullable NbtCompound data
    ) {
        if (!hasActiveMatch()) {
            return;
        }
        NbtCompound payload = data == null ? new NbtCompound() : data.copy();
        if (actor != null) {
            payload.putUuid("actor", actor.getUuid());
        }
        if (target != null) {
            payload.putUuid("target", target.getUuid());
        }
        currentMatch.addEvent(type, world.getTime(), System.currentTimeMillis(), payload);
    }

    public static final class EventBuilder {
        private final String type;
        private ServerWorld world;
        private ServerPlayerEntity actor;
        private ServerPlayerEntity target;
        private final NbtCompound data = new NbtCompound();

        private EventBuilder(String type) {
            this.type = type;
        }

        public EventBuilder world(ServerWorld world) {
            this.world = world;
            return this;
        }

        public EventBuilder actor(ServerPlayerEntity actor) {
            this.actor = actor;
            if (this.world == null && actor != null) {
                this.world = actor.getServerWorld();
            }
            return this;
        }

        public EventBuilder target(ServerPlayerEntity target) {
            this.target = target;
            return this;
        }

        public EventBuilder put(String key, String value) {
            this.data.putString(key, value);
            return this;
        }

        public EventBuilder putInt(String key, int value) {
            this.data.putInt(key, value);
            return this;
        }

        public EventBuilder putLong(String key, long value) {
            this.data.putLong(key, value);
            return this;
        }

        public EventBuilder putFloat(String key, float value) {
            this.data.putFloat(key, value);
            return this;
        }

        public EventBuilder putDouble(String key, double value) {
            this.data.putDouble(key, value);
            return this;
        }

        public EventBuilder putBool(String key, boolean value) {
            this.data.putBoolean(key, value);
            return this;
        }

        public EventBuilder putUuid(String key, UUID value) {
            this.data.putUuid(key, value);
            return this;
        }

        public EventBuilder putPos(String key, Vec3d pos) {
            GameRecordManager.putPos(this.data, key, pos);
            return this;
        }

        public EventBuilder putBlockPos(String key, BlockPos pos) {
            GameRecordManager.putBlockPos(this.data, key, pos);
            return this;
        }

        public EventBuilder putNbt(String key, NbtCompound nbt) {
            this.data.put(key, nbt.copy());
            return this;
        }

        public void record() {
            if (this.world != null) {
                addEvent(this.world, this.type, this.actor, this.target, this.data);
            }
        }
    }

    public static final class MatchRecord {
        private final UUID matchId;
        private final Identifier dimensionId;
        private final Identifier gameModeId;
        private final Identifier mapEffectId;
        private final long startTick;
        private final long startMs;
        private final List<GameRecordEvent> events = new ArrayList<>();
        private boolean active = true;
        private int nextSeq;

        private MatchRecord(
                UUID matchId,
                Identifier dimensionId,
                Identifier gameModeId,
                Identifier mapEffectId,
                long startTick,
                long startMs
        ) {
            this.matchId = matchId;
            this.dimensionId = dimensionId;
            this.gameModeId = gameModeId;
            this.mapEffectId = mapEffectId;
            this.startTick = startTick;
            this.startMs = startMs;
        }

        public UUID getMatchId() {
            return this.matchId;
        }

        public Identifier getDimensionId() {
            return this.dimensionId;
        }

        public Identifier getGameModeId() {
            return this.gameModeId;
        }

        public Identifier getMapEffectId() {
            return this.mapEffectId;
        }

        public long getStartTick() {
            return this.startTick;
        }

        public long getStartMs() {
            return this.startMs;
        }

        public List<GameRecordEvent> getEvents() {
            return Collections.unmodifiableList(this.events);
        }

        private void addEvent(String type, long worldTick, long realTimeMs, NbtCompound data) {
            this.events.add(new GameRecordEvent(this.matchId, this.nextSeq++, type, worldTick, realTimeMs, data));
        }
    }
}
