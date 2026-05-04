package newnonsick.disable_entity.client.util;

import net.minecraft.client.MinecraftClient;

public final class FpsSampler {
    private static final FpsSampler INSTANCE = new FpsSampler();
    private static final int SAMPLE_COUNT = 60;

    private final float[] samples = new float[SAMPLE_COUNT];
    private int index;
    private int count;

    private FpsSampler() {
    }

    public static FpsSampler getInstance() {
        return INSTANCE;
    }

    public void onClientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        samples[index] = client.getCurrentFps();
        index = (index + 1) % SAMPLE_COUNT;
        if (count < SAMPLE_COUNT) {
            count++;
        }
    }

    public float getAverage() {
        if (count == 0) {
            return 0.0f;
        }
        float sum = 0.0f;
        for (int i = 0; i < count; i++) {
            sum += samples[i];
        }
        return sum / count;
    }
}
