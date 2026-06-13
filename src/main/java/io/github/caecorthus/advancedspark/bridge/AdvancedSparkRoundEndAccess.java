package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * English: Mixin-backed access to AdvancedSpark's rich Spark-wathe round-end data.
 * Chinese: 由 mixin 支撑的 AdvancedSpark 富 Spark-wathe 回合结算数据访问口。
 */
public interface AdvancedSparkRoundEndAccess {
    List<AdvancedSparkRoundEndBridge.RoundEndData> advancedspark$getPlayers();

    GameFunctions.WinStatus advancedspark$getWinStatus();

    @Nullable Identifier advancedspark$getGameModeId();

    void advancedspark$setRoundEndData(
            List<AdvancedSparkRoundEndBridge.RoundEndData> players,
            GameFunctions.WinStatus winStatus,
            @Nullable Identifier gameModeId
    );
}
