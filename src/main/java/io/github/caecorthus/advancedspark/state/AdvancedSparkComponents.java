package io.github.caecorthus.advancedspark.state;

import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * English: Component-style access point that can later be replaced by a persisted backend.
 * Chinese: 组件风格的访问入口，之后可以替换为持久化后端。
 */
public final class AdvancedSparkComponents {
    private static final Map<MinecraftServer, AdvancedSparkGameState> SERVER_STATES =
            Collections.synchronizedMap(new WeakHashMap<>());

    private AdvancedSparkComponents() {
    }

    public static AdvancedSparkGameState get(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        return SERVER_STATES.computeIfAbsent(server, ignored -> new AdvancedSparkGameState());
    }

    public static void clear(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        SERVER_STATES.remove(server);
    }
}
