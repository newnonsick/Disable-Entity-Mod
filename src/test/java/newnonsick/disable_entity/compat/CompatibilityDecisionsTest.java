package newnonsick.disable_entity.compat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import newnonsick.disable_entity.config.DisableEntityConfig;

class CompatibilityDecisionsTest {
    @Test
    void entityDistanceCullingIsDisabledWhenExternalCullingIsLoaded() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.distanceCulling.enabled = true;
        config.distanceCulling.entityDistanceLimitEnabled = true;

        assertFalse(CompatibilityDecisions.shouldUseEntityDistanceCulling(config, true, false));
        assertFalse(CompatibilityDecisions.shouldUseEntityDistanceCulling(config, false, true));
    }

    @Test
    void distanceCullingStaysEnabledWithoutOverlap() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.distanceCulling.enabled = true;
        config.distanceCulling.entityDistanceLimitEnabled = true;
        config.distanceCulling.blockEntityDistanceLimitEnabled = true;

        assertTrue(CompatibilityDecisions.shouldUseEntityDistanceCulling(config, false, false));
        assertTrue(CompatibilityDecisions.shouldUseBlockEntityDistanceCulling(config, false, false));
    }

    @Test
    void blockStateFreezingIsEnabledWhenConfigSaysSo() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.blockStates.enabled = true;

        assertTrue(CompatibilityDecisions.shouldUseBlockStateFreezing(config));
    }

    @Test
    void blockStateFreezingIsDisabledWhenGlobalOff() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.globalEnabled = false;
        config.blockStates.enabled = true;

        assertFalse(CompatibilityDecisions.shouldUseBlockStateFreezing(config));
    }
}