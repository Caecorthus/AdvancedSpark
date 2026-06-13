package io.github.caecorthus.advancedspark.content;

import net.minecraft.sound.SoundEvent;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Registry shell for migrated Spark and NoellesRoles sounds.
 * Chinese: 已迁移 Spark 与 NoellesRoles 声音的注册壳。
 */
public final class AdvancedSparkSoundEvents {
    private static final String INITIALIZER_NAME = "sounds";
    private static final AdvancedSparkContentDeclarations<SoundEvent, SoundEvent> DECLARATIONS = new AdvancedSparkContentDeclarations<>();

    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> BOMB_BEEP =
            declare("item.bomb.beep");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> BOMB_EXPLODE =
            declare("item.bomb.explode");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> JESTER_LAUGH =
            declare("ambient.jester_laugh");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> CORRUPT_COP_MOMENT_1 =
            declare("music.corrupt_cop_moment_1");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> CORRUPT_COP_MOMENT_2 =
            declare("music.corrupt_cop_moment_2");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> CORRUPT_COP_EXECUTION =
            declare("ambient.corrupt_cop_execution");
    public static final AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> JESTER_MOMENT =
            declare("music.jester_moment");

    private AdvancedSparkSoundEvents() {
    }

    public static AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> declare(String path) {
        return declare(path, () -> SoundEvent.of(AdvancedSparkContentRegistry.contentId(path)));
    }

    public static <T extends SoundEvent> AdvancedSparkContentDeclaration<T, SoundEvent> declare(String path, Supplier<T> supplier) {
        return DECLARATIONS.declare(path, supplier);
    }

    public static void registerAll(BiFunction<String, SoundEvent, SoundEvent> registrar) {
        DECLARATIONS.registerAll(registrar);
    }

    public static void register() {
        register(() -> AdvancedSparkSoundEvents.registerAll(AdvancedSparkContentRegistry::registerSoundEvent));
    }

    static void register(Runnable initializer) {
        AdvancedSparkContentBootstrap.registerInitializer(INITIALIZER_NAME, Objects.requireNonNull(initializer, "initializer"));
    }

    static void resetForTests() {
        DECLARATIONS.reset();
    }
}
