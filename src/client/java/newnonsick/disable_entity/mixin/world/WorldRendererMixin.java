package newnonsick.disable_entity.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
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
        if (RenderRules.shouldSkipFrozenBlockStateRerender(oldState, newState)) {
            ci.cancel();
        }
    }

    @Inject(method = "scheduleBlockRerenderIfNeeded", at = @At("HEAD"), cancellable = true)
    private void skipFrozenBlockStateRerender(BlockPos pos, BlockState oldState, BlockState newState,
            CallbackInfo ci) {
        if (RenderRules.shouldSkipFrozenBlockStateRerender(oldState, newState)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void disableClouds(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("clouds")) {
            ci.cancel();
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void disableWeather(CallbackInfo ci) {
        if (RenderRules.shouldDisableWorldRender("weather")) {
            ci.cancel();
        }
    }
}
