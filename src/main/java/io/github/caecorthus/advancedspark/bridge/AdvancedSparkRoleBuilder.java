package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import io.github.caecorthus.advancedspark.api.role.Faction;
import io.github.caecorthus.advancedspark.api.role.RoleAppearanceCondition;
import io.github.caecorthus.advancedspark.api.role.RoleSelectionContext;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * English: Builds original Wathe roles while keeping Spark-only role metadata in AdvancedSpark.
 * Chinese: 构造原版 Wathe 职业，同时把 Spark 专属职业元数据保存在 AdvancedSpark 中。
 */
public final class AdvancedSparkRoleBuilder {
    private final Identifier identifier;
    private int color = 0xFFFFFF;
    private boolean innocent = true;
    private boolean canUseKiller;
    private Role.MoodType moodType = Role.MoodType.REAL;
    private int maxSprintTime = -1;
    private boolean canSeeTime;
    private Faction faction;
    private boolean mapSpecific;
    private boolean special;
    private RoleAppearanceCondition condition = RoleAppearanceCondition.always();

    private AdvancedSparkRoleBuilder(Identifier identifier) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
    }

    public static AdvancedSparkRoleBuilder create(Identifier identifier) {
        return new AdvancedSparkRoleBuilder(identifier);
    }

    public static AdvancedSparkRoleBuilder fromSparkRole(
            Identifier identifier,
            int color,
            boolean innocent,
            boolean canUseKiller,
            Role.MoodType moodType,
            int maxSprintTime,
            boolean canSeeTime
    ) {
        return create(identifier)
                .color(color)
                .innocent(innocent)
                .canUseKiller(canUseKiller)
                .moodType(moodType)
                .maxSprintTime(maxSprintTime)
                .canSeeTime(canSeeTime);
    }

    public static AdvancedSparkRoleBuilder fromSparkRole(
            Identifier identifier,
            int color,
            boolean innocent,
            boolean canUseKiller,
            Role.MoodType moodType,
            int maxSprintTime,
            boolean canSeeTime,
            dev.doctor4t.wathe.api.RoleAppearanceCondition condition
    ) {
        return fromSparkRole(identifier, color, innocent, canUseKiller, moodType, maxSprintTime, canSeeTime)
                .sparkAppearanceCondition(condition);
    }

    public AdvancedSparkRoleBuilder color(int color) {
        this.color = color;
        return this;
    }

    public AdvancedSparkRoleBuilder innocent(boolean innocent) {
        this.innocent = innocent;
        return this;
    }

    public AdvancedSparkRoleBuilder canUseKiller(boolean canUseKiller) {
        this.canUseKiller = canUseKiller;
        return this;
    }

    public AdvancedSparkRoleBuilder moodType(Role.MoodType moodType) {
        this.moodType = Objects.requireNonNull(moodType, "moodType");
        return this;
    }

    public AdvancedSparkRoleBuilder maxSprintTime(int maxSprintTime) {
        this.maxSprintTime = maxSprintTime;
        return this;
    }

    public AdvancedSparkRoleBuilder canSeeTime(boolean canSeeTime) {
        this.canSeeTime = canSeeTime;
        return this;
    }

    public AdvancedSparkRoleBuilder faction(Faction faction) {
        this.faction = Objects.requireNonNull(faction, "faction");
        return this;
    }

    public AdvancedSparkRoleBuilder mapSpecific(boolean mapSpecific) {
        this.mapSpecific = mapSpecific;
        return this;
    }

    public AdvancedSparkRoleBuilder special(boolean special) {
        this.special = special;
        return this;
    }

    public AdvancedSparkRoleBuilder appearanceCondition(RoleAppearanceCondition condition) {
        this.condition = Objects.requireNonNull(condition, "condition");
        return this;
    }

    public AdvancedSparkRoleBuilder sparkAppearanceCondition(dev.doctor4t.wathe.api.RoleAppearanceCondition condition) {
        Objects.requireNonNull(condition, "condition");
        this.condition = context -> condition.shouldAppear(toSparkContext(context));
        return this;
    }

    public Role build() {
        return new Role(
                identifier,
                color,
                innocent,
                canUseKiller,
                moodType,
                normalizeSparkSprintTime(maxSprintTime),
                canSeeTime
        );
    }

    public Role register() {
        return AdvancedSparkWatheRoleRegistry.register(
                build(),
                faction != null ? faction : inferFaction(),
                mapSpecific,
                special,
                condition
        );
    }

    Role registerLocal() {
        return AdvancedSparkWatheRoleRegistry.registerLocal(
                build(),
                faction != null ? faction : inferFaction(),
                mapSpecific,
                special,
                condition
        );
    }

    private Faction inferFaction() {
        if (canUseKiller) {
            return Faction.IMPOSTOR;
        }
        if (innocent) {
            return Faction.CREWMATE;
        }
        return Faction.NEUTRAL;
    }

    private static int normalizeSparkSprintTime(int maxSprintTime) {
        return maxSprintTime == Integer.MAX_VALUE ? -1 : maxSprintTime;
    }

    private static dev.doctor4t.wathe.api.RoleSelectionContext toSparkContext(RoleSelectionContext context) {
        Set<Role> assignedRoles = context.assignedRoleIds().stream()
                .map(AdvancedSparkWatheRoleRegistry::role)
                .flatMap(java.util.Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
        return new dev.doctor4t.wathe.api.RoleSelectionContext(
                context.playerCount(),
                context.targetKillerCount(),
                context.targetNeutralCount(),
                context.targetVigilanteCount(),
                assignedRoles
        );
    }
}
