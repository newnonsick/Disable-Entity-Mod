package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.render.fog.FogRenderer;
import newnonsick.disable_entity.util.RenderRules;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Suppresses fog rendering when the fog optimization is enabled.
 */
@Mixin(value = FogRenderer.class, priority = 1100)
public abstract class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void disableFog(CallbackInfoReturnable<Vector4f> cir) {
        if (RenderRules.shouldDisableWorldRender("fog")) {
            // Returning a zeroed vector might be what's expected for "no fog" color,
            // but we should check if this actually "disables" it.
            // For now, this fixes the crash.
            cir.setReturnValue(new Vector4f(0, 0, 0, 0));
        }
    }
}
