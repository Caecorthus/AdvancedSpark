package io.github.caecorthus.advancedspark.content;

import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * English: Immutable declaration for one migrated content entry before it is registered.
 * Chinese: 单个已迁移内容在正式注册前的不可变声明。
 */
public final class AdvancedSparkContentDeclaration<T, R> {
    private final String path;
    private final Identifier id;
    private final Supplier<T> supplier;
    private T content;
    private R registered;

    AdvancedSparkContentDeclaration(String path, Identifier id, Supplier<T> supplier) {
        this.path = Objects.requireNonNull(path, "path");
        this.id = Objects.requireNonNull(id, "id");
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    T create() {
        this.content = Objects.requireNonNull(supplier.get(), path);
        return content;
    }

    void markRegistered(R registered) {
        this.registered = Objects.requireNonNull(registered, path);
    }

    public String path() {
        return path;
    }

    public Identifier id() {
        return id;
    }

    public T content() {
        if (content == null) {
            throw new IllegalStateException("Content has not been created yet: " + id);
        }
        return content;
    }

    public R registered() {
        if (registered == null) {
            throw new IllegalStateException("Content has not been registered yet: " + id);
        }
        return registered;
    }

    public boolean isRegistered() {
        return registered != null;
    }
}
