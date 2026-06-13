package dev.doctor4t.wathe.cca;

import dev.doctor4t.wathe.Wathe;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkStaminaRules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/**
 * English: Spark-wathe compatible player stamina component.
 * Chinese: 兼容 Spark-wathe 的玩家体力组件。
 */
public class PlayerStaminaComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<PlayerStaminaComponent> KEY =
            ComponentRegistry.getOrCreate(Wathe.id("stamina"), PlayerStaminaComponent.class);

    private final PlayerEntity player;
    private float sprintingTicks = 0.0f;
    private int maxSprintTime = -1;
    private boolean exhausted;
    private float lastSyncedValue = -1.0f;
    private int lastSyncedMaxTime = -1;
    private boolean lastSyncedExhausted;

    public PlayerStaminaComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(this.sprintingTicks);
        buf.writeVarInt(this.maxSprintTime);
        buf.writeBoolean(this.exhausted);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.sprintingTicks = buf.readFloat();
        this.maxSprintTime = buf.readVarInt();
        this.exhausted = buf.readBoolean();
    }

    public float getSprintingTicks() {
        return this.sprintingTicks;
    }

    public void setSprintingTicks(float sprintingTicks) {
        this.sprintingTicks = sprintingTicks;
    }

    public int getMaxSprintTime() {
        return this.maxSprintTime;
    }

    public void setMaxSprintTime(int maxSprintTime) {
        this.maxSprintTime = maxSprintTime;
    }

    public boolean isExhausted() {
        return this.exhausted;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
    }

    public int getExhaustionRecoveryTicks() {
        return AdvancedSparkStaminaRules.EXHAUSTION_RECOVERY_TICKS;
    }

    public boolean isInfiniteStamina() {
        return this.maxSprintTime < 0 || this.sprintingTicks < 0.0f || this.sprintingTicks >= Integer.MAX_VALUE;
    }

    public AdvancedSparkStaminaRules.State state() {
        return new AdvancedSparkStaminaRules.State(this.sprintingTicks, this.maxSprintTime, this.exhausted);
    }

    public void apply(AdvancedSparkStaminaRules.State state) {
        this.sprintingTicks = state.sprintingTicks();
        this.maxSprintTime = state.maxSprintTime();
        this.exhausted = state.exhausted();
    }

    public void reset() {
        this.sprintingTicks = 0.0f;
        this.maxSprintTime = -1;
        this.exhausted = false;
        this.lastSyncedValue = -1.0f;
        this.lastSyncedMaxTime = -1;
        this.lastSyncedExhausted = false;
        this.sync();
    }

    @Override
    public void serverTick() {
        boolean needsSync = Math.abs(this.sprintingTicks - this.lastSyncedValue) >= 10.0f
                || this.maxSprintTime != this.lastSyncedMaxTime
                || this.exhausted != this.lastSyncedExhausted;
        if (!needsSync) {
            return;
        }

        this.sync();
        this.lastSyncedValue = this.sprintingTicks;
        this.lastSyncedMaxTime = this.maxSprintTime;
        this.lastSyncedExhausted = this.exhausted;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putFloat("sprintingTicks", this.sprintingTicks);
        tag.putInt("maxSprintTime", this.maxSprintTime);
        tag.putBoolean("exhausted", this.exhausted);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.sprintingTicks = tag.contains("sprintingTicks", NbtElement.FLOAT_TYPE) ? tag.getFloat("sprintingTicks") : 0.0f;
        this.maxSprintTime = tag.contains("maxSprintTime", NbtElement.INT_TYPE) ? tag.getInt("maxSprintTime") : -1;
        this.exhausted = tag.contains("exhausted") && tag.getBoolean("exhausted");
    }
}
