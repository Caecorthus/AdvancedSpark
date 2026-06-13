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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * English: World-level hidden body registry used by migrated cleanup and body-hiding roles.
 * Chinese: 已迁移清理/藏尸职业使用的世界级隐藏尸体登记表。
 */
public class HiddenBodiesWorldComponent implements AutoSyncedComponent, AdvancedSparkResettableComponent {
    public static final ComponentKey<HiddenBodiesWorldComponent> KEY =
            AdvancedSparkComponentKeyFactory.create(AdvancedSparkConstants.id("hidden_bodies"), HiddenBodiesWorldComponent.class);

    private final @Nullable World world;
    private final Set<UUID> hiddenBodies = new LinkedHashSet<>();

    public HiddenBodiesWorldComponent(@Nullable World world) {
        this.world = world;
    }

    public boolean addHiddenBody(UUID bodyUuid) {
        boolean added = this.hiddenBodies.add(bodyUuid);
        if (added) {
            this.sync();
        }
        return added;
    }

    public boolean isHidden(UUID bodyUuid) {
        return this.hiddenBodies.contains(bodyUuid);
    }

    public Set<UUID> hiddenBodies() {
        return Collections.unmodifiableSet(this.hiddenBodies);
    }

    public void sync() {
        if (this.world != null && KEY != null) {
            KEY.sync(this.world);
        }
    }

    @Override
    public void reset() {
        if (!this.hiddenBodies.isEmpty()) {
            this.hiddenBodies.clear();
            this.sync();
        }
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.hiddenBodies.size());
        for (UUID bodyUuid : this.hiddenBodies) {
            buf.writeUuid(bodyUuid);
        }
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.hiddenBodies.clear();
        int count = buf.readInt();
        for (int index = 0; index < count; index++) {
            this.hiddenBodies.add(buf.readUuid());
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound hiddenBodiesTag = new NbtCompound();
        hiddenBodiesTag.putInt("count", this.hiddenBodies.size());
        int index = 0;
        for (UUID bodyUuid : this.hiddenBodies) {
            hiddenBodiesTag.putUuid("body_" + index, bodyUuid);
            index++;
        }
        tag.put("hiddenBodies", hiddenBodiesTag);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.hiddenBodies.clear();
        if (!tag.contains("hiddenBodies")) {
            return;
        }

        NbtCompound hiddenBodiesTag = tag.getCompound("hiddenBodies");
        int count = hiddenBodiesTag.getInt("count");
        for (int index = 0; index < count; index++) {
            String key = "body_" + index;
            if (hiddenBodiesTag.contains(key)) {
                this.hiddenBodies.add(hiddenBodiesTag.getUuid(key));
            }
        }
    }
}
