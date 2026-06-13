package io.github.caecorthus.advancedspark.content;

import io.github.caecorthus.advancedspark.AdvancedSparkSourceIds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSparkBaseRolesTest {
    @Test
    public void veteranPreservesSparkWatheNamespaceAndValues() {
        assertEquals(AdvancedSparkSourceIds.sparkWathe("veteran"), AdvancedSparkBaseRoles.VETERAN.identifier());
        assertEquals(2, AdvancedSparkBaseRoles.VETERAN_STAB_USES);
        assertEquals(200, AdvancedSparkBaseRoles.VETERAN_MAX_SPRINT_TICKS);
    }
}
