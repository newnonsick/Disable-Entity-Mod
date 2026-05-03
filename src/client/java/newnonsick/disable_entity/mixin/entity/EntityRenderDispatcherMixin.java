package newnonsick.disable_entity.mixin.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cancels entity rendering before the dispatcher reaches renderer-specific
 * work.
 */
@Mixin(value = EntityRenderDispatcher.class, priority = 1100)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Frustum;DDD)Z", at = @At("HEAD"), cancellable = true)
    private void disableEntityRendering(Entity entity, Frustum frustum, double x, double y, double z,
            CallbackInfoReturnable<Boolean> cir) {
        try {
            double squaredDistance = entity.squaredDistanceTo(x, y, z);
            if (RenderRules.shouldHideEntity(entity, squaredDistance)) {
                PerformanceTracker.getInstance().recordHiddenEntity();
                cir.setReturnValue(false);
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in entity render culling", e);
        }
    }
}