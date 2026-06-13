package io.github.caecorthus.advancedspark.content;

import net.minecraft.entity.EntityType;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Registry shell for migrated Spark and NoellesRoles entity types.
 * Chinese: 已迁移 Spark 与 NoellesRoles 实体类型的注册壳。
 */
public final class AdvancedSparkEntityTypes {
    private static final String INITIALIZER_NAME = "entity_types";
    private static final AdvancedSparkContentDeclarations<EntityType<?>, EntityType<?>> DECLARATIONS = new AdvancedSparkContentDeclarations<>();

    private AdvancedSparkEntityTypes() {
    }

    public static <T extends EntityType<?>> AdvancedSparkContentDeclaration<T, EntityType<?>> declare(String path, Supplier<T> supplier) {
        return DECLARATIONS.declare(path, supplier);
    }

    public static void registerAll(BiFunction<String, EntityType<?>, EntityType<?>> registrar) {
        DECLARATIONS.registerAll(registrar);
    }

    public static void register() {
        register(() -> AdvancedSparkEntityTypes.registerAll(AdvancedSparkContentRegistry::registerEntityType));
    }

    static void register(Runnable initializer) {
        AdvancedSparkContentBootstrap.registerInitializer(INITIALIZER_NAME, Objects.requireNonNull(initializer, "initializer"));
    }

    static void resetForTests() {
        DECLARATIONS.reset();
    }
}
