package io.github.caecorthus.advancedspark.component;

import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/**
 * English: Shared cooldown component used by migrated active abilities.
 * Chinese: 已迁移主动能力共用的冷却组件。
 */
public class AbilityPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent, AdvancedSparkResettableComponent {
    public static final ComponentKey<AbilityPlayerComponent> KEY =
            AdvancedSparkComponentKeyFactory.create(AdvancedSparkConstants.id("ability"), AbilityPlayerComponent.class);

    private final @Nullable PlayerEntity player;
    public int cooldown;

    public AbilityPlayerComponent(@Nullable PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        if (this.player != null && KEY != null) {
            KEY.sync(this.player);
        }
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.cooldown);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.cooldown = buf.readInt();
    }

    @Override
    public void clientTick() {
        if (this.cooldown > 1) {
            this.cooldown--;
        }
    }

    @Override
    public void serverTick() {
        if (this.cooldown > 0) {
            this.cooldown--;
            if (this.cooldown % 20 == 0) {
                this.sync();
            }
        }
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int ticks) {
        this.cooldown = ticks;
        this.sync();
    }

    @Override
    public void reset() {
        this.cooldown = 0;
        this.sync();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("cooldown", this.cooldown);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;
    }
}
