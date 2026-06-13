package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe gun punishment override event.
 * Chinese: Spark-wathe 枪击惩罚覆盖事件。
 */
public interface ShouldPunishGunShooter {
    Event<ShouldPunishGunShooter> EVENT = EventFactory.createArrayBacked(
            ShouldPunishGunShooter.class,
            callbacks -> (shooter, victim) -> {
                for (ShouldPunishGunShooter callback : callbacks) {
                    PunishResult result = callback.shouldPunish(shooter, victim);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
    );

    @Nullable
    PunishResult shouldPunish(@NotNull PlayerEntity shooter, @NotNull PlayerEntity victim);

    final class PunishResult {
        private final PunishType type;
        private final Runnable customPunishment;

        private PunishResult(PunishType type, Runnable customPunishment) {
            this.type = type;
            this.customPunishment = customPunishment;
        }

        public static PunishResult cancel() {
            return new PunishResult(PunishType.CANCEL, null);
        }

        public static PunishResult allow() {
            return new PunishResult(PunishType.ALLOW, null);
        }

        public static PunishResult custom(@NotNull Runnable customPunishment) {
            return new PunishResult(PunishType.CUSTOM, customPunishment);
        }

        public PunishType getType() {
            return this.type;
        }

        public boolean shouldPunish() {
            return this.type != PunishType.CANCEL;
        }

        public boolean hasCustomPunishment() {
            return this.type == PunishType.CUSTOM && this.customPunishment != null;
        }

        public void executeCustomPunishment() {
            if (this.customPunishment != null) {
                this.customPunishment.run();
            }
        }

        public enum PunishType {
            CANCEL,
            ALLOW,
            CUSTOM
        }
    }
}
