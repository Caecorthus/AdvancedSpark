package dev.doctor4t.wathe.cca;

import dev.doctor4t.wathe.Wathe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

/**
 * English: Spark-wathe Veteran component for limited knife stabs.
 * Chinese: Spark-wathe 老兵组件，用于限制刀击次数。
 */
public class PlayerVeteranComponent implements AutoSyncedComponent {
    public static final ComponentKey<PlayerVeteranComponent> KEY =
            SparkWatheComponentKeyFactory.create(Wathe.id("veteran"), PlayerVeteranComponent.class);
    public static final int MAX_STAB_USES = 2;

    private final PlayerEntity player;
    private int stabUsesLeft;

    public PlayerVeteranComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    public void sync() {
        if (KEY != null && this.player != null) {
            KEY.sync(this.player);
        }
    }

    public void reset() {
        this.stabUsesLeft = 0;
        this.sync();
    }

    public void initialize() {
        this.stabUsesLeft = MAX_STAB_USES;
        this.sync();
    }

    public int getStabUsesLeft() {
        return this.stabUsesLeft;
    }

    public boolean hasStabUsesLeft() {
        return this.stabUsesLeft > 0;
    }

    public boolean useStab() {
        if (this.stabUsesLeft <= 0) {
            return false;
        }
        this.stabUsesLeft--;
        this.sync();
        return true;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("StabUsesLeft", this.stabUsesLeft);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.stabUsesLeft = tag.contains("StabUsesLeft", NbtElement.INT_TYPE) ? tag.getInt("StabUsesLeft") : 0;
    }
}
