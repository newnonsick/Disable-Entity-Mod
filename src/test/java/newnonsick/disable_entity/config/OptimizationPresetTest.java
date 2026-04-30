package newnonsick.disable_entity.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OptimizationPresetTest {
    @Test
    void balancedPresetDetectsAfterApply() {
        DisableEntityConfig config = new DisableEntityConfig();

        OptimizationPreset.BALANCED.apply(config);

        assertEquals(OptimizationPreset.BALANCED, OptimizationPreset.detect(config));
    }

    @Test
    void manualChangeMovesPresetToCustom() {
        DisableEntityConfig config = new DisableEntityConfig();

        OptimizationPreset.PERFORMANCE.apply(config);
        config.entityRendering.hidePlayers = false;

        assertEquals(OptimizationPreset.CUSTOM, OptimizationPreset.detect(config));
    }

    @Test
    void blockStateChangeMovesPresetToCustom() {
        DisableEntityConfig config = new DisableEntityConfig();

        OptimizationPreset.BALANCED.apply(config);
        config.blockStates.freezeRedstone = false;

        assertEquals(OptimizationPreset.CUSTOM, OptimizationPreset.detect(config));
    }
}