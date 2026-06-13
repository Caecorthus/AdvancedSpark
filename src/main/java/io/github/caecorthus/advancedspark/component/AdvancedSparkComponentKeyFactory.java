package io.github.caecorthus.advancedspark.component;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

/**
 * English: Creates CCA keys while keeping plain JVM component tests loadable.
 * Chinese: 创建 CCA key，同时让普通 JVM 组件测试可以加载类。
 */
final class AdvancedSparkComponentKeyFactory {
    private AdvancedSparkComponentKeyFactory() {
    }

    static <T extends Component> @Nullable ComponentKey<T> create(Identifier id, Class<T> componentType) {
        try {
            return ComponentRegistry.getOrCreate(id, componentType);
        } catch (ExceptionInInitializerError | NoClassDefFoundError | NullPointerException exception) {
            return null;
        }
    }
}
