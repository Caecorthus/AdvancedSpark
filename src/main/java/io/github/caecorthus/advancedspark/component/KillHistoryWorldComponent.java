package io.github.caecorthus.advancedspark.component;

import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * English: World-level recent kill ledger used by migrated role checks.
 * Chinese: 已迁移职业判定使用的世界级近期击杀记录。
 */
public class KillHistoryWorldComponent implements Component, ServerTickingComponent, AdvancedSparkResettableComponent {
    public static final int DEFAULT_LOOKBACK_TICKS = 20 * 60 * 2;
    public static final ComponentKey<KillHistoryWorldComponent> KEY =
            AdvancedSparkComponentKeyFactory.create(AdvancedSparkConstants.id("kill_history"), KillHistoryWorldComponent.class);

    private final @Nullable World world;
    private final List<KillRecord> killRecords = new ArrayList<>();
    private final Set<Identifier> immuneDeathReasons = new LinkedHashSet<>();

    public KillHistoryWorldComponent(@Nullable World world) {
        this.world = world;
    }

    public void addImmuneDeathReason(Identifier deathReason) {
        this.immuneDeathReasons.add(Objects.requireNonNull(deathReason, "deathReason"));
    }

    public void removeImmuneDeathReason(Identifier deathReason) {
        this.immuneDeathReasons.remove(deathReason);
    }

    public Set<Identifier> immuneDeathReasons() {
        return Collections.unmodifiableSet(this.immuneDeathReasons);
    }

    public void recordKill(UUID killerUuid, UUID victimUuid, Identifier deathReason) {
        long currentTick = this.world == null ? 0L : this.world.getTime();
        this.recordKill(killerUuid, victimUuid, deathReason, currentTick);
    }

    public void recordKill(UUID killerUuid, UUID victimUuid, Identifier deathReason, long timestampTicks) {
        this.killRecords.add(new KillRecord(
                Objects.requireNonNull(killerUuid, "killerUuid"),
                Objects.requireNonNull(victimUuid, "victimUuid"),
                Objects.requireNonNull(deathReason, "deathReason"),
                Math.max(0L, timestampTicks)
        ));
    }

    public boolean hasRecentNonImmuneKill(UUID killerUuid, int lookbackTicks, long currentTick) {
        int safeLookbackTicks = Math.max(0, lookbackTicks);
        for (KillRecord record : this.killRecords) {
            if (record.killerUuid().equals(killerUuid)
                    && !record.isImmune(this.immuneDeathReasons)
                    && record.isWithinLookback(safeLookbackTicks, currentTick)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRecentNonImmuneKill(UUID killerUuid, long currentTick) {
        return this.hasRecentNonImmuneKill(killerUuid, DEFAULT_LOOKBACK_TICKS, currentTick);
    }

    public void pruneExpired(long currentTick) {
        this.killRecords.removeIf(record -> !record.isWithinLookback(DEFAULT_LOOKBACK_TICKS, currentTick));
    }

    public List<KillRecord> records() {
        return Collections.unmodifiableList(this.killRecords);
    }

    @Override
    public void serverTick() {
        if (this.world != null) {
            this.pruneExpired(this.world.getTime());
        }
    }

    @Override
    public void reset() {
        if (!this.killRecords.isEmpty()) {
            this.killRecords.clear();
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList recordsTag = new NbtList();
        for (KillRecord record : this.killRecords) {
            NbtCompound recordTag = new NbtCompound();
            recordTag.putUuid("killer", record.killerUuid());
            recordTag.putUuid("victim", record.victimUuid());
            recordTag.putString("deathReason", record.deathReason().toString());
            recordTag.putLong("tick", record.timestampTicks());
            recordsTag.add(recordTag);
        }
        tag.put("killRecords", recordsTag);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.killRecords.clear();
        if (!tag.contains("killRecords")) {
            return;
        }

        NbtList recordsTag = tag.getList("killRecords", NbtElement.COMPOUND_TYPE);
        for (int index = 0; index < recordsTag.size(); index++) {
            NbtCompound recordTag = recordsTag.getCompound(index);
            Identifier deathReason = Identifier.tryParse(recordTag.getString("deathReason"));
            if (deathReason != null && recordTag.contains("killer") && recordTag.contains("victim")) {
                this.killRecords.add(new KillRecord(
                        recordTag.getUuid("killer"),
                        recordTag.getUuid("victim"),
                        deathReason,
                        recordTag.contains("tick") ? recordTag.getLong("tick") : 0L
                ));
            }
        }
    }

    public record KillRecord(UUID killerUuid, UUID victimUuid, Identifier deathReason, long timestampTicks) {
        private boolean isImmune(Set<Identifier> immuneDeathReasons) {
            return immuneDeathReasons.contains(this.deathReason);
        }

        private boolean isWithinLookback(int lookbackTicks, long currentTick) {
            return currentTick >= this.timestampTicks && currentTick - this.timestampTicks <= lookbackTicks;
        }
    }
}
