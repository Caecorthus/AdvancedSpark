package dev.doctor4t.wathe.record.replay;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReplayRegistryTest {
    @Test
    public void storesGeneralAndSpecializedFormatters() {
        ReplayEventFormatter formatter = (event, match, world) -> Text.literal("ok");
        Identifier id = Identifier.of("advancedspark_test", "event");

        ReplayRegistry.registerFormatter("custom", formatter);
        ReplayRegistry.registerGlobalEventFormatter(id, formatter);

        assertTrue(ReplayRegistry.isIncluded("custom"));
        assertSame(formatter, ReplayRegistry.getFormatter("custom"));
        assertSame(formatter, ReplayRegistry.getGlobalEventFormatter(id));
    }
}
