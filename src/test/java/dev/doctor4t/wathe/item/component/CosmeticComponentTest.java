package dev.doctor4t.wathe.item.component;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CosmeticComponentTest {
    @Test
    public void readsTextureResourcesAndExtraData() {
        CosmeticComponent component = new CosmeticComponent(
                "cosmetic",
                "{\"text\":\"Cosmetic\"}",
                "rare",
                "primary.png",
                "{\"textures\":{\"thrown\":\"thrown.png\"},\"data\":{\"power\":3}}"
        );

        assertEquals("thrown.png", component.getTexture("thrown"));
        assertEquals("primary.png", component.getTexture("missing"));
        assertEquals(List.of("thrown.png"), component.getResourceTextureUrls());
        assertNotNull(component.getExtraData());
        assertEquals("other.png", component.withTextureUrl("other.png").textureUrl());
    }

    @Test
    public void toleratesEmptyResources() {
        CosmeticComponent component = new CosmeticComponent("cosmetic", "name", "common", "primary.png", "");

        assertEquals("primary.png", component.getTexture("any"));
        assertEquals(List.of(), component.getResourceTextureUrls());
        assertNull(component.getExtraData());
    }
}
