package dev.doctor4t.wathe.record.replay;

import dev.doctor4t.wathe.record.GameRecordEvent;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * English: Lightweight replay text helpers compatible with Spark-wathe APIs.
 * Chinese: 兼容 Spark-wathe API 的轻量回放文本辅助工具。
 */
public final class ReplayGenerator {
    private ReplayGenerator() {
    }

    public record PlayerInfo(String name, String roleTranslationKey, int roleColor) {
    }

    public static void generateAndSend(ServerWorld world, GameRecordManager.MatchRecord match) {
        Map<UUID, PlayerInfo> playerInfoCache = getPlayerInfoCache(match);
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.translatable("replay.title").formatted(Formatting.GOLD), false);
            for (GameRecordEvent event : match.getEvents()) {
                ReplayEventFormatter formatter = ReplayRegistry.getFormatter(event.type());
                if (formatter != null) {
                    Text text = formatter.format(event, match, world);
                    if (text != null) {
                        player.sendMessage(Text.literal("[" + formatTime(event.worldTick(), match.getStartTick()) + "] ")
                                .formatted(Formatting.GRAY)
                                .append(text), false);
                    }
                }
            }
        }
        playerInfoCache.clear();
    }

    public static String formatTime(long tick, long startTick) {
        long totalSeconds = Math.max(0L, tick - startTick) / 20L;
        return String.format("%02d:%02d", totalSeconds / 60L, totalSeconds % 60L);
    }

    public static Text formatPlayerName(UUID uuid, Map<UUID, PlayerInfo> playerInfoCache) {
        PlayerInfo info = playerInfoCache.get(uuid);
        if (info == null) {
            return Text.literal(uuid.toString().substring(0, 8));
        }
        MutableText text = Text.literal(info.name() + "(")
                .append(Text.translatable(info.roleTranslationKey()))
                .append(Text.literal(")"));
        return text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(info.roleColor())));
    }

    public static Text formatItemName(NbtCompound data, ServerWorld world) {
        String itemId = data.getString("item");
        Identifier id = Identifier.tryParse(itemId);
        if (id == null) {
            return Text.literal("[unknown]").formatted(Formatting.WHITE);
        }
        Item item = Registries.ITEM.get(id);
        return Text.literal("[")
                .append(Text.translatable(item.getTranslationKey()))
                .append(Text.literal("]"))
                .formatted(Formatting.WHITE);
    }

    public static Map<UUID, PlayerInfo> getPlayerInfoCache(GameRecordManager.MatchRecord match) {
        Map<UUID, PlayerInfo> playerInfo = new HashMap<>();
        List<GameRecordEvent> events = match.getEvents();
        for (GameRecordEvent event : events) {
            if (!"role_assigned".equals(event.type())) {
                continue;
            }
            NbtCompound player = event.data().getCompound("player");
            if (player.containsUuid("uuid")) {
                UUID uuid = player.getUuid("uuid");
                String name = player.getString("name");
                Identifier roleId = Identifier.tryParse(player.getString("role"));
                String translationKey = roleId == null ? "unknown" : "announcement.role." + roleId.getPath();
                playerInfo.put(uuid, new PlayerInfo(name, translationKey, player.getInt("role_color")));
            }
        }
        return playerInfo;
    }
}
