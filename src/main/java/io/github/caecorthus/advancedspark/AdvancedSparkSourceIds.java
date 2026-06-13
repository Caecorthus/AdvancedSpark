package io.github.caecorthus.advancedspark;

import net.minecraft.util.Identifier;

/**
 * English: Identifier helpers for source-compatible migrated content.
 * Chinese: 面向源兼容迁移内容的标识符辅助方法。
 */
public final class AdvancedSparkSourceIds {
    public static final String SPARK_WATHE_MOD_ID = "wathe";
    public static final String NOELLES_ROLES_MOD_ID = "noellesroles";

    private AdvancedSparkSourceIds() {
    }

    public static Identifier advancedSpark(String path) {
        return AdvancedSparkConstants.id(path);
    }

    public static Identifier sparkWathe(String path) {
        return Identifier.of(SPARK_WATHE_MOD_ID, path);
    }

    public static Identifier noellesRoles(String path) {
        return Identifier.of(NOELLES_ROLES_MOD_ID, path);
    }
}
