package io.github.caecorthus.advancedspark.content;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Typed declaration queue shared by migrated content registry shells.
 * Chinese: 已迁移内容注册壳共用的类型化声明队列。
 */
final class AdvancedSparkContentDeclarations<T, R> {
    private final List<AdvancedSparkContentDeclaration<? extends T, R>> declarations = new ArrayList<>();

    <V extends T> AdvancedSparkContentDeclaration<V, R> declare(String path, Supplier<V> supplier) {
        return declare(path, AdvancedSparkContentRegistry.contentId(path), supplier);
    }

    <V extends T> AdvancedSparkContentDeclaration<V, R> declare(String path, Identifier id, Supplier<V> supplier) {
        AdvancedSparkContentDeclaration<V, R> declaration = new AdvancedSparkContentDeclaration<>(
                path,
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(supplier, "supplier")
        );
        declarations.add(declaration);
        return declaration;
    }

    void registerAll(BiFunction<String, T, R> registrar) {
        Objects.requireNonNull(registrar, "registrar");
        for (AdvancedSparkContentDeclaration<? extends T, R> declaration : declarations) {
            T content = declaration.create();
            declaration.markRegistered(registrar.apply(declaration.path(), content));
        }
    }

    void registerAllById(BiFunction<Identifier, T, R> registrar) {
        Objects.requireNonNull(registrar, "registrar");
        for (AdvancedSparkContentDeclaration<? extends T, R> declaration : declarations) {
            T content = declaration.create();
            declaration.markRegistered(registrar.apply(declaration.id(), content));
        }
    }

    void reset() {
        declarations.clear();
    }
}
