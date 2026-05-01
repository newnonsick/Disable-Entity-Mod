package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockStateModel;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockRenderManager.class, priority = 1100)
public abstract class BlockRenderManagerMixin {

    @ModifyVariable(
        method = "renderBlock",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private BlockState freezeTerrainBlockState(BlockState state) {
        return getFrozenState(state);
    }

    @ModifyVariable(
        method = "renderDamage",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private BlockState freezeDamageBlockState(BlockState state) {
        return getFrozenState(state);
    }

    @Redirect(
        method = "getModel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/BlockModels;getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BlockStateModel;"
        )
    )
    private BlockStateModel freezeBlockStateForModelLookup(
        BlockModels blockModels,
        BlockState state
    ) {
        return blockModels.getModel(RenderRules.getRenderableBlockState(state));
    }

    private static BlockState getFrozenState(BlockState state) {
        return RenderRules.getRenderableBlockState(state);
    }
}
