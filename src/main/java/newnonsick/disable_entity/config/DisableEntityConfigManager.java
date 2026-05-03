package newnonsick.disable_entity.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import newnonsick.disable_entity.DisableEntity;

/**
 * Singleton loader and saver for the mod configuration file.
 * Uses ReentrantReadWriteLock for concurrent read access with exclusive writes.
 */
public final class DisableEntityConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("disable-entity.json");
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final int MAX_IMPORT_SIZE = 65536;

    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = LOCK.writeLock();

    private static DisableEntityConfig config = new DisableEntityConfig();
    private static boolean loaded;

    private DisableEntityConfigManager() {
    }

    public static void load() {
        WRITE_LOCK.lock();
        try {
            if (loaded) {
                return;
            }

            config = readConfig();
            config.sanitize();
            loaded = true;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static DisableEntityConfig getConfig() {
        READ_LOCK.lock();
        try {
            if (loaded) {
                return config;
            }
        } finally {
            READ_LOCK.unlock();
        }

        WRITE_LOCK.lock();
        try {
            if (!loaded) {
                config = readConfig();
                config.sanitize();
                loaded = true;
            }
            return config;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void save() {
        WRITE_LOCK.lock();
        try {
            saveInternal(true, null);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void saveWithActivePreset(OptimizationPreset activePreset) {
        WRITE_LOCK.lock();
        try {
            saveInternal(false, activePreset);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    private static void saveInternal(boolean detectPreset, OptimizationPreset activePreset) {
        ensureLoaded();
        config.sanitize();
        if (activePreset != null) {
            config.activePreset = activePreset;
        } else if (detectPreset) {
            config.activePreset = OptimizationPreset.detect(config);
        }
        writeConfig(config);
    }

    public static void resetToDefaults() {
        WRITE_LOCK.lock();
        try {
            config = new DisableEntityConfig();
            saveInternal(true, null);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static String exportConfigToJson() {
        READ_LOCK.lock();
        try {
            ensureLoadedForRead();
            config.sanitize();
            return GSON.toJson(config);
        } finally {
            READ_LOCK.unlock();
        }
    }

    public static boolean importConfigFromJson(String json) {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();

            if (json == null || json.length() > MAX_IMPORT_SIZE) {
                DisableEntity.LOGGER.warn("Config import rejected: input is null or exceeds maximum size of {} bytes.",
                        MAX_IMPORT_SIZE);
                return false;
            }

            DisableEntityConfig importedConfig = GSON.fromJson(json, DisableEntityConfig.class);
            if (importedConfig == null) {
                return false;
            }

            importedConfig.sanitize();
            config = importedConfig;
            saveInternal(true, null);
            return true;
        } catch (JsonParseException exception) {
            DisableEntity.LOGGER.warn("Failed to import config JSON; keeping the current settings.", exception);
            return false;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void applyPreset(OptimizationPreset preset) {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            if (preset == null) {
                preset = OptimizationPreset.CUSTOM;
            }

            preset.apply(config);
            config.activePreset = preset;
            saveInternal(false, preset);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleGlobalEnabled() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.globalEnabled = !config.globalEnabled;
            saveInternal(true, null);
            return config.globalEnabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleEntityRendering() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.entityRendering.enabled = !config.entityRendering.enabled;
            saveInternal(true, null);
            return config.entityRendering.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleParticles() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.particles.enabled = !config.particles.enabled;
            saveInternal(true, null);
            return config.particles.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleBlockEntities() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.blockEntities.enabled = !config.blockEntities.enabled;
            saveInternal(true, null);
            return config.blockEntities.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleNametags() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.nametags.enabled = !config.nametags.enabled;
            saveInternal(true, null);
            return config.nametags.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleBlockStates() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.blockStates.enabled = !config.blockStates.enabled;
            saveInternal(true, null);
            return config.blockStates.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean toggleWorldRendering() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.worldRendering.enabled = !config.worldRendering.enabled;
            saveInternal(true, null);
            return config.worldRendering.enabled;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean togglePerformanceOverlay() {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            config.showPerformanceOverlay = !config.showPerformanceOverlay;
            saveInternal(true, null);
            return config.showPerformanceOverlay;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    private static void ensureLoaded() {
        if (!loaded) {
            config = readConfig();
            config.sanitize();
            loaded = true;
        }
    }

    private static void ensureLoadedForRead() {
        if (!loaded) {
            READ_LOCK.unlock();
            WRITE_LOCK.lock();
            try {
                if (!loaded) {
                    config = readConfig();
                    config.sanitize();
                    loaded = true;
                }
                READ_LOCK.lock();
            } finally {
                WRITE_LOCK.unlock();
            }
        }
    }

    private static DisableEntityConfig readConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            DisableEntityConfig defaultConfig = new DisableEntityConfig();
            writeConfig(defaultConfig);
            return defaultConfig;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            DisableEntityConfig parsed = GSON.fromJson(reader, DisableEntityConfig.class);
            return parsed == null ? new DisableEntityConfig() : parsed;
        } catch (JsonParseException | IOException exception) {
            backupCorruptConfig();
            DisableEntity.LOGGER.error("Failed to read config file {}, using defaults.", CONFIG_PATH, exception);
            return new DisableEntityConfig();
        }
    }

    private static void backupCorruptConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                Path backupPath = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".broken");
                Files.copy(CONFIG_PATH, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            DisableEntity.LOGGER.warn("Failed to back up a corrupt config file at {}.", CONFIG_PATH, exception);
        }
    }

    private static void writeConfig(DisableEntityConfig currentConfig) {
        try {
            Path parent = CONFIG_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path temporaryPath = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + TEMP_FILE_SUFFIX);
            try (Writer writer = Files.newBufferedWriter(temporaryPath, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                GSON.toJson(currentConfig, writer);
            }

            try {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException moveException) {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            DisableEntity.LOGGER.error("Failed to write config file {}.", CONFIG_PATH, exception);
        }
    }
}