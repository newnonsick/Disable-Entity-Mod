package newnonsick.disable_entity.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
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

    private static volatile DisableEntityConfig config = new DisableEntityConfig();
    private static volatile boolean loaded;

    private static final Path PROFILES_PATH = FabricLoader.getInstance().getConfigDir().resolve("disable-entity-profiles.json");
    private static final Map<String, DisableEntityConfig> serverProfiles = new HashMap<>();
    private static volatile String activeServerAddress;

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
            serverProfiles.putAll(readProfiles());
            loaded = true;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static DisableEntityConfig getConfig() {
        if (!loaded) {
            load();
        }

        String address = activeServerAddress;
        if (address != null) {
            DisableEntityConfig profile = serverProfiles.get(address);
            if (profile != null) {
                return profile;
            }
        }
        return config;
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
        DisableEntityConfig target = getConfig();
        target.sanitize();
        if (activePreset != null) {
            target.activePreset = activePreset;
        } else if (detectPreset) {
            target.activePreset = OptimizationPreset.detect(target);
        }

        if (activeServerAddress != null && serverProfiles.containsKey(activeServerAddress)) {
            writeProfiles();
        } else {
            writeConfig(config);
        }
    }

    public static void resetToDefaults() {
        WRITE_LOCK.lock();
        try {
            DisableEntityConfig fresh = new DisableEntityConfig();
            if (activeServerAddress != null && serverProfiles.containsKey(activeServerAddress)) {
                serverProfiles.put(activeServerAddress, fresh);
                writeProfiles();
            } else {
                config = fresh;
                writeConfig(config);
            }
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static String exportConfigToJson() {
        READ_LOCK.lock();
        try {
            DisableEntityConfig target = getConfig();
            target.sanitize();
            return GSON.toJson(target);
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
            if (activeServerAddress != null) {
                serverProfiles.put(activeServerAddress, importedConfig);
                writeProfiles();
            } else {
                config = importedConfig;
                saveInternal(true, null);
            }
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
            DisableEntityConfig target = getConfig();
            target.showPerformanceOverlay = !target.showPerformanceOverlay;
            saveInternal(true, null);
            return target.showPerformanceOverlay;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void activateServerProfile(String address) {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            activeServerAddress = address;
            DisableEntity.LOGGER.info("Activated server profile for {}", address);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void deactivateServerProfile() {
        WRITE_LOCK.lock();
        try {
            activeServerAddress = null;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static boolean hasServerProfile(String address) {
        READ_LOCK.lock();
        try {
            return serverProfiles.containsKey(address);
        } finally {
            READ_LOCK.unlock();
        }
    }

    public static String getActiveServerAddress() {
        return activeServerAddress;
    }

    public static Set<String> getServerProfileKeys() {
        READ_LOCK.lock();
        try {
            return Set.copyOf(serverProfiles.keySet());
        } finally {
            READ_LOCK.unlock();
        }
    }

    public static void saveCurrentAsServerProfile(String address) {
        WRITE_LOCK.lock();
        try {
            ensureLoaded();
            DisableEntityConfig current = getConfig();
            String json = GSON.toJson(current);
            DisableEntityConfig copy = GSON.fromJson(json, DisableEntityConfig.class);
            if (copy != null) {
                copy.sanitize();
                serverProfiles.put(address, copy);
                writeProfiles();
            }
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static void deleteServerProfile(String address) {
        WRITE_LOCK.lock();
        try {
            serverProfiles.remove(address);
            writeProfiles();
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

    private static Map<String, DisableEntityConfig> readProfiles() {
        if (!Files.exists(PROFILES_PATH)) {
            return new HashMap<>();
        }

        try (Reader reader = Files.newBufferedReader(PROFILES_PATH)) {
            Type type = new TypeToken<Map<String, DisableEntityConfig>>() {
            }.getType();
            Map<String, DisableEntityConfig> parsed = GSON.fromJson(reader, type);
            if (parsed != null) {
                for (DisableEntityConfig profile : parsed.values()) {
                    profile.sanitize();
                }
                return parsed;
            }
        } catch (JsonParseException | IOException exception) {
            DisableEntity.LOGGER.error("Failed to read profiles file {}, using empty map.", PROFILES_PATH, exception);
        }
        return new HashMap<>();
    }

    private static void writeProfiles() {
        try {
            Path parent = PROFILES_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path temporaryPath = PROFILES_PATH.resolveSibling(PROFILES_PATH.getFileName() + TEMP_FILE_SUFFIX);
            try (Writer writer = Files.newBufferedWriter(temporaryPath, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                GSON.toJson(serverProfiles, writer);
            }

            try {
                Files.move(temporaryPath, PROFILES_PATH, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException moveException) {
                Files.move(temporaryPath, PROFILES_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            DisableEntity.LOGGER.error("Failed to write profiles file {}.", PROFILES_PATH, exception);
        }
    }
}