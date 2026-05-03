package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.util.math.BlockPos;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SectionBuilder.class, priority = 1100)
public abstract class SectionBuilderMixin {

    @Redirect(
        method = "build",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/chunk/ChunkRendererRegion;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
        )
    )
    private BlockState freezeChunkBuildBlockState(ChunkRendererRegion region, BlockPos pos) {
        try {
            return RenderRules.getRenderableBlockState(region.getBlockState(pos));
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error freezing chunk build block state", e);
            return region.getBlockState(pos);
        }
    }
}
