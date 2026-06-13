package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.game.GameFunctions;
import io.github.caecorthus.advancedspark.api.event.WinEvents;
import io.github.caecorthus.advancedspark.state.AdvancedSparkGameState;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSparkWinBridgeTest {
    @Test
    public void mapsWatheWinStatusesToAdvancedSparkWinnerIds() {
        assertEquals(Optional.empty(), AdvancedSparkWinBridge.toWinnerId(GameFunctions.WinStatus.NONE));
        assertEquals(Optional.of(Identifier.of("wathe", "killers")), AdvancedSparkWinBridge.toWinnerId(GameFunctions.WinStatus.KILLERS));
        assertEquals(Optional.of(Identifier.of("wathe", "passengers")), AdvancedSparkWinBridge.toWinnerId(GameFunctions.WinStatus.PASSENGERS));
        assertEquals(Optional.of(Identifier.of("wathe", "time")), AdvancedSparkWinBridge.toWinnerId(GameFunctions.WinStatus.TIME));
        assertEquals(Optional.of(Identifier.of("wathe", "loose_end")), AdvancedSparkWinBridge.toWinnerId(GameFunctions.WinStatus.LOOSE_END));
    }

    @Test
    public void neutralStateWinnerTakesPriorityOverEventAndVanillaWinner() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        Identifier stateWinner = Identifier.of("advancedspark_test", "state_neutral");
        Identifier eventWinner = Identifier.of("advancedspark_test", "event_neutral");
        state.setNeutralWinner(stateWinner);

        assertEquals(
                Optional.of(stateWinner),
                AdvancedSparkWinBridge.resolveWinnerId(
                        state,
                        Optional.of(eventWinner),
                        GameFunctions.WinStatus.KILLERS
                )
        );
    }

    @Test
    public void eventNeutralWinnerTakesPriorityOverVanillaWinner() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        Identifier eventWinner = Identifier.of("advancedspark_test", "event_neutral");

        assertEquals(
                Optional.of(eventWinner),
                AdvancedSparkWinBridge.resolveWinnerId(
                        state,
                        Optional.of(eventWinner),
                        GameFunctions.WinStatus.PASSENGERS
                )
        );
    }

    @Test
    public void vanillaWinnerIsUsedWhenNoNeutralWinnerExists() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();

        assertEquals(
                Optional.of(Identifier.of("wathe", "passengers")),
                AdvancedSparkWinBridge.resolveWinnerId(
                        state,
                        Optional.empty(),
                        GameFunctions.WinStatus.PASSENGERS
                )
        );
    }

    @Test
    public void existingNeutralStateIsNotOverwrittenByLaterEventWinner() {
        AdvancedSparkGameState state = new AdvancedSparkGameState();
        Identifier stateWinner = Identifier.of("advancedspark_test", "state_neutral");
        Identifier eventWinner = Identifier.of("advancedspark_test", "event_neutral");
        state.setNeutralWinner(stateWinner);

        assertEquals(
                Optional.of(stateWinner),
                AdvancedSparkWinBridge.resolveWinnerId(
                        state,
                        Optional.of(eventWinner),
                        GameFunctions.WinStatus.LOOSE_END
                )
        );
    }

    @Test
    public void dispatchesSparkStyleWinDeterminedEventBeforeRoundEndConsumers() {
        AtomicReference<GameFunctions.WinStatus> seenStatus = new AtomicReference<>();

        WinEvents.WIN_DETERMINED.register((world, gameComponent, winStatus, neutralWinner) -> seenStatus.set(winStatus));

        AdvancedSparkWinBridge.dispatchWinDetermined(null, null, GameFunctions.WinStatus.KILLERS, null);

        assertEquals(GameFunctions.WinStatus.KILLERS, seenStatus.get());
    }
}
