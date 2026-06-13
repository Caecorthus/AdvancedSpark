package io.github.caecorthus.advancedspark;

import io.github.caecorthus.advancedspark.bridge.AdvancedSparkComponentLifecycleBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkShopBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkKillHistoryBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkRoleLifecycleBridge;
import io.github.caecorthus.advancedspark.content.AdvancedSparkBaseRoles;
import io.github.caecorthus.advancedspark.content.AdvancedSparkContentBootstrap;
import io.github.caecorthus.advancedspark.content.AdvancedSparkDataComponentTypes;
import io.github.caecorthus.advancedspark.content.AdvancedSparkEntityTypes;
import io.github.caecorthus.advancedspark.content.AdvancedSparkItems;
import io.github.caecorthus.advancedspark.content.AdvancedSparkSoundEvents;
import io.github.caecorthus.advancedspark.content.AdvancedSparkStatusEffects;
import dev.doctor4t.wathe.index.WatheAttributes;
import dev.doctor4t.wathe.config.datapack.MapEnhancementsConfigurationReloader;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * English: Fabric entrypoint for AdvancedSpark's foundation layer.
 * Chinese: AdvancedSpark 基础层的 Fabric 入口点。
 */
public final class AdvancedSpark implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdvancedSparkConstants.MOD_NAME);

    @Override
    public void onInitialize() {
        WatheAttributes.initialize();
        AdvancedSparkRoleLifecycleBridge.initialize();
        AdvancedSparkComponentLifecycleBridge.initialize();
        AdvancedSparkKillHistoryBridge.initialize();
        AdvancedSparkItems.register();
        AdvancedSparkStatusEffects.register();
        AdvancedSparkSoundEvents.register();
        AdvancedSparkDataComponentTypes.register();
        AdvancedSparkEntityTypes.register();
        MapEnhancementsConfigurationReloader.register();
        AdvancedSparkContentBootstrap.initialize();
        AdvancedSparkBaseRoles.register();
        AdvancedSparkShopBridge.buildWatheShopEntries();
        LOGGER.info("{} foundation initialized.", AdvancedSparkConstants.MOD_NAME);
    }
}
