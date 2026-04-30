package newnonsick.disable_entity.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import newnonsick.disable_entity.DisableEntity;

/**
 * Singleton loader and saver for the mod configuration file.
 */
public final class DisableEntityConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("disable-entity.json");
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    private static DisableEntityConfig config = new DisableEntityConfig();
    private static boolean loaded;

    private DisableEntityConfigManager() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }

        config = readConfig();
        config.sanitize();
        loaded = true;
    }

    public static synchronized DisableEntityConfig getConfig() {
        load();
        return config;
    }

    public static synchronized void save() {
        saveInternal(true, null);
    }

    public static synchronized void saveWithActivePreset(OptimizationPreset activePreset) {
        saveInternal(false, activePreset);
    }

    private static void saveInternal(boolean detectPreset, OptimizationPreset activePreset) {
        load();
        config.sanitize();
        if (activePreset != null) {
            config.activePreset = activePreset;
        } else if (detectPreset) {
            config.activePreset = OptimizationPreset.detect(config);
        }
        writeConfig(config);
    }

    public static synchronized void resetToDefaults() {
        config = new DisableEntityConfig();
        save();
    }

    public static synchronized String exportConfigToJson() {
        load();
        config.sanitize();
        return GSON.toJson(config);
    }

    public static synchronized boolean importConfigFromJson(String json) {
        load();

        try {
            DisableEntityConfig importedConfig = GSON.fromJson(json, DisableEntityConfig.class);
            if (importedConfig == null) {
                return false;
            }

            importedConfig.sanitize();
            config = importedConfig;
            save();
            return true;
        } catch (JsonParseException exception) {
            DisableEntity.LOGGER.warn("Failed to import config JSON; keeping the current settings.", exception);
            return false;
        }
    }

    public static synchronized void applyPreset(OptimizationPreset preset) {
        load();
        if (preset == null) {
            preset = OptimizationPreset.CUSTOM;
        }

        preset.apply(config);
        config.activePreset = preset;
        saveWithActivePreset(preset);
    }

    public static synchronized boolean toggleGlobalEnabled() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.globalEnabled = !currentConfig.globalEnabled;
        save();
        return currentConfig.globalEnabled;
    }

    public static synchronized boolean toggleEntityRendering() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.entityRendering.enabled = !currentConfig.entityRendering.enabled;
        save();
        return currentConfig.entityRendering.enabled;
    }

    public static synchronized boolean toggleParticles() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.particles.enabled = !currentConfig.particles.enabled;
        save();
        return currentConfig.particles.enabled;
    }

    public static synchronized boolean toggleBlockEntities() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.blockEntities.enabled = !currentConfig.blockEntities.enabled;
        save();
        return currentConfig.blockEntities.enabled;
    }

    public static synchronized boolean toggleNametags() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.nametags.enabled = !currentConfig.nametags.enabled;
        save();
        return currentConfig.nametags.enabled;
    }

    public static synchronized boolean toggleBlockStates() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.blockStates.enabled = !currentConfig.blockStates.enabled;
        save();
        return currentConfig.blockStates.enabled;
    }

    public static synchronized boolean toggleWorldRendering() {
        DisableEntityConfig currentConfig = getConfig();
        currentConfig.worldRendering.enabled = !currentConfig.worldRendering.enabled;
        save();
        return currentConfig.worldRendering.enabled;
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