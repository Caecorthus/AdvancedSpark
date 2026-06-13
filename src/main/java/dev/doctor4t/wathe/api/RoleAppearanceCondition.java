package dev.doctor4t.wathe.api;

/**
 * English: Spark-wathe role appearance condition shim for migrated role selection rules.
 * Chinese: 面向已迁移职业选择规则的 Spark-wathe 职业出现条件垫片。
 */
@FunctionalInterface
public interface RoleAppearanceCondition {
    RoleAppearanceCondition ALWAYS = context -> true;

    boolean shouldAppear(RoleSelectionContext context);

    default RoleAppearanceCondition and(RoleAppearanceCondition other) {
        return context -> this.shouldAppear(context) && other.shouldAppear(context);
    }

    default RoleAppearanceCondition or(RoleAppearanceCondition other) {
        return context -> this.shouldAppear(context) || other.shouldAppear(context);
    }

    default RoleAppearanceCondition negate() {
        return context -> !this.shouldAppear(context);
    }

    static RoleAppearanceCondition minPlayers(int minPlayers) {
        return context -> context.getTotalPlayerCount() >= minPlayers;
    }

    static RoleAppearanceCondition maxPlayers(int maxPlayers) {
        return context -> context.getTotalPlayerCount() <= maxPlayers;
    }

    static RoleAppearanceCondition playerCountBetween(int minPlayers, int maxPlayers) {
        return context -> context.getTotalPlayerCount() >= minPlayers
                && context.getTotalPlayerCount() <= maxPlayers;
    }

    static RoleAppearanceCondition minKillers(int minKillers) {
        return context -> context.getTargetKillerCount() >= minKillers;
    }

    static RoleAppearanceCondition maxKillers(int maxKillers) {
        return context -> context.getTargetKillerCount() <= maxKillers;
    }

    static RoleAppearanceCondition minNeutrals(int minNeutrals) {
        return context -> context.getTargetNeutralCount() >= minNeutrals;
    }

    static RoleAppearanceCondition maxNeutrals(int maxNeutrals) {
        return context -> context.getTargetNeutralCount() <= maxNeutrals;
    }
}
