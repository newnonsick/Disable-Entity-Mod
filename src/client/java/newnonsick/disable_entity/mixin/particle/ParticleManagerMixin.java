package newnonsick.disable_entity.mixin.particle;

import java.util.Map;
import java.util.Queue;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import newnonsick.disable_entity.util.ParticleCategory;
import newnonsick.disable_entity.util.ParticleVisibilityTracker;
import newnonsick.disable_entity.util.RenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Filters particle spawning at the manager boundary and suppresses full renders
 * when the
 * aggressive mode is active.
 */
@Mixin(value = ParticleManager.class, priority = 1100)
public abstract class ParticleManagerMixin {
    @Shadow
    @Final
    private Map<ParticleTextureSheet, Queue<Particle>> particles;

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void disableParticleSpawning(ParticleEffect parameters, double x, double y, double z, double velocityX,
            double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (RenderRules.shouldHideParticle(parameters)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    private void trackParticleSpawn(ParticleEffect parameters, double x, double y, double z, double velocityX,
            double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        Particle particle = cir.getReturnValue();
        if (particle != null) {
            ParticleVisibilityTracker.register(particle, ParticleCategory.from(parameters));
        }
    }

    @Inject(method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;)V", at = @At("HEAD"), cancellable = true)
    private void disableEmitterSpawning(Entity entity, ParticleEffect parameters, CallbackInfo ci) {
        if (RenderRules.shouldHideParticle(parameters)) {
            ci.cancel();
        }
    }

    @Inject(method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V", at = @At("HEAD"), cancellable = true)
    private void disableEmitterSpawning(Entity entity, ParticleEffect parameters, int maxAge, CallbackInfo ci) {
        if (RenderRules.shouldHideParticle(parameters)) {
            ci.cancel();
        }
    }

    @Inject(method = "addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"), cancellable = true)
    private void disableBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (RenderRules.shouldHideParticleCategory(ParticleCategory.BLOCK)) {
            ci.cancel();
        }
    }

    @Inject(method = "addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V", at = @At("HEAD"), cancellable = true)
    private void disableBlockBreakingParticles(BlockPos pos, Direction direction, CallbackInfo ci) {
        if (RenderRules.shouldHideParticleCategory(ParticleCategory.BLOCK)) {
            ci.cancel();
        }
    }

    @Inject(method = "setWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"))
    private void clearTrackedParticles(ClientWorld world, CallbackInfo ci) {
        ParticleVisibilityTracker.clear();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void cullHiddenParticles(CallbackInfo ci) {
        ParticleVisibilityTracker.purgeHiddenParticles(this.particles.values());
    }

    @Inject(method = "renderParticles(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V", at = @At("HEAD"), cancellable = true)
    private void disableParticleRendering(Camera camera, float tickProgress,
            VertexConsumerProvider.Immediate vertexConsumers, CallbackInfo ci) {
        if (RenderRules.shouldHideAllParticles()) {
            ci.cancel();
        }
    }
}