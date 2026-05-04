package newnonsick.disable_entity.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

import net.minecraft.client.particle.Particle;
import newnonsick.disable_entity.DisableEntity;

/**
 * Tracks spawned particles by coarse category so hidden categories can be
 * culled on the next tick.
 */
public final class ParticleVisibilityTracker {
    private static final Map<Particle, ParticleCategory> CATEGORIES =
            Collections.synchronizedMap(new WeakHashMap<>());

    private ParticleVisibilityTracker() {
    }

    public static void register(Particle particle, ParticleCategory category) {
        CATEGORIES.put(particle, category);
    }

    public static void clear() {
        CATEGORIES.clear();
    }

    public static boolean shouldCull(Particle particle) {
        if (particle == null) {
            return false;
        }
        ParticleCategory category = CATEGORIES.get(particle);
        if (category == null) {
            return false;
        }
        return RenderRules.shouldHideParticleCategory(category);
    }

    public static void purgeHiddenParticles(Collection<? extends Queue<Particle>> queues) {
        for (Queue<Particle> queue : queues) {
            Particle[] snapshot = queue.toArray(new Particle[0]);
            for (Particle particle : snapshot) {
                try {
                    if (shouldCull(particle)) {
                        particle.markDead();
                        PerformanceTracker.getInstance().recordHiddenParticle();
                    }
                } catch (Exception e) {
                    DisableEntity.LOGGER.error("Error culling particle", e);
                }
            }
        }
    }
}