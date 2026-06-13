package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoundEndAccess;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoundEndBridge;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * English: Adds Spark-wathe rich round-end data storage to original Wathe's component.
 * Chinese: 给原版 Wathe 回合结算组件补上 Spark-wathe 富结算数据存储。
 */
@Mixin(GameRoundEndComponent.class)
public abstract class GameRoundEndComponentRichDataBridgeMixin implements AdvancedSparkRoundEndAccess {
    @Unique
    private final List<AdvancedSparkRoundEndBridge.RoundEndData> advancedspark$players = new ArrayList<>();
    @Unique
    private GameFunctions.WinStatus advancedspark$winStatus = GameFunctions.WinStatus.NONE;
    @Unique
    private @Nullable Identifier advancedspark$gameModeId;

    @Shadow
    public abstract void sync();

    @Override
    public List<AdvancedSparkRoundEndBridge.RoundEndData> advancedspark$getPlayers() {
        return this.advancedspark$players;
    }

    @Override
    public GameFunctions.WinStatus advancedspark$getWinStatus() {
        return this.advancedspark$winStatus;
    }

    @Override
    public @Nullable Identifier advancedspark$getGameModeId() {
        return this.advancedspark$gameModeId;
    }

    @Override
    public void advancedspark$setRoundEndData(
            List<AdvancedSparkRoundEndBridge.RoundEndData> players,
            GameFunctions.WinStatus winStatus,
            @Nullable Identifier gameModeId
    ) {
        this.advancedspark$players.clear();
        this.advancedspark$players.addAll(players);
        this.advancedspark$winStatus = winStatus;
        this.advancedspark$gameModeId = gameModeId;
        this.sync();
    }

    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void advancedspark$writeRichRoundEndData(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        NbtList players = new NbtList();
        for (AdvancedSparkRoundEndBridge.RoundEndData player : this.advancedspark$players) {
            players.add(player.writeToNbt());
        }
        tag.put("AdvancedSparkPlayers", players);
        tag.putInt("AdvancedSparkWinStatus", this.advancedspark$winStatus.ordinal());
        if (this.advancedspark$gameModeId != null) {
            tag.putString("AdvancedSparkGameMode", this.advancedspark$gameModeId.toString());
        }
    }

    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void advancedspark$readRichRoundEndData(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        this.advancedspark$players.clear();
        for (NbtElement element : tag.getList("AdvancedSparkPlayers", NbtElement.COMPOUND_TYPE)) {
            this.advancedspark$players.add(new AdvancedSparkRoundEndBridge.RoundEndData((NbtCompound) element));
        }
        if (tag.contains("AdvancedSparkWinStatus")) {
            this.advancedspark$winStatus = GameFunctions.WinStatus.values()[tag.getInt("AdvancedSparkWinStatus")];
        }
        this.advancedspark$gameModeId = tag.contains("AdvancedSparkGameMode")
                ? Identifier.of(tag.getString("AdvancedSparkGameMode"))
                : null;
    }
}
