package dev.doctor4t.wathe.index;

import dev.doctor4t.wathe.Wathe;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * English: Spark-wathe attribute shim exposed for Spark-NoellesRoles compatibility.
 * Chinese: 为兼容 Spark-NoellesRoles 暴露的 Spark-wathe 属性垫片。
 */
public final class WatheAttributes {
    public static final RegistryEntry<EntityAttribute> MAX_SPRINT_TIME =
            Registry.registerReference(
                    Registries.ATTRIBUTE,
                    Wathe.id("max_sprint_time"),
                    new ClampedEntityAttribute("attribute.name.wathe.max_sprint_time", 200.0, 0.0, 100000.0)
                            .setTracked(true)
            );

    private WatheAttributes() {
    }

    public static void initialize() {
    }
}
