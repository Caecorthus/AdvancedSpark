package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.util.math.Box;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdvancedSparkMapVariablesBridgeTest {
    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void exposesSnowflakeColliderBridgeMethods() {
        MapVariablesWorldComponent component = new MapVariablesWorldComponent(null);
        Box collider = new Box(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);

        assertEquals(new Box(-41.5, 126.0, -538.5, 169.5, 120.0, -532.5), AdvancedSparkMapVariablesBridge.DEFAULT_SNOWFLAKE_COLLIDER);
        assertThrows(IllegalStateException.class, () -> AdvancedSparkMapVariablesBridge.getSnowflakeCollider(component));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkMapVariablesBridge.setSnowflakeCollider(component, collider));
    }
}
