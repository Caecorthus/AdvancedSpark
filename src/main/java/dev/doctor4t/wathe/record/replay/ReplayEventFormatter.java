package dev.doctor4t.wathe.record.replay;

import dev.doctor4t.wathe.record.GameRecordEvent;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * English: Formats one recorded event for replay output.
 * Chinese: 将一条记录事件格式化为回放输出。
 */
@FunctionalInterface
public interface ReplayEventFormatter {
    @Nullable
    Text format(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerWorld world);
}
