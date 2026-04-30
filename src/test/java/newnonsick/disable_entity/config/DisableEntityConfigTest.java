package newnonsick.disable_entity.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import newnonsick.disable_entity.util.ParticleFilterMode;

class DisableEntityConfigTest {
    @Test
    void sanitizeRestoresMissingSectionsAndClampsDistances() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.configVersion = 0;
        config.activePreset = null;
        config.entityRendering = null;
        config.nametags = null;
        config.particles = null;
        config.blockEntities = null;
        config.blockStates = null;
        config.worldRendering = null;
        config.distanceCulling = null;

        config.sanitize();

        assertEquals(DisableEntityConfig.CURRENT_CONFIG_VERSION, config.configVersion);
        assertEquals(OptimizationPreset.CUSTOM, config.activePreset);
        assertNotNull(config.entityRendering);
        assertNotNull(config.nametags);
        assertNotNull(config.particles);
        assertNotNull(config.blockEntities);
        assertNotNull(config.blockStates);
        assertNotNull(config.worldRendering);
        assertNotNull(config.distanceCulling);
        assertEquals(ParticleFilterMode.ALL, config.particles.mode);
    }

    @Test
    void sanitizeClampsRenderDistances() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.distanceCulling.entityRenderDistance = 10_000;
        config.distanceCulling.blockEntityRenderDistance = -50;

        config.sanitize();

        assertTrue(config.distanceCulling.entityRenderDistance <= DisableEntityConfig.MAX_ENTITY_RENDER_DISTANCE);
        assertTrue(config.distanceCulling.blockEntityRenderDistance >= DisableEntityConfig.MIN_RENDER_DISTANCE);
    }

    @Test
    void blockStateRenderingDefaultsAreCorrect() {
        DisableEntityConfig config = new DisableEntityConfig();
        assertNotNull(config.blockStates);
        assertFalse(config.blockStates.enabled);
        assertTrue(config.blockStates.freezeRedstone);
        assertTrue(config.blockStates.freezePistons);
        assertFalse(config.blockStates.freezeOtherDynamic);
    }

    @Test
    void worldRenderingDefaultsAreCorrect() {
        DisableEntityConfig config = new DisableEntityConfig();
        assertNotNull(config.worldRendering);
        assertFalse(config.worldRendering.enabled);
        assertFalse(config.worldRendering.disableClouds);
        assertFalse(config.worldRendering.disableWeather);
        assertFalse(config.worldRendering.disableFog);
    }
}