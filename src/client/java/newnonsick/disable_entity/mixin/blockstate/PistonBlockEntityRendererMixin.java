package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.DynamicBlockFamily;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PistonBlockEntityRenderer.class, priority = 1100)
public abstract class PistonBlockEntityRendererMixin {

    @Shadow
    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
        throw new AssertionError();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/PistonBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    private void freezePistonAnimation(PistonBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, net.minecraft.util.math.Vec3d vec, CallbackInfo ci) {
        try {
            if (RenderRules.shouldFreezeDynamicBlockFamily(DynamicBlockFamily.PISTON)) {
                BlockState pushedBlock = entity.getPushedBlock();
                BlockState renderableState = RenderRules.getRenderableBlockState(pushedBlock);
                if (!renderableState.isAir()) {
                    this.renderModel(entity.getPos(), renderableState, matrices, vertexConsumers, entity.getWorld(), false, overlay);
                }
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in piston block state freezing", e);
        }
    }
}
