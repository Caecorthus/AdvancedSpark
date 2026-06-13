package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.cca.MapEnhancementsWorldComponent;
import dev.doctor4t.wathe.cca.MapVotingComponent;
import dev.doctor4t.wathe.cca.PlayerStaminaComponent;
import dev.doctor4t.wathe.cca.PlayerVeteranComponent;
import io.github.caecorthus.advancedspark.component.AbilityPlayerComponent;
import io.github.caecorthus.advancedspark.component.ConfigWorldComponent;
import io.github.caecorthus.advancedspark.component.HiddenBodiesWorldComponent;
import io.github.caecorthus.advancedspark.component.KillHistoryWorldComponent;
import io.github.caecorthus.advancedspark.component.WorldMusicComponent;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

/**
 * English: Cardinal Components entrypoint for Spark-wathe compatibility shims.
 * Chinese: Spark-wathe 兼容垫片使用的 Cardinal Components 入口点。
 */
public final class AdvancedSparkWatheComponents implements EntityComponentInitializer, WorldComponentInitializer, ScoreboardComponentInitializer {
    @Override
    public void registerWorldComponentFactories(@NotNull WorldComponentFactoryRegistry registry) {
        AdvancedSparkComponentRegistration.registerWorldComponent(
                registry,
                MapEnhancementsWorldComponent.KEY,
                MapEnhancementsWorldComponent::new
        );
        AdvancedSparkComponentRegistration.registerWorldComponent(
                registry,
                ConfigWorldComponent.KEY,
                ConfigWorldComponent::new
        );
        AdvancedSparkComponentRegistration.registerWorldComponent(
                registry,
                WorldMusicComponent.KEY,
                WorldMusicComponent::new
        );
        AdvancedSparkComponentRegistration.registerWorldComponent(
                registry,
                HiddenBodiesWorldComponent.KEY,
                HiddenBodiesWorldComponent::new
        );
        AdvancedSparkComponentRegistration.registerWorldComponent(
                registry,
                KillHistoryWorldComponent.KEY,
                KillHistoryWorldComponent::new
        );
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        AdvancedSparkComponentRegistration.registerPlayerComponent(
                registry,
                PlayerStaminaComponent.KEY,
                PlayerStaminaComponent::new
        );
        AdvancedSparkComponentRegistration.registerPlayerComponent(
                registry,
                PlayerVeteranComponent.KEY,
                PlayerVeteranComponent::new
        );
        AdvancedSparkComponentRegistration.registerPlayerComponent(
                registry,
                AbilityPlayerComponent.KEY,
                AbilityPlayerComponent::new
        );
    }

    @Override
    public void registerScoreboardComponentFactories(@NotNull ScoreboardComponentFactoryRegistry registry) {
        AdvancedSparkComponentRegistration.registerScoreboardComponent(
                registry,
                MapVotingComponent.KEY,
                MapVotingComponent::new
        );
    }
}
