package newnonsick.disable_entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import newnonsick.disable_entity.client.keybind.DisableEntityKeybindManager;
import newnonsick.disable_entity.client.util.AdaptiveTuningManager;
import newnonsick.disable_entity.client.util.FpsSampler;
import newnonsick.disable_entity.config.DisableEntityConfigManager;

/**
 * Client bootstrap for keybinds, server profile switching, and other
 * client-only wiring.
 */
public final class DisableEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisableEntityConfigManager.load();
        DisableEntityKeybindManager.register();
        registerServerProfileHooks();
        ClientTickEvents.END_CLIENT_TICK.register(client -> FpsSampler.getInstance().onClientTick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> AdaptiveTuningManager.getInstance().onClientTick());
        DisableEntity.LOGGER.info("Client optimization hooks registered.");
    }

    private static void registerServerProfileHooks() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.getCurrentServerEntry() != null) {
                String address = mc.getCurrentServerEntry().address;
                DisableEntityConfigManager.activateServerProfile(address);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            DisableEntityConfigManager.deactivateServerProfile();
        });
    }
}