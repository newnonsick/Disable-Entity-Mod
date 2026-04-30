package newnonsick.disable_entity.mixin.shadow;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.WorldView;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips shadow drawing at the dispatcher level.
 */
@Mixin(value = EntityRenderDispatcher.class, priority = 1100)
public abstract class EntityRenderDispatcherShadowMixin {
    @Inject(method = "renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/entity/state/EntityRenderState;FLnet/minecraft/world/WorldView;F)V", at = @At("HEAD"), cancellable = true)
    private static void disableEntityShadows(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            EntityRenderState renderState, float opacity, WorldView world, float radius, CallbackInfo ci) {
        if (RenderRules.shouldHideEntityShadow()) {
            ci.cancel();
        }
    }
}