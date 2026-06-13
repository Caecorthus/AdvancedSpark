package dev.doctor4t.wathe.config.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * English: Spark-wathe compatible map enhancement configuration model.
 * Chinese: 与 Spark-wathe 兼容的地图增强配置模型。
 */
public record MapEnhancementsConfiguration(
        List<RoomConfig> rooms,
        Optional<SceneryConfig> scenery,
        Optional<VisibilityConfig> visibility,
        Optional<FogConfig> fog,
        Optional<CameraShakeConfig> cameraShake,
        Optional<InteractionBlacklistConfig> interactionBlacklist,
        Optional<GravityConfig> gravity,
        Optional<MovementConfig> movement,
        Optional<JumpConfig> jump,
        Optional<AmbienceConfig> ambience,
        Optional<SpecialRolesConfig> specialRoles
) {
    public static final MapEnhancementsConfiguration EMPTY = new MapEnhancementsConfiguration(
            List.of(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final Codec<MapEnhancementsConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RoomConfig.CODEC.listOf().optionalFieldOf("rooms", List.of()).forGetter(MapEnhancementsConfiguration::rooms),
            SceneryConfig.CODEC.optionalFieldOf("scenery").forGetter(MapEnhancementsConfiguration::scenery),
            VisibilityConfig.CODEC.optionalFieldOf("visibility").forGetter(MapEnhancementsConfiguration::visibility),
            FogConfig.CODEC.optionalFieldOf("fog").forGetter(MapEnhancementsConfiguration::fog),
            CameraShakeConfig.CODEC.optionalFieldOf("camera_shake").forGetter(MapEnhancementsConfiguration::cameraShake),
            InteractionBlacklistConfig.CODEC.optionalFieldOf("interaction_blacklist").forGetter(MapEnhancementsConfiguration::interactionBlacklist),
            GravityConfig.CODEC.optionalFieldOf("gravity").forGetter(MapEnhancementsConfiguration::gravity),
            MovementConfig.CODEC.optionalFieldOf("movement").forGetter(MapEnhancementsConfiguration::movement),
            JumpConfig.CODEC.optionalFieldOf("jump").forGetter(MapEnhancementsConfiguration::jump),
            AmbienceConfig.CODEC.optionalFieldOf("ambience").forGetter(MapEnhancementsConfiguration::ambience),
            SpecialRolesConfig.CODEC.optionalFieldOf("special_roles").forGetter(MapEnhancementsConfiguration::specialRoles)
    ).apply(instance, MapEnhancementsConfiguration::new));

    public MapEnhancementsConfiguration {
        rooms = List.copyOf(rooms);
        scenery = scenery == null ? Optional.empty() : scenery;
        visibility = visibility == null ? Optional.empty() : visibility;
        fog = fog == null ? Optional.empty() : fog;
        cameraShake = cameraShake == null ? Optional.empty() : cameraShake;
        interactionBlacklist = interactionBlacklist == null ? Optional.empty() : interactionBlacklist;
        gravity = gravity == null ? Optional.empty() : gravity;
        movement = movement == null ? Optional.empty() : movement;
        jump = jump == null ? Optional.empty() : jump;
        ambience = ambience == null ? Optional.empty() : ambience;
        specialRoles = specialRoles == null ? Optional.empty() : specialRoles;
    }

    public SceneryConfig getSceneryOrDefault() {
        return this.scenery.orElse(SceneryConfig.DEFAULT);
    }

    public VisibilityConfig getVisibilityOrDefault() {
        return this.visibility.orElse(VisibilityConfig.DEFAULT);
    }

    public FogConfig getFogOrDefault() {
        return this.fog.orElse(FogConfig.DEFAULT);
    }

    public CameraShakeConfig getCameraShakeOrDefault() {
        return this.cameraShake.orElse(CameraShakeConfig.DEFAULT);
    }

    public InteractionBlacklistConfig getInteractionBlacklistOrDefault() {
        return this.interactionBlacklist.orElse(InteractionBlacklistConfig.DEFAULT);
    }

    public GravityConfig getGravityOrDefault() {
        return this.gravity.orElse(GravityConfig.DEFAULT);
    }

    public MovementConfig getMovementOrDefault() {
        return this.movement.orElse(MovementConfig.DEFAULT);
    }

    public JumpConfig getJumpOrDefault() {
        return this.jump.orElse(JumpConfig.DEFAULT);
    }

    public AmbienceConfig getAmbienceOrDefault() {
        return this.ambience.orElse(AmbienceConfig.DEFAULT);
    }

    public SpecialRolesConfig getSpecialRolesOrDefault() {
        return this.specialRoles.orElse(SpecialRolesConfig.DEFAULT);
    }

    public int getRoomCount() {
        return this.rooms.size();
    }

    public Optional<RoomConfig> getRoomConfig(int roomNumber) {
        if (roomNumber < 0 || roomNumber >= this.rooms.size()) {
            return Optional.empty();
        }
        return Optional.of(this.rooms.get(roomNumber));
    }

    public Optional<RoomConfig.SpawnPoint> getSpawnPointForPlayer(int roomNumber, int playerIndexInRoom) {
        return this.getRoomConfig(roomNumber)
                .filter(room -> !room.spawnPoints().isEmpty())
                .map(room -> room.getSpawnPoint(Math.max(0, playerIndexInRoom)));
    }

    public int getTotalCapacity() {
        return this.rooms.stream().mapToInt(RoomConfig::getMaxPlayers).sum();
    }

    public record SceneryConfig(int heightOffset, int minX, int maxX, int minZ, int maxZ) {
        public static final SceneryConfig DEFAULT = new SceneryConfig(116, -208, 303, -896, -177);
        public static final Codec<SceneryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("height_offset", 116).forGetter(SceneryConfig::heightOffset),
                Codec.INT.optionalFieldOf("min_x", -208).forGetter(SceneryConfig::minX),
                Codec.INT.optionalFieldOf("max_x", 303).forGetter(SceneryConfig::maxX),
                Codec.INT.optionalFieldOf("min_z", -896).forGetter(SceneryConfig::minZ),
                Codec.INT.optionalFieldOf("max_z", -177).forGetter(SceneryConfig::maxZ)
        ).apply(instance, SceneryConfig::new));
    }

    public record VisibilityConfig(int day, int night, int sundown) {
        public static final VisibilityConfig DEFAULT = new VisibilityConfig(400, 200, 300);
        public static final Codec<VisibilityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("day").forGetter(VisibilityConfig::day),
                Codec.INT.fieldOf("night").forGetter(VisibilityConfig::night),
                Codec.INT.fieldOf("sundown").forGetter(VisibilityConfig::sundown)
        ).apply(instance, VisibilityConfig::new));
    }

    public record FogConfig(float start, float endMoving, float endStationary, int nightColor) {
        public static final FogConfig DEFAULT = new FogConfig(32.0f, 96.0f, 64.0f, 0x0D0D14);
        public static final Codec<FogConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("start", 32.0f).forGetter(FogConfig::start),
                Codec.FLOAT.optionalFieldOf("end_moving", 96.0f).forGetter(FogConfig::endMoving),
                Codec.FLOAT.optionalFieldOf("end_stationary", 64.0f).forGetter(FogConfig::endStationary),
                Codec.INT.optionalFieldOf("night_color", 0x0D0D14).forGetter(FogConfig::nightColor)
        ).apply(instance, FogConfig::new));
    }

    public record CameraShakeConfig(boolean enabled, float amplitudeIndoor, float amplitudeOutdoor, float strengthIndoor, float strengthOutdoor) {
        public static final CameraShakeConfig DEFAULT = new CameraShakeConfig(true, 0.002f, 0.006f, 0.04f, 0.08f);
        public static final Codec<CameraShakeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("enabled", true).forGetter(CameraShakeConfig::enabled),
                Codec.FLOAT.optionalFieldOf("amplitude_indoor", 0.002f).forGetter(CameraShakeConfig::amplitudeIndoor),
                Codec.FLOAT.optionalFieldOf("amplitude_outdoor", 0.006f).forGetter(CameraShakeConfig::amplitudeOutdoor),
                Codec.FLOAT.optionalFieldOf("strength_indoor", 0.04f).forGetter(CameraShakeConfig::strengthIndoor),
                Codec.FLOAT.optionalFieldOf("strength_outdoor", 0.08f).forGetter(CameraShakeConfig::strengthOutdoor)
        ).apply(instance, CameraShakeConfig::new));
    }

    public record SnowParticlesConfig(int count, float spawnOffsetX, float spawnRangeY, float spawnRangeZ) {
        public static final SnowParticlesConfig DEFAULT = new SnowParticlesConfig(350, -180.0f, 48.0f, 32.0f);
        public static final Codec<SnowParticlesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("count", 350).forGetter(SnowParticlesConfig::count),
                Codec.FLOAT.optionalFieldOf("spawn_offset_x", -180.0f).forGetter(SnowParticlesConfig::spawnOffsetX),
                Codec.FLOAT.optionalFieldOf("spawn_range_y", 48.0f).forGetter(SnowParticlesConfig::spawnRangeY),
                Codec.FLOAT.optionalFieldOf("spawn_range_z", 32.0f).forGetter(SnowParticlesConfig::spawnRangeZ)
        ).apply(instance, SnowParticlesConfig::new));
    }

    public record InteractionBlacklistConfig(List<String> blocks, List<String> blockTags) {
        public static final InteractionBlacklistConfig DEFAULT = new InteractionBlacklistConfig(List.of(), List.of());
        public static final Codec<InteractionBlacklistConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().optionalFieldOf("blocks", List.of()).forGetter(InteractionBlacklistConfig::blocks),
                Codec.STRING.listOf().optionalFieldOf("block_tags", List.of()).forGetter(InteractionBlacklistConfig::blockTags)
        ).apply(instance, InteractionBlacklistConfig::new));

        public InteractionBlacklistConfig {
            blocks = List.copyOf(blocks);
            blockTags = List.copyOf(blockTags);
        }

        public boolean isBlacklisted(Block block) {
            Identifier blockId = Registries.BLOCK.getId(block);
            if (this.blocks.contains(blockId.toString())) {
                return true;
            }
            for (String tagName : this.blockTags) {
                Identifier tagId = Identifier.tryParse(tagName);
                if (tagId != null && block.getDefaultState().isIn(TagKey.of(RegistryKeys.BLOCK, tagId))) {
                    return true;
                }
            }
            return false;
        }

        public Set<Block> getBlacklistedBlocks() {
            Set<Block> result = new HashSet<>();
            for (String id : this.blocks) {
                Identifier identifier = Identifier.tryParse(id);
                if (identifier != null) {
                    result.add(Registries.BLOCK.get(identifier));
                }
            }
            return result;
        }
    }

    public record GravityConfig(float gravityMultiplier) {
        public static final GravityConfig DEFAULT = new GravityConfig(1.0f);
        public static final Codec<GravityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("gravity_multiplier", 1.0f).forGetter(GravityConfig::gravityMultiplier)
        ).apply(instance, GravityConfig::new));
    }

    public record MovementConfig(float walkSpeedMultiplier, float sprintSpeedMultiplier) {
        public static final MovementConfig DEFAULT = new MovementConfig(1.0f, 1.0f);
        public static final Codec<MovementConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("walk_speed_multiplier", 1.0f).forGetter(MovementConfig::walkSpeedMultiplier),
                Codec.FLOAT.optionalFieldOf("sprint_speed_multiplier", 1.0f).forGetter(MovementConfig::sprintSpeedMultiplier)
        ).apply(instance, MovementConfig::new));
    }

    public record JumpConfig(boolean allowed, float staminaCost) {
        public static final JumpConfig DEFAULT = new JumpConfig(false, 0.0f);
        public static final Codec<JumpConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("allowed", false).forGetter(JumpConfig::allowed),
                Codec.FLOAT.optionalFieldOf("stamina_cost", 0.0f).forGetter(JumpConfig::staminaCost)
        ).apply(instance, JumpConfig::new));
    }

    public record AmbienceConfig(boolean requireTrainMoving, Optional<String> insideSound, Optional<String> outsideSound) {
        public static final String DEFAULT_INSIDE_SOUND = "wathe:ambient.train.inside";
        public static final String DEFAULT_OUTSIDE_SOUND = "wathe:ambient.train.outside";
        public static final AmbienceConfig DEFAULT = new AmbienceConfig(
                true,
                Optional.of(DEFAULT_INSIDE_SOUND),
                Optional.of(DEFAULT_OUTSIDE_SOUND)
        );
        public static final Codec<AmbienceConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("require_train_moving", true).forGetter(AmbienceConfig::requireTrainMoving),
                Codec.STRING.optionalFieldOf("inside_sound").forGetter(AmbienceConfig::insideSound),
                Codec.STRING.optionalFieldOf("outside_sound").forGetter(AmbienceConfig::outsideSound)
        ).apply(instance, AmbienceConfig::new));

        public AmbienceConfig {
            insideSound = insideSound == null ? Optional.empty() : insideSound;
            outsideSound = outsideSound == null ? Optional.empty() : outsideSound;
        }
    }

    public record SpecialRolesConfig(List<String> enabledRoles) {
        public static final SpecialRolesConfig DEFAULT = new SpecialRolesConfig(List.of());
        public static final Codec<SpecialRolesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().optionalFieldOf("enabled_roles", List.of()).forGetter(SpecialRolesConfig::enabledRoles)
        ).apply(instance, SpecialRolesConfig::new));

        public SpecialRolesConfig {
            enabledRoles = List.copyOf(enabledRoles);
        }
    }
}
