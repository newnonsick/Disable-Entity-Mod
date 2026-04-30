package newnonsick.disable_entity;

import net.fabricmc.api.ModInitializer;
import newnonsick.disable_entity.compat.ModCompatibility;
import newnonsick.disable_entity.config.DisableEntityConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Fabric entrypoint for the client-only optimization mod.
 */
public class DisableEntity implements ModInitializer {
	public static final String MOD_ID = "disable-entity";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DisableEntityConfigManager.load();
		ModCompatibility.logDetectedMods(LOGGER);
		LOGGER.info("Disable Entity initialized.");
	}
}