package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import newnonsick.disable_entity.util.DynamicBlockRegistry;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockRenderManager.class, priority = 1100)
public abstract class BlockRenderManagerMixin {

    @Redirect(method = "getModel",
             at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/render/block/BlockModels;getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BlockStateModel;"))
    private net.minecraft.client.render.model.BlockStateModel freezeBlockStateForModelLookup(BlockModels blockModels, BlockState state) {
        if (!RenderRules.shouldFreezeBlockState(state)) {
            return blockModels.getModel(state);
        }

        BlockState frozen = DynamicBlockRegistry.getInstance().getFrozenState(state.getBlock());
        return blockModels.getModel(frozen != null ? frozen : state);
    }
}
