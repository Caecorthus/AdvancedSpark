package io.github.caecorthus.advancedspark;

import net.minecraft.util.Identifier;

/**
 * English: Shared constants and identifier helpers for AdvancedSpark.
 * Chinese: AdvancedSpark 的共享常量和标识符辅助方法。
 */
public final class AdvancedSparkConstants {
    public static final String MOD_ID = "advancedspark";
    public static final String MOD_NAME = "AdvancedSpark";

    private AdvancedSparkConstants() {
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
