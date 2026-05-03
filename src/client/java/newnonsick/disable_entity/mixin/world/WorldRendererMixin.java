package newnonsick.disable_entity.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import newnonsick.disable_entity.DisableEntity;
import newnonsick.disable_entity.util.PerformanceTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses world-rendering work when the corresponding optimizations are
 * enabled.
 */
@Mixin(value = WorldRenderer.class, priority = 1100)
public abstract class WorldRendererMixin {

    @Inject(method = "updateBlock", at = @At("HEAD"), cancellable = true)
    private void skipFrozenBlockStateUpdate(BlockView world, BlockPos pos, BlockState oldState, BlockState newState,
            int flags, CallbackInfo ci) {
        try {
            if (RenderRules.shouldSkipFrozenBlockStateRerender(oldState, newState)) {
                PerformanceTracker.getInstance().recordFrozenBlockState();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in frozen block state update skip", e);
        }
    }

    @Inject(method = "scheduleBlockRerenderIfNeeded", at = @At("HEAD"), cancellable = true)
    private void skipFrozenBlockStateRerender(BlockPos pos, BlockState oldState, BlockState newState,
            CallbackInfo ci) {
        try {
            if (RenderRules.shouldSkipFrozenBlockStateRerender(oldState, newState)) {
                PerformanceTracker.getInstance().recordFrozenBlockState();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in frozen block state rerender skip", e);
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void disableClouds(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("clouds")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in cloud render culling", e);
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void disableWeather(CallbackInfo ci) {
        try {
            if (RenderRules.shouldDisableWorldRender("weather")) {
                PerformanceTracker.getInstance().recordHiddenWorldFeature();
                ci.cancel();
            }
        } catch (Exception e) {
            DisableEntity.LOGGER.error("Error in weather render culling", e);
        }
    }
}
