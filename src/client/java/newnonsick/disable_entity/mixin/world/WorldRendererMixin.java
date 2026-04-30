package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.render.WorldRenderer;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses cloud and weather rendering when the corresponding world
 * rendering optimizations are enabled.
 */
@Mixin(value = WorldRenderer.class, priority = 1100)
public abstract class WorldRendererMixin {

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void disableClouds(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("clouds")) {
            ci.cancel();
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void disableWeather(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("weather")) {
            ci.cancel();
        }
    }
}
