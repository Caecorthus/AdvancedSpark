package io.github.caecorthus.advancedspark.api.role;

import java.util.Arrays;
import java.util.Objects;

/**
 * English: Predicate hook for map/player-count specific role availability.
 * Chinese: 用于地图和玩家数量等条件的职业出现判断钩子。
 */
@FunctionalInterface
public interface RoleAppearanceCondition {
    boolean canAppear(RoleSelectionContext context);

    static RoleAppearanceCondition always() {
        return context -> true;
    }

    static RoleAppearanceCondition never() {
        return context -> false;
    }

    static RoleAppearanceCondition minPlayers(int playerCount) {
        if (playerCount < 0) {
            throw new IllegalArgumentException("playerCount cannot be negative");
        }
        return context -> context.playerCount() >= playerCount;
    }

    static RoleAppearanceCondition not(RoleAppearanceCondition condition) {
        Objects.requireNonNull(condition, "condition");
        return context -> !condition.canAppear(context);
    }

    static RoleAppearanceCondition all(RoleAppearanceCondition... conditions) {
        RoleAppearanceCondition[] copy = copyConditions(conditions);
        return context -> Arrays.stream(copy).allMatch(condition -> condition.canAppear(context));
    }

    static RoleAppearanceCondition any(RoleAppearanceCondition... conditions) {
        RoleAppearanceCondition[] copy = copyConditions(conditions);
        return context -> Arrays.stream(copy).anyMatch(condition -> condition.canAppear(context));
    }

    default RoleAppearanceCondition and(RoleAppearanceCondition other) {
        Objects.requireNonNull(other, "other");
        return context -> canAppear(context) && other.canAppear(context);
    }

    default RoleAppearanceCondition or(RoleAppearanceCondition other) {
        Objects.requireNonNull(other, "other");
        return context -> canAppear(context) || other.canAppear(context);
    }

    private static RoleAppearanceCondition[] copyConditions(RoleAppearanceCondition[] conditions) {
        Objects.requireNonNull(conditions, "conditions");
        RoleAppearanceCondition[] copy = conditions.clone();
        for (RoleAppearanceCondition condition : copy) {
            Objects.requireNonNull(condition, "condition");
        }
        return copy;
    }
}
