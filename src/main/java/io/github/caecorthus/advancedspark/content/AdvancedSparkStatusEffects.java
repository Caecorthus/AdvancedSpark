package io.github.caecorthus.advancedspark.content;

import io.github.caecorthus.advancedspark.effect.GinImmunityEffect;
import io.github.caecorthus.advancedspark.effect.NoCollisionEffect;
import io.github.caecorthus.advancedspark.effect.StimulationEffect;
import io.github.caecorthus.advancedspark.effect.WhiskeyShieldEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Registry shell for migrated Spark and NoellesRoles status effects.
 * Chinese: 已迁移 Spark 与 NoellesRoles 状态效果的注册壳。
 */
public final class AdvancedSparkStatusEffects {
    private static final String INITIALIZER_NAME = "status_effects";
    private static final AdvancedSparkContentDeclarations<StatusEffect, RegistryEntry<StatusEffect>> DECLARATIONS = new AdvancedSparkContentDeclarations<>();

    public static final AdvancedSparkContentDeclaration<StimulationEffect, RegistryEntry<StatusEffect>> STIMULATION =
            declare("stimulation", StimulationEffect::new);
    public static final AdvancedSparkContentDeclaration<NoCollisionEffect, RegistryEntry<StatusEffect>> NO_COLLISION =
            declare("no_collision", NoCollisionEffect::new);
    public static final AdvancedSparkContentDeclaration<GinImmunityEffect, RegistryEntry<StatusEffect>> GIN_IMMUNITY =
            declare("gin_immunity", GinImmunityEffect::new);
    public static final AdvancedSparkContentDeclaration<WhiskeyShieldEffect, RegistryEntry<StatusEffect>> WHISKEY_SHIELD =
            declare("whiskey_shield", WhiskeyShieldEffect::new);

    private AdvancedSparkStatusEffects() {
    }

    public static <T extends StatusEffect> AdvancedSparkContentDeclaration<T, RegistryEntry<StatusEffect>> declare(String path, Supplier<T> supplier) {
        return DECLARATIONS.declare(path, supplier);
    }

    public static void registerAll(BiFunction<String, StatusEffect, RegistryEntry<StatusEffect>> registrar) {
        DECLARATIONS.registerAll(registrar);
    }

    public static void register() {
        register(() -> AdvancedSparkStatusEffects.registerAll(AdvancedSparkContentRegistry::registerStatusEffect));
    }

    static void register(Runnable initializer) {
        AdvancedSparkContentBootstrap.registerInitializer(INITIALIZER_NAME, Objects.requireNonNull(initializer, "initializer"));
    }

    static void resetForTests() {
        DECLARATIONS.reset();
    }
}
