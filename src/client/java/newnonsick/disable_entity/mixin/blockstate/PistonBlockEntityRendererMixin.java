package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import newnonsick.disable_entity.util.DynamicBlockFamily;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PistonBlockEntityRenderer.class, priority = 1100)
public abstract class PistonBlockEntityRendererMixin {

    @Shadow
    private void renderModel(
        BlockPos pos,
        BlockState state,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        World world,
        boolean cull,
        int light
    ) {
        throw new AssertionError();
    }

    @Redirect(
        method = "render(Lnet/minecraft/block/entity/PistonBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/entity/PistonBlockEntityRenderer;renderModel(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;ZI)V"
        )
    )
    private void freezeMovingPistonModel(
        PistonBlockEntityRenderer renderer,
        BlockPos pos,
        BlockState state,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        World world,
        boolean cull,
        int light
    ) {
        if (RenderRules.shouldFreezeDynamicBlockFamily(DynamicBlockFamily.PISTON)) {
            BlockState renderableState = RenderRules.getRenderableBlockState(state);
            if (renderableState.isAir()) {
                return;
            }

            this.renderModel(pos, renderableState, matrices, vertexConsumers, world, cull, light);
            return;
        }

        this.renderModel(pos, state, matrices, vertexConsumers, world, cull, light);
    }
}
