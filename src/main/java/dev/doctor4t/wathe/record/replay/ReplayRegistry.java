package dev.doctor4t.wathe.record.replay;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * English: Registry for Spark-wathe replay formatters.
 * Chinese: Spark-wathe 回放格式化器注册表。
 */
public final class ReplayRegistry {
    private static final Map<String, ReplayEventFormatter> FORMATTERS = new ConcurrentHashMap<>();
    private static final Map<Identifier, ReplayEventFormatter> SKILL_FORMATTERS = new ConcurrentHashMap<>();
    private static final Map<Identifier, ReplayEventFormatter> ITEM_USE_FORMATTERS = new ConcurrentHashMap<>();
    private static final Map<Identifier, ReplayEventFormatter> PLATTER_TAKE_FORMATTERS = new ConcurrentHashMap<>();
    private static final Map<Identifier, ReplayEventFormatter> GLOBAL_EVENT_FORMATTERS = new ConcurrentHashMap<>();

    private ReplayRegistry() {
    }

    public static void registerFormatter(String eventType, ReplayEventFormatter formatter) {
        FORMATTERS.put(eventType, formatter);
    }

    public static void registerSkillFormatter(Identifier skillId, ReplayEventFormatter formatter) {
        SKILL_FORMATTERS.put(skillId, formatter);
    }

    public static void registerItemUseFormatter(Identifier itemId, ReplayEventFormatter formatter) {
        ITEM_USE_FORMATTERS.put(itemId, formatter);
    }

    public static void registerPlatterTakeFormatter(Identifier itemId, ReplayEventFormatter formatter) {
        PLATTER_TAKE_FORMATTERS.put(itemId, formatter);
    }

    public static void registerGlobalEventFormatter(Identifier eventId, ReplayEventFormatter formatter) {
        GLOBAL_EVENT_FORMATTERS.put(eventId, formatter);
    }

    public static boolean isIncluded(String eventType) {
        return FORMATTERS.containsKey(eventType);
    }

    public static @Nullable ReplayEventFormatter getFormatter(String eventType) {
        return FORMATTERS.get(eventType);
    }

    public static @Nullable ReplayEventFormatter getSkillFormatter(Identifier skillId) {
        return SKILL_FORMATTERS.get(skillId);
    }

    public static @Nullable ReplayEventFormatter getItemUseFormatter(Identifier itemId) {
        return ITEM_USE_FORMATTERS.get(itemId);
    }

    public static @Nullable ReplayEventFormatter getPlatterTakeFormatter(Identifier itemId) {
        return PLATTER_TAKE_FORMATTERS.get(itemId);
    }

    public static @Nullable ReplayEventFormatter getGlobalEventFormatter(Identifier eventId) {
        return GLOBAL_EVENT_FORMATTERS.get(eventId);
    }
}
