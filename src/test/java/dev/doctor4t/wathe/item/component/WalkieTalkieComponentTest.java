package dev.doctor4t.wathe.item.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalkieTalkieComponentTest {
    @Test
    public void exposesSparkWatheDefaultChannel() {
        assertEquals(0, WalkieTalkieComponent.DEFAULT.channel());
        assertEquals(7, new WalkieTalkieComponent(7).channel());
    }
}
