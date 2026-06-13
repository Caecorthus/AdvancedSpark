package io.github.caecorthus.advancedspark.content;

import net.minecraft.item.Item;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Registry shell for migrated Spark and NoellesRoles items.
 * Chinese: 已迁移 Spark 与 NoellesRoles 物品的注册壳。
 */
public final class AdvancedSparkItems {
    private static final String INITIALIZER_NAME = "items";
    private static final AdvancedSparkContentDeclarations<Item, Item> DECLARATIONS = new AdvancedSparkContentDeclarations<>();

    private AdvancedSparkItems() {
    }

    public static <T extends Item> AdvancedSparkContentDeclaration<T, Item> declare(String path, Supplier<T> supplier) {
        return DECLARATIONS.declare(path, supplier);
    }

    public static void registerAll(BiFunction<String, Item, Item> registrar) {
        DECLARATIONS.registerAll(registrar);
    }

    public static void register() {
        register(() -> AdvancedSparkItems.registerAll(AdvancedSparkContentRegistry::registerItem));
    }

    static void register(Runnable initializer) {
        AdvancedSparkContentBootstrap.registerInitializer(INITIALIZER_NAME, Objects.requireNonNull(initializer, "initializer"));
    }

    static void resetForTests() {
        DECLARATIONS.reset();
    }
}
