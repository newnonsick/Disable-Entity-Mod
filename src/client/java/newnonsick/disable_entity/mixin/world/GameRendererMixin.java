package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.render.GameRenderer;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses vignette rendering and hand-bob intensity when the
 * corresponding world rendering optimizations are enabled.
 */
@Mixin(value = GameRenderer.class, priority = 1100)
public abstract class GameRendererMixin {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void disableHandBob(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("hand_bob")) {
            ci.cancel();
        }
    }
}
