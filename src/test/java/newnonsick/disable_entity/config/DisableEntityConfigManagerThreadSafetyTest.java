package newnonsick.disable_entity.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class DisableEntityConfigManagerThreadSafetyTest {

    @Test
    void configReferenceIsVolatile() throws NoSuchFieldException {
        Field configField = DisableEntityConfigManager.class.getDeclaredField("config");
        assertTrue(
            Modifier.isVolatile(configField.getModifiers()),
            "Config reference must be volatile for lock-free fast-path reads"
        );
    }

    @Test
    void loadedFlagIsVolatile() throws NoSuchFieldException {
        Field loadedField = DisableEntityConfigManager.class.getDeclaredField("loaded");
        assertTrue(
            Modifier.isVolatile(loadedField.getModifiers()),
            "Loaded flag must be volatile for correct double-checked locking behavior"
        );
    }
}
