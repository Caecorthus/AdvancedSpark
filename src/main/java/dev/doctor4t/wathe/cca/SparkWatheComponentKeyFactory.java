package dev.doctor4t.wathe.cca;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

/**
 * English: Creates wathe-namespaced CCA keys while keeping plain JVM tests loadable.
 * Chinese: 创建 wathe 命名空间 CCA key，同时让普通 JVM 测试可以加载组件类。
 */
final class SparkWatheComponentKeyFactory {
    private SparkWatheComponentKeyFactory() {
    }

    static <T extends Component> @Nullable ComponentKey<T> create(Identifier id, Class<T> componentType) {
        try {
            return ComponentRegistry.getOrCreate(id, componentType);
        } catch (ExceptionInInitializerError | NoClassDefFoundError | NullPointerException exception) {
            return null;
        }
    }
}
