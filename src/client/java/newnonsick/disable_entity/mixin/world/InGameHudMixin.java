package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import newnonsick.disable_entity.config.DisableEntityConfig;
import newnonsick.disable_entity.config.DisableEntityConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses vignette rendering, screen overlays, and renders the
 * performance overlay when enabled.
 */
@Mixin(value = InGameHud.class, priority = 1100)
public abstract class InGameHudMixin {

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void disableVignette(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("vignette")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in vignette culling", e);
        }
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void disablePortalOverlay(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("overlays")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in portal overlay culling", e);
        }
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void disableSpyglassOverlay(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("overlays")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in spyglass overlay culling", e);
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private long disableEntity$lastUpdateTime = 0;

    @org.spongepowered.asm.mixin.Unique
    private int[] disableEntity$values = new int[7];

    @org.spongepowered.asm.mixin.Unique
    private int disableEntity$total = 0;

    @org.spongepowered.asm.mixin.Unique
    private int disableEntity$maxValueWidth = 30; // Minimum width for values

    @Inject(method = "render", at = @At("TAIL"))
    private void renderPerformanceOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        try {
            DisableEntityConfig config = DisableEntityConfigManager.getConfig();
            if (!config.globalEnabled || !config.showPerformanceOverlay) {
                return;
            }

            PerformanceTracker tracker = PerformanceTracker.getInstance();
            long currentTime = System.currentTimeMillis();
            
            // Throttle updates to every 250ms (4 FPS) to prevent unreadable flickering
            if (currentTime - this.disableEntity$lastUpdateTime > 250) {
                this.disableEntity$total = tracker.getTotalHidden();
                this.disableEntity$values[0] = tracker.getLastHiddenEntities();
                this.disableEntity$values[1] = tracker.getLastHiddenBlockEntities();
                this.disableEntity$values[2] = tracker.getLastHiddenParticles();
                this.disableEntity$values[3] = tracker.getLastHiddenNametags();
                this.disableEntity$values[4] = tracker.getLastHiddenShadows();
                this.disableEntity$values[5] = tracker.getLastHiddenWorldFeatures();
                this.disableEntity$values[6] = tracker.getLastFrozenBlockStates();
                this.disableEntity$lastUpdateTime = currentTime;
            }

            // Hide entirely if there is absolutely no activity
            if (this.disableEntity$total == 0 && tracker.getTotalHidden() == 0) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;
            int x = 6;
            int y = 6;
            int lineHeight = textRenderer.fontHeight + 3;

            // Always render all lines so the UI height never jumps
            String[] lines = new String[] {
                "Total Hidden",
                "Entities",
                "Block Entities",
                "Particles",
                "Nametags",
                "Shadows",
                "World Effects",
                "Frozen States"
            };

            int[] values = new int[] {
                this.disableEntity$total,
                this.disableEntity$values[0],
                this.disableEntity$values[1],
                this.disableEntity$values[2],
                this.disableEntity$values[3],
                this.disableEntity$values[4],
                this.disableEntity$values[5],
                this.disableEntity$values[6]
            };

            int lineCount = lines.length;
            int maxLabelWidth = textRenderer.getWidth("Disable Entity");

            for (int i = 0; i < lineCount; i++) {
                maxLabelWidth = Math.max(maxLabelWidth, textRenderer.getWidth(lines[i]));
                int valWidth = textRenderer.getWidth(String.valueOf(values[i]));
                // Keep a running max width so the panel only grows, never shrinks (0 jitter)
                this.disableEntity$maxValueWidth = Math.max(this.disableEntity$maxValueWidth, valWidth);
            }

            int panelPadding = 6;
            int textGap = 16;
            int panelWidth = maxLabelWidth + textGap + this.disableEntity$maxValueWidth + panelPadding * 2;
            int panelHeight = (lineCount + 1) * lineHeight + panelPadding * 2 + 4;

            int panelX = x;
            int panelY = y;

            context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xB0121212);
            context.fill(panelX, panelY, panelX + 2, panelY + panelHeight, 0xFF00E676);

            int currentY = panelY + panelPadding;
            context.drawText(textRenderer, "Disable Entity", panelX + panelPadding + 3, currentY, 0xFFFFFFFF, true);
            currentY += lineHeight;

            context.fill(panelX + panelPadding + 3, currentY - 1, panelX + panelWidth - panelPadding, currentY, 0x40FFFFFF);
            currentY += 4;

            for (int i = 0; i < lineCount; i++) {
                int labelColor = (i == 0) ? 0xFF00E676 : 0xFFAAAAAA;
                // Dim the value if it's 0 to make active metrics stand out
                int valueColor = (i == 0) ? 0xFFFFFFFF : (values[i] == 0 ? 0xFF666666 : 0xFFDDDDDD);
                
                context.drawText(textRenderer, lines[i], panelX + panelPadding + 3, currentY, labelColor, true);
                
                String valStr = String.valueOf(values[i]);
                int valWidth = textRenderer.getWidth(valStr);
                int valX = panelX + panelWidth - panelPadding - valWidth;
                context.drawText(textRenderer, valStr, valX, currentY, valueColor, true);
                
                currentY += lineHeight;
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error rendering performance overlay", e);
        }
    }
}
