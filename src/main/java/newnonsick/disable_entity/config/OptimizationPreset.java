package newnonsick.disable_entity.config;

import newnonsick.disable_entity.util.ParticleFilterMode;

/**
 * Recommended bundles of settings for quick setup.
 */
public enum OptimizationPreset {
    CUSTOM,
    BALANCED,
    PERFORMANCE,
    AGGRESSIVE;

    public void apply(DisableEntityConfig config) {
        switch (this) {
            case CUSTOM -> {
                return;
            }
            case BALANCED -> applyBalanced(config);
            case PERFORMANCE -> applyPerformance(config);
            case AGGRESSIVE -> applyAggressive(config);
        }
    }

    public static OptimizationPreset detect(DisableEntityConfig config) {
        if (matchesBalanced(config)) {
            return BALANCED;
        }
        if (matchesPerformance(config)) {
            return PERFORMANCE;
        }
        if (matchesAggressive(config)) {
            return AGGRESSIVE;
        }

        return CUSTOM;
    }

    private static void applyBalanced(DisableEntityConfig config) {
        config.globalEnabled = true;
        config.disableEntityShadows = true;

        config.entityRendering.enabled = true;
        config.entityRendering.hidePlayers = false;
        config.entityRendering.hideHostileMobs = true;
        config.entityRendering.hidePassiveMobs = true;
        config.entityRendering.hideItemEntities = true;
        config.entityRendering.hideProjectiles = true;
        config.entityRendering.hideArmorStands = true;
        config.entityRendering.hideExperienceOrbs = true;
        config.entityRendering.hideItemFrames = true;
        config.entityRendering.hidePaintings = true;
        config.entityRendering.hideLeashKnots = true;
        config.entityRendering.hideDisplayEntities = true;
        config.entityRendering.hideVehicles = true;
        config.entityRendering.hideMiscellaneousEntities = true;

        config.nametags.enabled = true;
        config.nametags.hidePlayers = false;
        config.nametags.hideMobs = true;
        config.nametags.hideArmorStands = true;

        config.particles.enabled = true;
        config.particles.mode = ParticleFilterMode.BLACKLIST;
        config.particles.blockParticles = true;
        config.particles.itemParticles = true;
        config.particles.smokeParticles = true;
        config.particles.flameParticles = true;
        config.particles.explosionParticles = true;
        config.particles.spellParticles = true;
        config.particles.waterParticles = true;
        config.particles.redstoneParticles = true;
        config.particles.ambientParticles = false;
        config.particles.otherParticles = false;

        config.blockEntities.enabled = true;
        config.blockEntities.hideChests = false;
        config.blockEntities.hideSigns = false;
        config.blockEntities.hideBeacons = true;
        config.blockEntities.hideSpawners = true;
        config.blockEntities.hideShulkerBoxes = true;
        config.blockEntities.hideBanners = false;
        config.blockEntities.hideFurnaces = true;
        config.blockEntities.hideEnchantingTables = true;
        config.blockEntities.hideMiscellaneousBlockEntities = true;

        config.distanceCulling.enabled = true;
        config.distanceCulling.entityDistanceLimitEnabled = true;
        config.distanceCulling.entityRenderDistance = DisableEntityConfig.DEFAULT_ENTITY_RENDER_DISTANCE;
        config.distanceCulling.blockEntityDistanceLimitEnabled = true;
        config.distanceCulling.blockEntityRenderDistance = DisableEntityConfig.DEFAULT_BLOCK_ENTITY_RENDER_DISTANCE;

        config.blockStates.enabled = true;
        config.blockStates.freezeRedstone = true;
        config.blockStates.freezePistons = true;
        config.blockStates.freezeDoors = false;
        config.blockStates.freezeRails = false;
        config.blockStates.freezeSculk = true;
        config.blockStates.freezeCrafters = false;
        config.blockStates.freezeObservers = true;
        config.blockStates.freezeRepeatersComparators = true;
        config.blockStates.freezeBells = false;
        config.blockStates.freezeOtherDynamic = false;
    }

    private static void applyPerformance(DisableEntityConfig config) {
        config.globalEnabled = true;
        config.disableEntityShadows = true;

        config.entityRendering.enabled = true;
        config.entityRendering.hidePlayers = true;
        config.entityRendering.hideHostileMobs = true;
        config.entityRendering.hidePassiveMobs = true;
        config.entityRendering.hideItemEntities = true;
        config.entityRendering.hideProjectiles = true;
        config.entityRendering.hideArmorStands = true;
        config.entityRendering.hideExperienceOrbs = true;
        config.entityRendering.hideItemFrames = true;
        config.entityRendering.hidePaintings = true;
        config.entityRendering.hideLeashKnots = true;
        config.entityRendering.hideDisplayEntities = true;
        config.entityRendering.hideVehicles = true;
        config.entityRendering.hideMiscellaneousEntities = true;

        config.nametags.enabled = true;
        config.nametags.hidePlayers = true;
        config.nametags.hideMobs = true;
        config.nametags.hideArmorStands = true;

        config.particles.enabled = true;
        config.particles.mode = ParticleFilterMode.ALL;

        config.blockEntities.enabled = true;
        config.blockEntities.hideChests = true;
        config.blockEntities.hideSigns = true;
        config.blockEntities.hideBeacons = true;
        config.blockEntities.hideSpawners = true;
        config.blockEntities.hideShulkerBoxes = true;
        config.blockEntities.hideBanners = true;
        config.blockEntities.hideFurnaces = true;
        config.blockEntities.hideEnchantingTables = true;
        config.blockEntities.hideMiscellaneousBlockEntities = true;

        config.distanceCulling.enabled = true;
        config.distanceCulling.entityDistanceLimitEnabled = true;
        config.distanceCulling.entityRenderDistance = 64;
        config.distanceCulling.blockEntityDistanceLimitEnabled = true;
        config.distanceCulling.blockEntityRenderDistance = 48;

        config.blockStates.enabled = true;
        config.blockStates.freezeRedstone = true;
        config.blockStates.freezePistons = true;
        config.blockStates.freezeDoors = true;
        config.blockStates.freezeRails = true;
        config.blockStates.freezeSculk = true;
        config.blockStates.freezeCrafters = true;
        config.blockStates.freezeObservers = true;
        config.blockStates.freezeRepeatersComparators = true;
        config.blockStates.freezeBells = true;
        config.blockStates.freezeOtherDynamic = false;
    }

    private static void applyAggressive(DisableEntityConfig config) {
        config.globalEnabled = true;
        config.disableEntityShadows = true;

        config.entityRendering.enabled = true;
        config.entityRendering.hidePlayers = true;
        config.entityRendering.hideHostileMobs = true;
        config.entityRendering.hidePassiveMobs = true;
        config.entityRendering.hideItemEntities = true;
        config.entityRendering.hideProjectiles = true;
        config.entityRendering.hideArmorStands = true;
        config.entityRendering.hideExperienceOrbs = true;
        config.entityRendering.hideItemFrames = true;
        config.entityRendering.hidePaintings = true;
        config.entityRendering.hideLeashKnots = true;
        config.entityRendering.hideDisplayEntities = true;
        config.entityRendering.hideVehicles = true;
        config.entityRendering.hideMiscellaneousEntities = true;

        config.nametags.enabled = true;
        config.nametags.hidePlayers = true;
        config.nametags.hideMobs = true;
        config.nametags.hideArmorStands = true;

        config.particles.enabled = true;
        config.particles.mode = ParticleFilterMode.ALL;

        config.blockEntities.enabled = true;
        config.blockEntities.hideChests = true;
        config.blockEntities.hideSigns = true;
        config.blockEntities.hideBeacons = true;
        config.blockEntities.hideSpawners = true;
        config.blockEntities.hideShulkerBoxes = true;
        config.blockEntities.hideBanners = true;
        config.blockEntities.hideFurnaces = true;
        config.blockEntities.hideEnchantingTables = true;
        config.blockEntities.hideMiscellaneousBlockEntities = true;

        config.distanceCulling.enabled = true;
        config.distanceCulling.entityDistanceLimitEnabled = true;
        config.distanceCulling.entityRenderDistance = 32;
        config.distanceCulling.blockEntityDistanceLimitEnabled = true;
        config.distanceCulling.blockEntityRenderDistance = 32;

        config.blockStates.enabled = true;
        config.blockStates.freezeRedstone = true;
        config.blockStates.freezePistons = true;
        config.blockStates.freezeDoors = true;
        config.blockStates.freezeRails = true;
        config.blockStates.freezeSculk = true;
        config.blockStates.freezeCrafters = true;
        config.blockStates.freezeObservers = true;
        config.blockStates.freezeRepeatersComparators = true;
        config.blockStates.freezeBells = true;
        config.blockStates.freezeOtherDynamic = true;
    }

    private static boolean matchesBalanced(DisableEntityConfig config) {
        return config.globalEnabled && config.disableEntityShadows
                && config.entityRendering.enabled && !config.entityRendering.hidePlayers
                && config.entityRendering.hideHostileMobs && config.entityRendering.hidePassiveMobs
                && config.entityRendering.hideItemEntities && config.entityRendering.hideProjectiles
                && config.entityRendering.hideArmorStands && config.entityRendering.hideExperienceOrbs
                && config.entityRendering.hideItemFrames && config.entityRendering.hidePaintings
                && config.entityRendering.hideLeashKnots && config.entityRendering.hideDisplayEntities
                && config.entityRendering.hideVehicles && config.entityRendering.hideMiscellaneousEntities
                && config.nametags.enabled
                && !config.nametags.hidePlayers && config.nametags.hideMobs && config.nametags.hideArmorStands
                && config.particles.enabled && config.particles.mode == ParticleFilterMode.BLACKLIST
                && config.particles.blockParticles && config.particles.itemParticles && config.particles.smokeParticles
                && config.particles.flameParticles && config.particles.explosionParticles
                && config.particles.spellParticles
                && config.particles.waterParticles && config.particles.redstoneParticles
                && !config.particles.ambientParticles
                && !config.particles.otherParticles && config.blockEntities.enabled && !config.blockEntities.hideChests
                && !config.blockEntities.hideSigns && config.blockEntities.hideBeacons
                && config.blockEntities.hideSpawners
                && config.blockEntities.hideShulkerBoxes && !config.blockEntities.hideBanners
                && config.blockEntities.hideFurnaces && config.blockEntities.hideEnchantingTables
                && config.blockEntities.hideMiscellaneousBlockEntities && config.distanceCulling.enabled
                && config.distanceCulling.entityDistanceLimitEnabled
                && config.distanceCulling.entityRenderDistance == DisableEntityConfig.DEFAULT_ENTITY_RENDER_DISTANCE
                && config.distanceCulling.blockEntityDistanceLimitEnabled
                && config.distanceCulling.blockEntityRenderDistance == DisableEntityConfig.DEFAULT_BLOCK_ENTITY_RENDER_DISTANCE
                && config.blockStates.enabled
                && config.blockStates.freezeRedstone && config.blockStates.freezePistons
                && !config.blockStates.freezeDoors && !config.blockStates.freezeRails
                && config.blockStates.freezeSculk && !config.blockStates.freezeCrafters
                && config.blockStates.freezeObservers && config.blockStates.freezeRepeatersComparators
                && !config.blockStates.freezeBells && !config.blockStates.freezeOtherDynamic;
    }

    private static boolean matchesPerformance(DisableEntityConfig config) {
        return config.globalEnabled && config.disableEntityShadows
                && config.entityRendering.enabled && config.entityRendering.hidePlayers
                && config.entityRendering.hideHostileMobs && config.entityRendering.hidePassiveMobs
                && config.entityRendering.hideItemEntities && config.entityRendering.hideProjectiles
                && config.entityRendering.hideArmorStands && config.entityRendering.hideExperienceOrbs
                && config.entityRendering.hideItemFrames && config.entityRendering.hidePaintings
                && config.entityRendering.hideLeashKnots && config.entityRendering.hideDisplayEntities
                && config.entityRendering.hideVehicles && config.entityRendering.hideMiscellaneousEntities
                && config.nametags.enabled
                && config.nametags.hidePlayers && config.nametags.hideMobs && config.nametags.hideArmorStands
                && config.particles.enabled && config.particles.mode == ParticleFilterMode.ALL
                && config.blockEntities.enabled
                && config.blockEntities.hideChests && config.blockEntities.hideSigns && config.blockEntities.hideBeacons
                && config.blockEntities.hideSpawners && config.blockEntities.hideShulkerBoxes
                && config.blockEntities.hideBanners && config.blockEntities.hideFurnaces
                && config.blockEntities.hideEnchantingTables && config.blockEntities.hideMiscellaneousBlockEntities
                && config.distanceCulling.enabled && config.distanceCulling.entityDistanceLimitEnabled
                && config.distanceCulling.entityRenderDistance == 64
                && config.distanceCulling.blockEntityDistanceLimitEnabled
                && config.distanceCulling.blockEntityRenderDistance == 48
                && config.blockStates.enabled
                && config.blockStates.freezeRedstone && config.blockStates.freezePistons
                && config.blockStates.freezeDoors && config.blockStates.freezeRails
                && config.blockStates.freezeSculk && config.blockStates.freezeCrafters
                && config.blockStates.freezeObservers && config.blockStates.freezeRepeatersComparators
                && config.blockStates.freezeBells && !config.blockStates.freezeOtherDynamic;
    }

    private static boolean matchesAggressive(DisableEntityConfig config) {
        return config.globalEnabled && config.disableEntityShadows
                && config.entityRendering.enabled && config.entityRendering.hidePlayers
                && config.entityRendering.hideHostileMobs && config.entityRendering.hidePassiveMobs
                && config.entityRendering.hideItemEntities && config.entityRendering.hideProjectiles
                && config.entityRendering.hideArmorStands && config.entityRendering.hideExperienceOrbs
                && config.entityRendering.hideItemFrames && config.entityRendering.hidePaintings
                && config.entityRendering.hideLeashKnots && config.entityRendering.hideDisplayEntities
                && config.entityRendering.hideVehicles && config.entityRendering.hideMiscellaneousEntities
                && config.nametags.enabled
                && config.nametags.hidePlayers && config.nametags.hideMobs && config.nametags.hideArmorStands
                && config.particles.enabled && config.particles.mode == ParticleFilterMode.ALL
                && config.blockEntities.enabled
                && config.blockEntities.hideChests && config.blockEntities.hideSigns && config.blockEntities.hideBeacons
                && config.blockEntities.hideSpawners && config.blockEntities.hideShulkerBoxes
                && config.blockEntities.hideBanners && config.blockEntities.hideFurnaces
                && config.blockEntities.hideEnchantingTables && config.blockEntities.hideMiscellaneousBlockEntities
                && config.distanceCulling.enabled && config.distanceCulling.entityDistanceLimitEnabled
                && config.distanceCulling.entityRenderDistance == 32
                && config.distanceCulling.blockEntityDistanceLimitEnabled
                && config.distanceCulling.blockEntityRenderDistance == 32
                && config.blockStates.enabled
                && config.blockStates.freezeRedstone && config.blockStates.freezePistons
                && config.blockStates.freezeDoors && config.blockStates.freezeRails
                && config.blockStates.freezeSculk && config.blockStates.freezeCrafters
                && config.blockStates.freezeObservers && config.blockStates.freezeRepeatersComparators
                && config.blockStates.freezeBells && config.blockStates.freezeOtherDynamic;
    }
}