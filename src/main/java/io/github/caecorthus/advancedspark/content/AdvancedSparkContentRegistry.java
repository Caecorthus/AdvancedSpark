package io.github.caecorthus.advancedspark.content;

import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * English: Registry facade for migrated NoellesRoles content using source-compatible ids.
 * Chinese: 使用源兼容 ID 的已迁移 NoellesRoles 内容注册门面。
 */
public final class AdvancedSparkContentRegistry {
    private AdvancedSparkContentRegistry() {
    }

    public static Identifier contentId(String path) {
        return AdvancedSparkSourceIds.noellesRoles(path);
    }

    public static <T extends Item> T registerItem(String path, T item) {
        return Registry.register(Registries.ITEM, contentId(path), item);
    }

    public static RegistryEntry<StatusEffect> registerStatusEffect(String path, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, contentId(path), statusEffect);
    }

    public static <T extends EntityType<?>> T registerEntityType(String path, T entityType) {
        return Registry.register(Registries.ENTITY_TYPE, contentId(path), entityType);
    }

    public static SoundEvent registerSoundEvent(String path) {
        Identifier id = contentId(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static SoundEvent registerSoundEvent(String path, SoundEvent soundEvent) {
        return Registry.register(Registries.SOUND_EVENT, contentId(path), soundEvent);
    }

    public static <T> ComponentType<T> registerDataComponentType(String path, ComponentType<T> componentType) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, contentId(path), componentType);
    }

    public static <T> ComponentType<T> registerDataComponentType(Identifier id, ComponentType<T> componentType) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, componentType);
    }
}
