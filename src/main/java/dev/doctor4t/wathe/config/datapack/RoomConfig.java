package dev.doctor4t.wathe.config.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

/**
 * English: Minimal Spark-wathe room config model for map enhancement compatibility.
 * Chinese: 用于地图增强兼容的最小 Spark-wathe 房间配置模型。
 */
public record RoomConfig(
        List<SpawnPoint> spawnPoints,
        Optional<Integer> maxPlayers,
        Optional<String> name
) {
    public static final Codec<RoomConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpawnPoint.CODEC.listOf().fieldOf("spawn_points").forGetter(RoomConfig::spawnPoints),
            Codec.INT.optionalFieldOf("max_players").forGetter(RoomConfig::maxPlayers),
            Codec.STRING.optionalFieldOf("name").forGetter(RoomConfig::name)
    ).apply(instance, RoomConfig::new));

    public RoomConfig {
        spawnPoints = List.copyOf(spawnPoints);
        maxPlayers = maxPlayers == null ? Optional.empty() : maxPlayers;
        name = name == null ? Optional.empty() : name;
    }

    public String getName(int roomNumber) {
        return this.name.orElse("Room " + roomNumber);
    }

    public int getMaxPlayers() {
        return this.maxPlayers.orElse(this.spawnPoints.size());
    }

    public SpawnPoint getSpawnPoint(int playerIndex) {
        if (this.spawnPoints.isEmpty()) {
            throw new IllegalStateException("Room has no spawn points configured");
        }
        return this.spawnPoints.get(playerIndex % this.spawnPoints.size());
    }

    public record SpawnPoint(double x, double y, double z, float yaw, float pitch) {
        public static final Codec<SpawnPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter(SpawnPoint::x),
                Codec.DOUBLE.fieldOf("y").forGetter(SpawnPoint::y),
                Codec.DOUBLE.fieldOf("z").forGetter(SpawnPoint::z),
                Codec.FLOAT.fieldOf("yaw").forGetter(SpawnPoint::yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(SpawnPoint::pitch)
        ).apply(instance, SpawnPoint::new));

        public Vec3d toVec3d() {
            return new Vec3d(this.x, this.y, this.z);
        }
    }
}
