package io.github.caecorthus.advancedspark.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * English: Kill lifecycle callbacks for role and round systems.
 * Chinese: 面向职业和回合系统的击杀生命周期回调。
 */
public final class KillEvents {
    public static final Event<BeforeKill> BEFORE_KILL = EventFactory.createArrayBacked(
            BeforeKill.class,
            callbacks -> (killer, victim) -> {
                for (BeforeKill callback : callbacks) {
                    ActionResult result = callback.beforeKill(killer, victim);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    public static final Event<AfterKill> AFTER_KILL = EventFactory.createArrayBacked(
            AfterKill.class,
            callbacks -> (killer, victim) -> {
                for (AfterKill callback : callbacks) {
                    callback.afterKill(killer, victim);
                }
            }
    );

    private KillEvents() {
    }

    public static boolean shouldCancelVanillaKill(ActionResult result) {
        return result == ActionResult.FAIL;
    }

    public enum Decision {
        PASS,
        ALLOW,
        CANCEL
    }

    public static final class Bridge<TPlayer> {
        private final List<BridgeBeforeKill<TPlayer>> beforeKillCallbacks = new CopyOnWriteArrayList<>();
        private final List<BridgeAfterKill<TPlayer>> afterKillCallbacks = new CopyOnWriteArrayList<>();

        public void registerBeforeKill(BridgeBeforeKill<TPlayer> callback) {
            beforeKillCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public void registerAfterKill(BridgeAfterKill<TPlayer> callback) {
            afterKillCallbacks.add(Objects.requireNonNull(callback, "callback"));
        }

        public Decision dispatchBeforeKill(TPlayer killer, TPlayer victim) {
            for (BridgeBeforeKill<TPlayer> callback : beforeKillCallbacks) {
                Decision decision = Objects.requireNonNull(callback.beforeKill(killer, victim), "decision");
                if (decision != Decision.PASS) {
                    return decision;
                }
            }
            return Decision.PASS;
        }

        public Decision dispatchKill(TPlayer killer, TPlayer victim, Runnable vanillaKill) {
            Objects.requireNonNull(vanillaKill, "vanillaKill");
            Decision decision = dispatchBeforeKill(killer, victim);
            if (decision == Decision.CANCEL) {
                return decision;
            }
            vanillaKill.run();
            dispatchAfterKill(killer, victim);
            return decision;
        }

        public void dispatchAfterKill(TPlayer killer, TPlayer victim) {
            for (BridgeAfterKill<TPlayer> callback : afterKillCallbacks) {
                callback.afterKill(killer, victim);
            }
        }
    }

    @FunctionalInterface
    public interface BridgeBeforeKill<TPlayer> {
        Decision beforeKill(TPlayer killer, TPlayer victim);
    }

    @FunctionalInterface
    public interface BridgeAfterKill<TPlayer> {
        void afterKill(TPlayer killer, TPlayer victim);
    }

    @FunctionalInterface
    public interface BeforeKill {
        ActionResult beforeKill(ServerPlayerEntity killer, ServerPlayerEntity victim);
    }

    @FunctionalInterface
    public interface AfterKill {
        void afterKill(ServerPlayerEntity killer, ServerPlayerEntity victim);
    }
}
