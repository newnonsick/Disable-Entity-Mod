package newnonsick.disable_entity.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisableEntityConfigSanitizationTest {

    @Test
    void newConfigHasVersion4AndCorrectDefaults() {
        DisableEntityConfig config = new DisableEntityConfig();

        assertEquals(4, config.configVersion);
        assertFalse(config.showFpsDeltaOnToggle);
        assertFalse(config.adaptiveTuningEnabled);
        assertEquals(30, config.adaptiveTargetFps);
        assertEquals(5, config.adaptiveEscalationDelaySeconds);
        assertFalse(config.entityRendering.neverHideNamedEntities);
        assertFalse(config.entityRendering.neverHideTamedEntities);
    }

    @Test
    void sanitizeMigratesZeroAdaptiveFieldsToDefaults() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.configVersion = 3;
        config.adaptiveTargetFps = 0;
        config.adaptiveEscalationDelaySeconds = 0;

        config.sanitize();

        assertEquals(4, config.configVersion);
        assertEquals(30, config.adaptiveTargetFps);
        assertEquals(5, config.adaptiveEscalationDelaySeconds);
    }

    @Test
    void sanitizeClampsExistingAdaptiveFields() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.adaptiveTargetFps = 500;
        config.adaptiveEscalationDelaySeconds = 90;

        config.sanitize();

        assertEquals(240, config.adaptiveTargetFps);
        assertEquals(60, config.adaptiveEscalationDelaySeconds);
    }

    @Test
    void sanitizePreservesValidAdaptiveFields() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.adaptiveTargetFps = 45;
        config.adaptiveEscalationDelaySeconds = 10;

        config.sanitize();

        assertEquals(45, config.adaptiveTargetFps);
        assertEquals(10, config.adaptiveEscalationDelaySeconds);
    }
}
