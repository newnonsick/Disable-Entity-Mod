package newnonsick.disable_entity.client.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.client.util.AdaptiveTuningManager;
import newnonsick.disable_entity.client.util.ClientRenderRefresh;
import newnonsick.disable_entity.client.util.FpsSampler;
import newnonsick.disable_entity.config.DisableEntityConfigManager;
import org.lwjgl.glfw.GLFW;

/**
 * Registers the runtime toggle hotkeys and applies their effects immediately.
 */
public final class DisableEntityKeybindManager {

    private static final String CATEGORY_KEY = "key.categories.disable_entity";

    private static final KeyBinding TOGGLE_ALL = register(
        "key.disable_entity.toggle_all"
    );
    private static final KeyBinding TOGGLE_ENTITIES = register(
        "key.disable_entity.toggle_entities"
    );
    private static final KeyBinding TOGGLE_PARTICLES = register(
        "key.disable_entity.toggle_particles"
    );
    private static final KeyBinding TOGGLE_BLOCK_ENTITIES = register(
        "key.disable_entity.toggle_block_entities"
    );
    private static final KeyBinding TOGGLE_NAMETAGS = register(
        "key.disable_entity.toggle_nametags"
    );
    private static final KeyBinding TOGGLE_BLOCK_STATES = register(
        "key.disable_entity.toggle_block_states"
    );
    private static final KeyBinding TOGGLE_WORLD_RENDERING = register(
        "key.disable_entity.toggle_world_rendering"
    );
    private static final KeyBinding TOGGLE_OVERLAY = register(
        "key.disable_entity.toggle_overlay"
    );
    private static final KeyBinding RESET_DEFAULTS = register(
        "key.disable_entity.reset_defaults"
    );
    private static final KeyBinding COPY_CONFIG = register(
        "key.disable_entity.copy_config"
    );
    private static final KeyBinding PASTE_CONFIG = register(
        "key.disable_entity.paste_config"
    );

    private static boolean registered;

    private static boolean pendingFpsDelta;
    private static long fpsDeltaStartTime;
    private static float fpsDeltaPre;

    private DisableEntityKeybindManager() {}

    public static void register() {
        if (registered) {
            return;
        }

        ClientTickEvents.END_CLIENT_TICK.register(
            DisableEntityKeybindManager::onClientTick
        );
        registered = true;
    }

    private static KeyBinding register(String translationKey) {
        return KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                defaultKey(translationKey),
                CATEGORY_KEY
            )
        );
    }

    private static int defaultKey(String translationKey) {
        return switch (translationKey) {
            case "key.disable_entity.toggle_all" -> GLFW.GLFW_KEY_INSERT;
            case "key.disable_entity.toggle_entities" -> GLFW.GLFW_KEY_HOME;
            case "key.disable_entity.toggle_particles" -> GLFW.GLFW_KEY_END;
            case "key.disable_entity.toggle_block_entities" -> GLFW.GLFW_KEY_PAGE_UP;
            case "key.disable_entity.toggle_nametags" -> GLFW.GLFW_KEY_PAGE_DOWN;
            case "key.disable_entity.toggle_block_states" -> GLFW.GLFW_KEY_F7;
            case "key.disable_entity.toggle_world_rendering" -> GLFW.GLFW_KEY_F8;
            case "key.disable_entity.toggle_overlay" -> GLFW.GLFW_KEY_F6;
            case "key.disable_entity.reset_defaults" -> GLFW.GLFW_KEY_DELETE;
            case "key.disable_entity.copy_config" -> GLFW.GLFW_KEY_PRINT_SCREEN;
            case "key.disable_entity.paste_config" -> GLFW.GLFW_KEY_SCROLL_LOCK;
            default -> GLFW.GLFW_KEY_UNKNOWN;
        };
    }

    private static void onClientTick(MinecraftClient client) {
        if (
            client.world == null ||
            client.player == null ||
            client.currentScreen != null
        ) {
            return;
        }

        if (pendingFpsDelta && System.currentTimeMillis() - fpsDeltaStartTime > 2000L) {
            pendingFpsDelta = false;
            float post = FpsSampler.getInstance().getAverage();
            int delta = Math.round(post - fpsDeltaPre);
            if (DisableEntityConfigManager.getConfig().showFpsDeltaOnToggle) {
                String key = delta >= 0
                    ? "message.disable_entity.fps_delta_gain"
                    : "message.disable_entity.fps_delta_loss";
                announce(client, Text.translatable(key, Math.abs(delta)));
            }
        }

        while (TOGGLE_ALL.wasPressed()) {
            if (AdaptiveTuningManager.getInstance().hasPendingPrompt()) {
                AdaptiveTuningManager.getInstance().acceptPrompt();
                ClientRenderRefresh.refreshAll();
                startFpsDeltaCheck();
                announce(
                    client,
                    Text.translatable("message.disable_entity.adaptive_applied")
                );
                continue;
            }
            boolean enabled = DisableEntityConfigManager.toggleGlobalEnabled();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.global_enabled"
                    : "message.disable_entity.global_disabled"
            );
        }
        while (TOGGLE_ENTITIES.wasPressed()) {
            boolean enabled =
                DisableEntityConfigManager.toggleEntityRendering();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.entities_enabled"
                    : "message.disable_entity.entities_disabled"
            );
        }
        while (TOGGLE_PARTICLES.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.toggleParticles();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.particles_enabled"
                    : "message.disable_entity.particles_disabled"
            );
        }
        while (TOGGLE_BLOCK_ENTITIES.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.toggleBlockEntities();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.block_entities_enabled"
                    : "message.disable_entity.block_entities_disabled"
            );
        }
        while (TOGGLE_NAMETAGS.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.toggleNametags();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.nametags_enabled"
                    : "message.disable_entity.nametags_disabled"
            );
        }
        while (TOGGLE_BLOCK_STATES.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.toggleBlockStates();
            ClientRenderRefresh.refreshBlockStates(client);
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.block_states_enabled"
                    : "message.disable_entity.block_states_disabled"
            );
        }
        while (TOGGLE_WORLD_RENDERING.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.toggleWorldRendering();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.world_rendering_enabled"
                    : "message.disable_entity.world_rendering_disabled"
            );
        }
        while (TOGGLE_OVERLAY.wasPressed()) {
            boolean enabled = DisableEntityConfigManager.togglePerformanceOverlay();
            startFpsDeltaCheck();
            announce(
                client,
                enabled
                    ? "message.disable_entity.overlay_enabled"
                    : "message.disable_entity.overlay_disabled"
            );
        }
        while (RESET_DEFAULTS.wasPressed()) {
            DisableEntityConfigManager.resetToDefaults();
            ClientRenderRefresh.refreshAll();
            startFpsDeltaCheck();
            announce(client, "message.disable_entity.config_reset");
        }
        while (COPY_CONFIG.wasPressed()) {
            copyConfigToClipboard(client);
            announce(client, "message.disable_entity.config_copied");
        }
        while (PASTE_CONFIG.wasPressed()) {
            pasteConfigFromClipboard(client);
        }
    }

    private static void copyConfigToClipboard(MinecraftClient client) {
        long windowHandle = client.getWindow().getHandle();
        GLFW.glfwSetClipboardString(
            windowHandle,
            DisableEntityConfigManager.exportConfigToJson()
        );
    }

    private static void pasteConfigFromClipboard(MinecraftClient client) {
        long windowHandle = client.getWindow().getHandle();
        String clipboardText = GLFW.glfwGetClipboardString(windowHandle);

        if (clipboardText == null || clipboardText.isBlank()) {
            announce(client, "message.disable_entity.config_import_failed");
            return;
        }

        boolean imported = DisableEntityConfigManager.importConfigFromJson(
            clipboardText
        );
        if (imported) {
            ClientRenderRefresh.refreshAll();
        }
        announce(
            client,
            imported
                ? "message.disable_entity.config_imported"
                : "message.disable_entity.config_import_failed"
        );
    }

    private static void startFpsDeltaCheck() {
        if (!DisableEntityConfigManager.getConfig().showFpsDeltaOnToggle) {
            return;
        }
        pendingFpsDelta = true;
        fpsDeltaStartTime = System.currentTimeMillis();
        fpsDeltaPre = FpsSampler.getInstance().getAverage();
    }

    private static void announce(
        MinecraftClient client,
        String translationKey
    ) {
        client.inGameHud.setOverlayMessage(
            Text.translatable(translationKey),
            false
        );
    }

    private static void announce(
        MinecraftClient client,
        Text text
    ) {
        client.inGameHud.setOverlayMessage(text, false);
    }
}
