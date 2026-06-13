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
 * English: World-scoped toggles used by migrated role logic.
 * Chinese: 已迁移职业逻辑使用的世界级开关。
 */
public class ConfigWorldComponent implements AutoSyncedComponent, AdvancedSparkResettableComponent {
    public static final ComponentKey<ConfigWorldComponent> KEY =
            AdvancedSparkComponentKeyFactory.create(AdvancedSparkConstants.id("config"), ConfigWorldComponent.class);

    private final @Nullable World world;
    public boolean insaneSeesMorphs = true;
    public boolean naturalVoodoosAllowed = false;

    public ConfigWorldComponent(@Nullable World world) {
        this.world = world;
    }

    public void sync() {
        if (this.world != null && KEY != null) {
            KEY.sync(this.world);
        }
    }

    @Override
    public void reset() {
        this.sync();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(this.insaneSeesMorphs);
        buf.writeBoolean(this.naturalVoodoosAllowed);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.insaneSeesMorphs = buf.readBoolean();
        this.naturalVoodoosAllowed = buf.readBoolean();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("insaneSeesMorphs", this.insaneSeesMorphs);
        tag.putBoolean("naturalVoodoosAllowed", this.naturalVoodoosAllowed);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("insaneSeesMorphs")) {
            this.insaneSeesMorphs = tag.getBoolean("insaneSeesMorphs");
        }
        if (tag.contains("naturalVoodoosAllowed")) {
            this.naturalVoodoosAllowed = tag.getBoolean("naturalVoodoosAllowed");
        }
    }
}
