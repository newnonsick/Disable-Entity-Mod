package newnonsick.disable_entity.mixin.world;

import net.minecraft.client.render.fog.FogRenderer;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Suppresses fog rendering when the fog optimization is enabled.
 * Returns a far-plane vector that effectively removes all distance fog.
 */
@Mixin(value = FogRenderer.class, priority = 1100)
public abstract class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void disableFog(CallbackInfoReturnable<Vector4f> cir) {
        try {
            if (RenderRules.shouldDisableWorldRender("fog")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                cir.setReturnValue(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in fog render culling", e);
        }
    }
}
