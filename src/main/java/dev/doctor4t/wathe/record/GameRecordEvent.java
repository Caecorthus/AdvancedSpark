package dev.doctor4t.wathe.record;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

/**
 * English: One ordered Spark-wathe match record event.
 * Chinese: 一条有序的 Spark-wathe 对局记录事件。
 */
public record GameRecordEvent(
        UUID matchId,
        int seq,
        String type,
        long worldTick,
        long realTimeMs,
        NbtCompound data
) {
}
