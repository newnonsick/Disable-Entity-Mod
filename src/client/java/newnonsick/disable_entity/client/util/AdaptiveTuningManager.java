package newnonsick.disable_entity.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.config.DisableEntityConfig;
import newnonsick.disable_entity.config.DisableEntityConfigManager;
import newnonsick.disable_entity.config.OptimizationPreset;

/**
 * Monitors FPS and prompts the user to escalate to a higher optimization
 * preset when frame rate stays below a target threshold.
 */
public final class AdaptiveTuningManager {
    private static final int TICKS_PER_SECOND = 20;
    private static final long PROMPT_DURATION_MS = 10_000L;

    private static final AdaptiveTuningManager INSTANCE = new AdaptiveTuningManager();

    private int tickCounter;
    private int consecutiveLowFpsSeconds;
    private OptimizationPreset pendingPreset;
    private long promptExpiryTime;

    private AdaptiveTuningManager() {
    }

    public static AdaptiveTuningManager getInstance() {
        return INSTANCE;
    }

    public void onClientTick(MinecraftClient client) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        if (!config.globalEnabled || !config.adaptiveTuningEnabled) {
            return;
        }

        if (client == null || client.player == null) {
            return;
        }

        tickCounter++;
        if (tickCounter < TICKS_PER_SECOND) {
            return;
        }
        tickCounter = 0;

        float fps = FpsSampler.getInstance().getAverage();
        int target = config.adaptiveTargetFps;
        int delay = config.adaptiveEscalationDelaySeconds;

        if (fps < target) {
            consecutiveLowFpsSeconds++;
            if (consecutiveLowFpsSeconds >= delay && pendingPreset == null) {
                OptimizationPreset next = suggestNextPreset();
                if (next != null) {
                    pendingPreset = next;
                    promptExpiryTime = System.currentTimeMillis() + PROMPT_DURATION_MS;
                    client.inGameHud.setOverlayMessage(
                        Text.translatable(
                            "message.disable_entity.adaptive_prompt",
                            Text.translatable(presetTranslationKey(next)).getString()
                        ),
                        false
                    );
                }
            }
        } else {
            consecutiveLowFpsSeconds = 0;
        }

        if (pendingPreset != null && System.currentTimeMillis() > promptExpiryTime) {
            pendingPreset = null;
        }
    }

    public boolean hasPendingPrompt() {
        return pendingPreset != null && System.currentTimeMillis() <= promptExpiryTime;
    }

    public OptimizationPreset getPendingPreset() {
        return pendingPreset;
    }

    public void acceptPrompt() {
        if (pendingPreset != null) {
            DisableEntityConfigManager.applyPreset(pendingPreset);
            pendingPreset = null;
            consecutiveLowFpsSeconds = 0;
        }
    }

    public void clearPrompt() {
        pendingPreset = null;
    }

    private static OptimizationPreset suggestNextPreset() {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        OptimizationPreset current = OptimizationPreset.detect(config);
        return switch (current) {
            case CUSTOM, BALANCED -> OptimizationPreset.PERFORMANCE;
            case PERFORMANCE -> OptimizationPreset.AGGRESSIVE;
            case AGGRESSIVE -> null;
        };
    }

    private static String presetTranslationKey(OptimizationPreset preset) {
        return switch (preset) {
            case CUSTOM -> "text.disable_entity.preset.custom";
            case BALANCED -> "text.disable_entity.preset.balanced";
            case PERFORMANCE -> "text.disable_entity.preset.performance";
            case AGGRESSIVE -> "text.disable_entity.preset.aggressive";
        };
    }
}
