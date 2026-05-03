package newnonsick.disable_entity.mixin.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels expensive block entity rendering before the renderer instance is
 * queried.
 */
@Mixin(value = BlockEntityRenderDispatcher.class, priority = 1100)
public abstract class BlockEntityRenderDispatcherMixin {
    @Shadow
    @Final
    public Camera camera;

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void disableBlockEntityRendering(E blockEntity, float tickProgress,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        try {
            double offsetX = blockEntity.getPos().getX() + 0.5D - this.camera.getPos().x;
            double offsetY = blockEntity.getPos().getY() + 0.5D - this.camera.getPos().y;
            double offsetZ = blockEntity.getPos().getZ() + 0.5D - this.camera.getPos().z;

            if (RenderRules.shouldHideBlockEntity(blockEntity,
                    offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ)) {
                PerformanceTracker.getInstance().recordHiddenBlockEntity();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in block entity render culling", e);
        }
    }
}