package newnonsick.disable_entity.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class PerformanceTracker {
    private static final PerformanceTracker INSTANCE = new PerformanceTracker();

    private final AtomicInteger hiddenEntities = new AtomicInteger();
    private final AtomicInteger hiddenBlockEntities = new AtomicInteger();
    private final AtomicInteger hiddenParticles = new AtomicInteger();
    private final AtomicInteger hiddenNametags = new AtomicInteger();
    private final AtomicInteger hiddenShadows = new AtomicInteger();
    private final AtomicInteger hiddenWorldFeatures = new AtomicInteger();
    private final AtomicInteger frozenBlockStates = new AtomicInteger();

    private volatile int lastHiddenEntities;
    private volatile int lastHiddenBlockEntities;
    private volatile int lastHiddenParticles;
    private volatile int lastHiddenNametags;
    private volatile int lastHiddenShadows;
    private volatile int lastHiddenWorldFeatures;
    private volatile int lastFrozenBlockStates;

    private PerformanceTracker() {
    }

    public static PerformanceTracker getInstance() {
        return INSTANCE;
    }

    public void recordHiddenEntity() {
        hiddenEntities.incrementAndGet();
    }

    public void recordHiddenBlockEntity() {
        hiddenBlockEntities.incrementAndGet();
    }

    public void recordHiddenParticle() {
        hiddenParticles.incrementAndGet();
    }

    public void recordHiddenNametag() {
        hiddenNametags.incrementAndGet();
    }

    public void recordHiddenShadow() {
        hiddenShadows.incrementAndGet();
    }

    public void recordHiddenWorldFeature() {
        hiddenWorldFeatures.incrementAndGet();
    }

    public void recordFrozenBlockState() {
        frozenBlockStates.incrementAndGet();
    }

    public void onFrameEnd() {
        lastHiddenEntities = hiddenEntities.getAndSet(0);
        lastHiddenBlockEntities = hiddenBlockEntities.getAndSet(0);
        lastHiddenParticles = hiddenParticles.getAndSet(0);
        lastHiddenNametags = hiddenNametags.getAndSet(0);
        lastHiddenShadows = hiddenShadows.getAndSet(0);
        lastHiddenWorldFeatures = hiddenWorldFeatures.getAndSet(0);
        lastFrozenBlockStates = frozenBlockStates.getAndSet(0);
    }

    public int getLastHiddenEntities() {
        return lastHiddenEntities;
    }

    public int getLastHiddenBlockEntities() {
        return lastHiddenBlockEntities;
    }

    public int getLastHiddenParticles() {
        return lastHiddenParticles;
    }

    public int getLastHiddenNametags() {
        return lastHiddenNametags;
    }

    public int getLastHiddenShadows() {
        return lastHiddenShadows;
    }

    public int getLastHiddenWorldFeatures() {
        return lastHiddenWorldFeatures;
    }

    public int getLastFrozenBlockStates() {
        return lastFrozenBlockStates;
    }

    public int getTotalHidden() {
        return lastHiddenEntities + lastHiddenBlockEntities + lastHiddenParticles
                + lastHiddenNametags + lastHiddenShadows + lastHiddenWorldFeatures
                + lastFrozenBlockStates;
    }
}
