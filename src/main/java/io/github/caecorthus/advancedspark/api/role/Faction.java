package io.github.caecorthus.advancedspark.api.role;

/**
 * English: Coarse team alignment for future Spark-compatible roles.
 * Chinese: 面向未来 Spark 兼容职业的基础阵营分类。
 */
public enum Faction {
    CREWMATE,
    IMPOSTOR,
    NEUTRAL,
    UNKNOWN;

    public boolean isCrewAligned() {
        return this == CREWMATE;
    }

    public boolean isImpostorAligned() {
        return this == IMPOSTOR;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }
}
