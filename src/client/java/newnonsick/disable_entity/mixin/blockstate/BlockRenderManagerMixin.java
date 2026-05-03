package newnonsick.disable_entity.mixin.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockStateModel;
import newnonsick.disable_entity.DisableEntity;
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
        try {
            return RenderRules.getRenderableBlockState(state);
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error freezing terrain block state", e);
            return state;
        }
    }

    @ModifyVariable(
        method = "renderDamage",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private BlockState freezeDamageBlockState(BlockState state) {
        try {
            return RenderRules.getRenderableBlockState(state);
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error freezing damage block state", e);
            return state;
        }
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
        try {
            return blockModels.getModel(RenderRules.getRenderableBlockState(state));
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error freezing block state for model lookup", e);
            return blockModels.getModel(state);
        }
    }
}
