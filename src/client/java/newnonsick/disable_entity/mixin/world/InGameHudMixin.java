package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.gui.hud.InGameHud;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses vignette rendering and screen overlays when the
 * corresponding world rendering optimizations are enabled.
 */
@Mixin(value = InGameHud.class, priority = 1100)
public abstract class InGameHudMixin {

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void disableVignette(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("vignette")) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void disablePortalOverlay(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("overlays")) {
            ci.cancel();
        }
    }
    
    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void disableSpyglassOverlay(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("overlays")) {
            ci.cancel();
        }
    }
}
