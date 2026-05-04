package newnonsick.disable_entity.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class PerformanceTrackerThreadSafetyTest {

    @Test
    void concurrentRecordCallsAreCorrect() throws InterruptedException {
        PerformanceTracker tracker = PerformanceTracker.getInstance();
        tracker.onFrameEnd();

        int threads = 16;
        int iterations = 5_000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        tracker.recordHiddenEntity();
                        tracker.recordHiddenBlockEntity();
                        tracker.recordHiddenParticle();
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads did not finish in time");
        assertEquals(0, errors.get(), "Exceptions occurred during concurrent access");

        tracker.onFrameEnd();

        assertEquals(
            threads * iterations,
            tracker.getLastHiddenEntities(),
            "Entity count should match total increments"
        );
        assertEquals(
            threads * iterations,
            tracker.getLastHiddenBlockEntities(),
            "Block entity count should match total increments"
        );
        assertEquals(
            threads * iterations,
            tracker.getLastHiddenParticles(),
            "Particle count should match total increments"
        );

        executor.shutdown();
    }
}
