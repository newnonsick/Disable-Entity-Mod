package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.render.GameRenderer;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses view bobbing and resets performance counters each frame.
 */
@Mixin(value = GameRenderer.class, priority = 1100)
public abstract class GameRendererMixin {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void disableHandBob(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("hand_bob")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in hand bob culling", e);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onFrameStart(CallbackInfo ci) {
        try {
            PerformanceTracker.getInstance().onFrameEnd();
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in frame start tracking", e);
        }
    }
}
