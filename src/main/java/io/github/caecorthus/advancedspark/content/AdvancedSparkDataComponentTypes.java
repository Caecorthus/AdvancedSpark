package io.github.caecorthus.advancedspark.content;

import com.mojang.serialization.Codec;
import dev.doctor4t.wathe.item.component.CosmeticComponent;
import dev.doctor4t.wathe.item.component.WalkieTalkieComponent;
import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * English: Registry shell for migrated Spark and NoellesRoles data component types.
 * Chinese: 已迁移 Spark 与 NoellesRoles 数据组件类型的注册壳。
 */
public final class AdvancedSparkDataComponentTypes {
    private static final String INITIALIZER_NAME = "data_component_types";
    private static final AdvancedSparkContentDeclarations<ComponentType<?>, ComponentType<?>> DECLARATIONS = new AdvancedSparkContentDeclarations<>();

    public static final AdvancedSparkContentDeclaration<ComponentType<Integer>, ComponentType<?>> BULLETS =
            declare("bullets", () -> ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.INTEGER)
                    .build());
    public static final AdvancedSparkContentDeclaration<ComponentType<WalkieTalkieComponent>, ComponentType<?>> WALKIE_TALKIE =
            declare("walkie_talkie", AdvancedSparkSourceIds.sparkWathe("walkie_talkie"), () -> ComponentType.<WalkieTalkieComponent>builder()
                    .codec(WalkieTalkieComponent.CODEC)
                    .packetCodec(WalkieTalkieComponent.PACKET_CODEC)
                    .build());
    public static final AdvancedSparkContentDeclaration<ComponentType<CosmeticComponent>, ComponentType<?>> SKIN =
            declare("skin", AdvancedSparkSourceIds.sparkWathe("skin"), () -> ComponentType.<CosmeticComponent>builder()
                    .codec(CosmeticComponent.CODEC)
                    .packetCodec(CosmeticComponent.PACKET_CODEC)
                    .build());

    private AdvancedSparkDataComponentTypes() {
    }

    public static <T extends ComponentType<?>> AdvancedSparkContentDeclaration<T, ComponentType<?>> declare(String path, Supplier<T> supplier) {
        return DECLARATIONS.declare(path, supplier);
    }

    public static <T extends ComponentType<?>> AdvancedSparkContentDeclaration<T, ComponentType<?>> declare(
            String path,
            Identifier id,
            Supplier<T> supplier
    ) {
        return DECLARATIONS.declare(path, id, supplier);
    }

    public static void registerAll(BiFunction<String, ComponentType<?>, ComponentType<?>> registrar) {
        DECLARATIONS.registerAll(registrar);
    }

    public static void register() {
        register(() -> DECLARATIONS.registerAllById(AdvancedSparkContentRegistry::registerDataComponentType));
    }

    static void register(Runnable initializer) {
        AdvancedSparkContentBootstrap.registerInitializer(INITIALIZER_NAME, Objects.requireNonNull(initializer, "initializer"));
    }

    static void resetForTests() {
        DECLARATIONS.reset();
    }
}
