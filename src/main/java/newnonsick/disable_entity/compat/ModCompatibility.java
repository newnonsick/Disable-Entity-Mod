package newnonsick.disable_entity.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

/**
 * Detects optimization mod presence without ever touching their internals.
 */
public final class ModCompatibility {
    public static final boolean SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");
    public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");
    public static final boolean ENTITY_CULLING_LOADED = FabricLoader.getInstance().isModLoaded("entityculling");
    public static final boolean LITHIUM_LOADED = FabricLoader.getInstance().isModLoaded("lithium");
    public static final boolean MORE_CULLING_LOADED = FabricLoader.getInstance().isModLoaded("moreculling");
    public static final boolean IMMEDIATELY_FAST_LOADED = FabricLoader.getInstance().isModLoaded("immediatelyfast");
    public static final boolean ENHANCED_BLOCK_ENTITIES_LOADED = FabricLoader.getInstance()
            .isModLoaded("enhancedblockentities");
    public static final boolean DYNAMIC_FPS_LOADED = FabricLoader.getInstance().isModLoaded("dynamicfps");
    public static final boolean INDIUM_LOADED = FabricLoader.getInstance().isModLoaded("indium");
    public static final boolean CONTINUITY_LOADED = FabricLoader.getInstance().isModLoaded("continuity");

    private ModCompatibility() {
    }

    public static void logDetectedMods(Logger logger) {
        if (SODIUM_LOADED) {
            logger.info("Detected Sodium; entity and block-entity hooks remain vanilla-dispatcher based.");
        }
        if (IRIS_LOADED) {
            logger.info("Detected Iris; this mod avoids framebuffer and renderer replacement hooks.");
        }
        if (ENTITY_CULLING_LOADED) {
            logger.info("Detected EntityCulling; render cancellation remains dispatch-level and non-invasive.");
        }
        if (LITHIUM_LOADED) {
            logger.info("Detected Lithium; no server-side behavior is modified.");
        }
        if (MORE_CULLING_LOADED) {
            logger.info("Detected MoreCulling; this mod stays on vanilla dispatcher hooks.");
        }
        if (IMMEDIATELY_FAST_LOADED) {
            logger.info("Detected ImmediatelyFast; this mod avoids framebuffer and buffer-system hooks.");
        }
        if (ENHANCED_BLOCK_ENTITIES_LOADED) {
            logger.info("Detected Enhanced Block Entities; block-entity filtering remains non-invasive.");
        }
        if (DYNAMIC_FPS_LOADED) {
            logger.info("Detected Dynamic FPS; this mod does not alter focus or tick-rate behavior.");
        }
        if (INDIUM_LOADED) {
            logger.info("Detected Indium; block-state freezing uses dispatcher-level hooks only.");
        }
        if (CONTINUITY_LOADED) {
            logger.info("Detected Continuity; connected textures remain active on frozen block states.");
        }
    }
}