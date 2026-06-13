package io.github.caecorthus.advancedspark.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * English: Ordered bootstrap point for migrated shared content registries.
 * Chinese: 已迁移共享内容注册表的有序启动入口。
 */
public final class AdvancedSparkContentBootstrap {
    private static final Map<String, Runnable> INITIALIZERS = new LinkedHashMap<>();
    private static boolean initialized;

    private AdvancedSparkContentBootstrap() {
    }

    public static synchronized void registerInitializer(String name, Runnable initializer) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(initializer, "initializer");
        if (initialized) {
            initializer.run();
            return;
        }
        INITIALIZERS.put(name, initializer);
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        INITIALIZERS.values().forEach(Runnable::run);
    }

    static synchronized void resetForTests() {
        initialized = false;
        INITIALIZERS.clear();
    }
}
