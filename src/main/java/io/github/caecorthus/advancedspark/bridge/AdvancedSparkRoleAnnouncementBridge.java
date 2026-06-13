package io.github.caecorthus.advancedspark.bridge;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.client.gui.RoleAnnouncementTexts;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

/**
 * English: Registers Wathe announcement entries for AdvancedSpark-owned roles.
 * Chinese: 为 AdvancedSpark 拥有的职业注册 Wathe 入场公告条目。
 */
public final class AdvancedSparkRoleAnnouncementBridge {
    private static final Map<Identifier, Integer> ANNOUNCEMENT_INDICES = new ConcurrentHashMap<>();

    private AdvancedSparkRoleAnnouncementBridge() {
    }

    public static int register(Role role) {
        Objects.requireNonNull(role, "role");
        return ANNOUNCEMENT_INDICES.computeIfAbsent(role.identifier(), ignored -> registerAnnouncementText(role));
    }

    public static OptionalInt announcementIndex(Identifier roleId) {
        Objects.requireNonNull(roleId, "roleId");
        Integer index = ANNOUNCEMENT_INDICES.get(roleId);
        return index == null ? OptionalInt.empty() : OptionalInt.of(index);
    }

    private static int registerAnnouncementText(Role role) {
        RoleAnnouncementTexts.RoleAnnouncementText announcementText =
                new RoleAnnouncementTexts.RoleAnnouncementText(role.identifier().getPath(), role.color());
        RoleAnnouncementTexts.registerRoleAnnouncementText(announcementText);
        return RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(announcementText);
    }
}
