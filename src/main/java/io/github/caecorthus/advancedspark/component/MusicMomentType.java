package io.github.caecorthus.advancedspark.component;

/**
 * English: Shared world music moment identifiers ported from NoellesRoles.
 * Chinese: 从 NoellesRoles 迁移来的世界音乐时刻标识。
 */
public enum MusicMomentType {
    NONE,
    CORRUPT_COP_MOMENT,
    JESTER_MOMENT;

    public static MusicMomentType fromString(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException exception) {
            return NONE;
        }
    }
}
