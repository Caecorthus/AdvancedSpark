package io.github.caecorthus.advancedspark.bridge;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedSparkRoundEndBridgeTest {
    @Test
    public void roundEndDataPreservesRoleAndPlayerEndStatus() {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000401");
        AdvancedSparkRoundEndBridge.RoundEndData data = new AdvancedSparkRoundEndBridge.RoundEndData(
                new GameProfile(playerId, "Player"),
                Identifier.of("advancedspark_test", "winner"),
                AdvancedSparkRoundEndBridge.PlayerEndStatus.LEFT_DEAD,
                true
        );

        NbtCompound tag = data.writeToNbt();
        AdvancedSparkRoundEndBridge.RoundEndData decoded = new AdvancedSparkRoundEndBridge.RoundEndData(tag);

        assertEquals(data, decoded);
        assertTrue(decoded.wasDead());
        assertTrue(decoded.hasLeft());
    }

    @Test
    @Disabled("Requires Minecraft CCA runtime")
    public void exposesRoundEndBridgeMethods() {
        GameRoundEndComponent component = new GameRoundEndComponent(null);

        assertThrows(IllegalStateException.class, () -> AdvancedSparkRoundEndBridge.getPlayers(component));
        assertThrows(IllegalStateException.class, () -> AdvancedSparkRoundEndBridge.getRoundGameMode(component));
    }
}
