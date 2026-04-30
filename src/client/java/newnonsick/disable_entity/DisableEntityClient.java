package newnonsick.disable_entity;

import net.fabricmc.api.ClientModInitializer;
import newnonsick.disable_entity.client.keybind.DisableEntityKeybindManager;
import newnonsick.disable_entity.config.DisableEntityConfigManager;

/**
 * Client bootstrap for keybinds and other client-only wiring.
 */
public final class DisableEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisableEntityConfigManager.load();
        DisableEntityKeybindManager.register();
        DisableEntity.LOGGER.info("Client optimization hooks registered.");
    }
}