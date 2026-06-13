package io.github.caecorthus.advancedspark.component;

import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

/**
 * English: World-level music state used by migrated role moments.
 * Chinese: 已迁移职业时刻使用的世界级音乐状态。
 */
public class WorldMusicComponent implements AutoSyncedComponent, AdvancedSparkResettableComponent {
    public static final ComponentKey<WorldMusicComponent> KEY =
            AdvancedSparkComponentKeyFactory.create(AdvancedSparkConstants.id("world_music"), WorldMusicComponent.class);

    private final @Nullable World world;
    private MusicMomentType currentMoment = MusicMomentType.NONE;
    private int musicIndex;

    public WorldMusicComponent(@Nullable World world) {
        this.world = world;
    }

    public void startMusic(MusicMomentType type, int index) {
        if (this.currentMoment != type || this.musicIndex != index) {
            this.currentMoment = type;
            this.musicIndex = index;
            this.sync();
        }
    }

    public void stopMusic() {
        if (this.currentMoment != MusicMomentType.NONE || this.musicIndex != 0) {
            this.currentMoment = MusicMomentType.NONE;
            this.musicIndex = 0;
            this.sync();
        }
    }

    public MusicMomentType getCurrentMoment() {
        return this.currentMoment;
    }

    public int getMusicIndex() {
        return this.musicIndex;
    }

    public void sync() {
        if (this.world != null && KEY != null) {
            KEY.sync(this.world);
        }
    }

    @Override
    public void reset() {
        this.stopMusic();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeString(this.currentMoment.name());
        buf.writeInt(this.musicIndex);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.currentMoment = MusicMomentType.fromString(buf.readString());
        this.musicIndex = buf.readInt();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("currentMoment", this.currentMoment.name());
        tag.putInt("musicIndex", this.musicIndex);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.currentMoment = tag.contains("currentMoment")
                ? MusicMomentType.fromString(tag.getString("currentMoment"))
                : MusicMomentType.NONE;
        this.musicIndex = tag.contains("musicIndex") ? tag.getInt("musicIndex") : 0;
    }
}
