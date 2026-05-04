package newnonsick.disable_entity.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FpsSamplerTest {

    @Test
    void rollingAverageIsAccurate() {
        FpsSampler sampler = FpsSampler.getInstance();

        for (int i = 0; i < 60; i++) {
            simulateTick(sampler, 60.0f);
        }

        assertEquals(60.0f, sampler.getAverage(), 0.001f);
    }

    @Test
    void rollingAverageWrapsCorrectly() {
        FpsSampler sampler = FpsSampler.getInstance();

        for (int i = 0; i < 60; i++) {
            simulateTick(sampler, 30.0f);
        }
        assertEquals(30.0f, sampler.getAverage(), 0.001f);

        for (int i = 0; i < 60; i++) {
            simulateTick(sampler, 90.0f);
        }
        assertEquals(90.0f, sampler.getAverage(), 0.001f);
    }



    private static void simulateTick(FpsSampler sampler, float fps) {
        sampler.recordSample(fps);
    }
}
