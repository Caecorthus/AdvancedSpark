package dev.doctor4t.wathe.config.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * English: One selectable Spark-wathe map registry entry.
 * Chinese: 一个可投票选择的 Spark-wathe 地图注册条目。
 */
public record MapRegistryEntry(
        Identifier dimensionId,
        String displayName,
        Optional<String> description,
        MapEnhancementsConfiguration enhancements,
        int minPlayers,
        int maxPlayers
) {
    public static final Codec<MapRegistryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(MapRegistryEntry::dimensionId),
            Codec.STRING.fieldOf("display_name").forGetter(MapRegistryEntry::displayName),
            Codec.STRING.optionalFieldOf("description").forGetter(MapRegistryEntry::description),
            MapEnhancementsConfiguration.CODEC.optionalFieldOf("enhancements", MapEnhancementsConfiguration.EMPTY)
                    .forGetter(MapRegistryEntry::enhancements),
            Codec.INT.optionalFieldOf("min_players", 0).forGetter(MapRegistryEntry::minPlayers),
            Codec.INT.optionalFieldOf("max_players", 100).forGetter(MapRegistryEntry::maxPlayers)
    ).apply(instance, MapRegistryEntry::new));

    public MapRegistryEntry {
        description = description == null ? Optional.empty() : description;
        enhancements = enhancements == null ? MapEnhancementsConfiguration.EMPTY : enhancements;
    }

    public boolean isEligible(int playerCount) {
        return playerCount >= this.minPlayers && playerCount <= this.maxPlayers;
    }
}
