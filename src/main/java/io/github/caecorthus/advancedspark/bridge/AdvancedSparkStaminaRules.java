package io.github.caecorthus.advancedspark.bridge;

import java.util.Objects;

/**
 * English: Pure Spark stamina rules shared by runtime adapters and tests.
 * Chinese: 运行时适配层和测试共用的纯 Spark 体力规则。
 */
public final class AdvancedSparkStaminaRules {
    public static final float SPRINT_DRAIN_PER_TICK = 1.0f;
    public static final float REST_RECOVERY_PER_TICK = 0.25f;
    public static final int EXHAUSTION_RECOVERY_TICKS = 300;
    public static final float EXHAUSTION_RECOVERY_THRESHOLD = EXHAUSTION_RECOVERY_TICKS * REST_RECOVERY_PER_TICK;

    private AdvancedSparkStaminaRules() {
    }

    public static TickResult tick(State state, int maxSprintTime, boolean wantsToSprint, boolean canSprintFromMood) {
        Objects.requireNonNull(state, "state");
        if (maxSprintTime < 0) {
            return new TickResult(new State(-1.0f, -1, false), wantsToSprint && canSprintFromMood);
        }

        float sprintingTicks = Math.min(Math.max(state.sprintingTicks(), 0.0f), maxSprintTime);
        boolean exhausted = state.exhausted();
        boolean canSprint = wantsToSprint && canSprintFromMood;

        if (sprintingTicks <= 0.0f) {
            canSprint = false;
            exhausted = true;
        }

        if (exhausted) {
            canSprint = false;
            if (sprintingTicks >= EXHAUSTION_RECOVERY_THRESHOLD) {
                exhausted = false;
            }
        }

        if (canSprint) {
            sprintingTicks = Math.max(sprintingTicks - SPRINT_DRAIN_PER_TICK, 0.0f);
        } else {
            sprintingTicks = Math.min(sprintingTicks + REST_RECOVERY_PER_TICK, maxSprintTime);
        }

        return new TickResult(new State(sprintingTicks, maxSprintTime, exhausted), canSprint);
    }

    public static State spendForJump(State state, float staminaCost) {
        Objects.requireNonNull(state, "state");
        if (staminaCost <= 0.0f || state.isInfinite()) {
            return state;
        }
        float sprintingTicks = Math.max(state.sprintingTicks() - staminaCost, 0.0f);
        return new State(sprintingTicks, state.maxSprintTime(), sprintingTicks <= 0.0f || state.exhausted());
    }

    public record State(float sprintingTicks, int maxSprintTime, boolean exhausted) {
        public boolean isInfinite() {
            return maxSprintTime < 0 || sprintingTicks < 0.0f || sprintingTicks >= Integer.MAX_VALUE;
        }
    }

    public record TickResult(State state, boolean sprintingAllowed) {
        public TickResult {
            Objects.requireNonNull(state, "state");
        }
    }
}
