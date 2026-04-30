package newnonsick.disable_entity.compat;

import newnonsick.disable_entity.config.DisableEntityConfig;

/**
 * Compatibility-aware feature decisions that prefer specialized optimizer mods
 * when they overlap.
 */
public final class CompatibilityDecisions {
    private CompatibilityDecisions() {
    }

    public static boolean shouldUseEntityDistanceCulling(DisableEntityConfig config) {
        return shouldUseEntityDistanceCulling(config, ModCompatibility.ENTITY_CULLING_LOADED,
                ModCompatibility.MORE_CULLING_LOADED);
    }

    public static boolean shouldUseEntityDistanceCulling(DisableEntityConfig config, boolean entityCullingLoaded,
            boolean moreCullingLoaded) {
        return config.globalEnabled && config.distanceCulling.enabled
                && config.distanceCulling.entityDistanceLimitEnabled
                && !isExternalCullingEnabled(entityCullingLoaded, moreCullingLoaded);
    }

    public static boolean shouldUseBlockEntityDistanceCulling(DisableEntityConfig config) {
        return shouldUseBlockEntityDistanceCulling(config, ModCompatibility.ENTITY_CULLING_LOADED,
                ModCompatibility.MORE_CULLING_LOADED);
    }

    public static boolean shouldUseBlockEntityDistanceCulling(DisableEntityConfig config,
            boolean entityCullingLoaded, boolean moreCullingLoaded) {
        return config.globalEnabled && config.distanceCulling.enabled
                && config.distanceCulling.blockEntityDistanceLimitEnabled
                && !isExternalCullingEnabled(entityCullingLoaded, moreCullingLoaded);
    }

    public static boolean shouldUseBlockStateFreezing(DisableEntityConfig config) {
        return config.globalEnabled && config.blockStates.enabled;
    }

    private static boolean isExternalCullingEnabled(boolean entityCullingLoaded, boolean moreCullingLoaded) {
        return entityCullingLoaded || moreCullingLoaded;
    }
}