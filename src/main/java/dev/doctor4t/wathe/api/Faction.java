package dev.doctor4t.wathe.api;

import java.lang.reflect.Field;

/**
 * English: Spark-wathe role faction used by migrated role logic and win grouping.
 * Chinese: Spark-wathe 职业阵营，用于迁移职业逻辑与胜利分组。
 */
public enum Faction {
    NONE,
    CIVILIAN,
    KILLER,
    NEUTRAL;

    public static Faction fromRole(Role role) {
        if (role == null || isNoRole(role)) {
            return NONE;
        }
        if (role.isInnocent()) {
            return CIVILIAN;
        }
        if (role.canUseKiller()) {
            return KILLER;
        }
        return NEUTRAL;
    }

    private static boolean isNoRole(Role role) {
        try {
            Field noRole = WatheRoles.class.getField("NO_ROLE");
            return noRole.get(null) == role;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
