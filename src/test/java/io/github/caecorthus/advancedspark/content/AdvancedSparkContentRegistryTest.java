package io.github.caecorthus.advancedspark.content;

import io.github.caecorthus.advancedspark.AdvancedSparkConstants;
import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSparkContentRegistryTest {
    @AfterEach
    public void resetContentBootstrap() {
        AdvancedSparkContentBootstrap.resetForTests();
        AdvancedSparkItems.resetForTests();
        AdvancedSparkStatusEffects.resetForTests();
        AdvancedSparkSoundEvents.resetForTests();
        AdvancedSparkDataComponentTypes.resetForTests();
        AdvancedSparkEntityTypes.resetForTests();
    }

    @Test
    public void contentIdsPreserveNoellesRolesNamespace() {
        assertEquals(
                AdvancedSparkSourceIds.noellesRoles("poison_needle"),
                AdvancedSparkContentRegistry.contentId("poison_needle")
        );
    }

    @Test
    public void contentBootstrapRunsInitializersOnceInRegistrationOrder() {
        List<String> calls = new ArrayList<>();

        AdvancedSparkContentBootstrap.registerInitializer("items", () -> calls.add("items"));
        AdvancedSparkContentBootstrap.registerInitializer("sounds", () -> calls.add("sounds"));

        AdvancedSparkContentBootstrap.initialize();
        AdvancedSparkContentBootstrap.initialize();

        assertEquals(List.of("items", "sounds"), calls);
    }

    @Test
    public void contentShellsRegisterBootstrapInitializersInContentOrder() {
        List<String> calls = new ArrayList<>();

        AdvancedSparkItems.register(() -> calls.add("items"));
        AdvancedSparkStatusEffects.register(() -> calls.add("effects"));
        AdvancedSparkSoundEvents.register(() -> calls.add("sounds"));

        AdvancedSparkContentBootstrap.initialize();

        assertEquals(List.of("items", "effects", "sounds"), calls);
    }

    @Test
    public void contentDeclarationsKeepTypedEntriesAndRegisterThemInOrder() {
        AdvancedSparkContentDeclarations<String, Integer> declarations = new AdvancedSparkContentDeclarations<>();
        List<String> calls = new ArrayList<>();

        AdvancedSparkContentDeclaration<String, Integer> poisonNeedle = declarations.declare("poison_needle", () -> "item");
        AdvancedSparkContentDeclaration<String, Integer> drowsy = declarations.declare("drowsy", () -> "effect");

        assertEquals(AdvancedSparkSourceIds.noellesRoles("poison_needle"), poisonNeedle.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("drowsy"), drowsy.id());

        declarations.registerAll((path, value) -> {
            calls.add(path + ":" + value);
            return value.length();
        });

        assertEquals(List.of("poison_needle:item", "drowsy:effect"), calls);
        assertEquals("item", poisonNeedle.content());
        assertEquals(4, poisonNeedle.registered());
        assertEquals("effect", drowsy.content());
        assertEquals(6, drowsy.registered());

        declarations.reset();
        declarations.registerAll((path, value) -> {
            calls.add(path + ":" + value);
            return value.length();
        });

        assertEquals(List.of("poison_needle:item", "drowsy:effect"), calls);
    }

    @Test
    public void contentShellsExposeTypedDeclarationsWithContentIds() {
        AdvancedSparkContentDeclaration<Item, Item> item = AdvancedSparkItems.declare(
                "poison_needle",
                () -> {
                    throw new AssertionError("item supplier should not run in this declaration test");
                }
        );
        AdvancedSparkContentDeclaration<StatusEffect, RegistryEntry<StatusEffect>> effect = AdvancedSparkStatusEffects.declare(
                "drowsy",
                () -> {
                    throw new AssertionError("effect supplier should not run in this declaration test");
                }
        );
        AdvancedSparkContentDeclaration<SoundEvent, SoundEvent> sound = AdvancedSparkSoundEvents.declare("role.veteran.alert");

        assertEquals(AdvancedSparkSourceIds.noellesRoles("poison_needle"), item.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("drowsy"), effect.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("role.veteran.alert"), sound.id());
    }

    @Test
    public void noellesStatusEffectsAreDeclaredWithSourceIds() {
        assertEquals(AdvancedSparkSourceIds.noellesRoles("stimulation"), AdvancedSparkStatusEffects.STIMULATION.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("no_collision"), AdvancedSparkStatusEffects.NO_COLLISION.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("gin_immunity"), AdvancedSparkStatusEffects.GIN_IMMUNITY.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("whiskey_shield"), AdvancedSparkStatusEffects.WHISKEY_SHIELD.id());
    }

    @Test
    public void noellesSoundsAreDeclaredWithSourceIds() {
        assertEquals(AdvancedSparkSourceIds.noellesRoles("item.bomb.beep"), AdvancedSparkSoundEvents.BOMB_BEEP.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("item.bomb.explode"), AdvancedSparkSoundEvents.BOMB_EXPLODE.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("ambient.jester_laugh"), AdvancedSparkSoundEvents.JESTER_LAUGH.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("music.corrupt_cop_moment_1"), AdvancedSparkSoundEvents.CORRUPT_COP_MOMENT_1.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("music.corrupt_cop_moment_2"), AdvancedSparkSoundEvents.CORRUPT_COP_MOMENT_2.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("ambient.corrupt_cop_execution"), AdvancedSparkSoundEvents.CORRUPT_COP_EXECUTION.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("music.jester_moment"), AdvancedSparkSoundEvents.JESTER_MOMENT.id());
    }

    @Test
    public void noellesDataComponentsAreDeclaredWithSourceIds() {
        assertEquals(AdvancedSparkSourceIds.noellesRoles("bullets"), AdvancedSparkDataComponentTypes.BULLETS.id());
    }

    @Test
    public void sparkWatheDataComponentsAreDeclaredWithWatheSourceIds() {
        assertEquals(AdvancedSparkSourceIds.sparkWathe("walkie_talkie"), AdvancedSparkDataComponentTypes.WALKIE_TALKIE.id());
        assertEquals(AdvancedSparkSourceIds.sparkWathe("skin"), AdvancedSparkDataComponentTypes.SKIN.id());
    }

    @Test
    public void advancedContentShellsExposeTypedDeclarationsWithContentIds() {
        AdvancedSparkContentDeclaration<ComponentType<?>, ComponentType<?>> dataComponent =
                AdvancedSparkDataComponentTypes.declare(
                        "owner",
                        () -> {
                            throw new AssertionError("data component supplier should not run in this declaration test");
                        }
                );
        AdvancedSparkContentDeclaration<EntityType<?>, EntityType<?>> entityType =
                AdvancedSparkEntityTypes.declare(
                        "fake_body",
                        () -> {
                            throw new AssertionError("entity type supplier should not run in this declaration test");
                        }
                );

        assertEquals(AdvancedSparkSourceIds.noellesRoles("owner"), dataComponent.id());
        assertEquals(AdvancedSparkSourceIds.noellesRoles("fake_body"), entityType.id());
    }
}
