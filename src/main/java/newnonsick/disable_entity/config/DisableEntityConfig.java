package newnonsick.disable_entity.config;

import java.util.Objects;

import net.minecraft.util.math.MathHelper;
import newnonsick.disable_entity.util.ParticleFilterMode;

/**
 * Strongly typed configuration model for all optimization controls.
 */
public final class DisableEntityConfig {
    public static final int CURRENT_CONFIG_VERSION = 3;
    public static final int MIN_RENDER_DISTANCE = 0;
    public static final int MAX_ENTITY_RENDER_DISTANCE = 512;
    public static final int DEFAULT_ENTITY_RENDER_DISTANCE = 96;
    public static final int MAX_BLOCK_ENTITY_RENDER_DISTANCE = 256;
    public static final int DEFAULT_BLOCK_ENTITY_RENDER_DISTANCE = 64;

    public int configVersion = CURRENT_CONFIG_VERSION;
    public OptimizationPreset activePreset = OptimizationPreset.CUSTOM;
    public boolean globalEnabled = true;
    public EntityRendering entityRendering = new EntityRendering();
    public boolean disableEntityShadows = false;
    public NametagRendering nametags = new NametagRendering();
    public ParticleRendering particles = new ParticleRendering();
    public BlockEntityRendering blockEntities = new BlockEntityRendering();
    public BlockStateRendering blockStates = new BlockStateRendering();
    public WorldRendering worldRendering = new WorldRendering();
    public DistanceCulling distanceCulling = new DistanceCulling();

    public void sanitize() {
        if (configVersion <= 0 || configVersion < CURRENT_CONFIG_VERSION) {
            configVersion = CURRENT_CONFIG_VERSION;
        }

        if (activePreset == null) {
            activePreset = OptimizationPreset.CUSTOM;
        }

        if (entityRendering == null) {
            entityRendering = new EntityRendering();
        }
        if (nametags == null) {
            nametags = new NametagRendering();
        }
        if (particles == null) {
            particles = new ParticleRendering();
        }
        if (blockEntities == null) {
            blockEntities = new BlockEntityRendering();
        }
        if (blockStates == null) {
            blockStates = new BlockStateRendering();
        }
        if (worldRendering == null) {
            worldRendering = new WorldRendering();
        }
        if (distanceCulling == null) {
            distanceCulling = new DistanceCulling();
        }

        particles.mode = Objects.requireNonNullElse(particles.mode, ParticleFilterMode.ALL);
        distanceCulling.entityRenderDistance = MathHelper.clamp(distanceCulling.entityRenderDistance,
                MIN_RENDER_DISTANCE, MAX_ENTITY_RENDER_DISTANCE);
        distanceCulling.blockEntityRenderDistance = MathHelper.clamp(distanceCulling.blockEntityRenderDistance,
                MIN_RENDER_DISTANCE, MAX_BLOCK_ENTITY_RENDER_DISTANCE);
    }

    public static final class EntityRendering {
        public boolean enabled = false;
        public boolean hidePlayers = true;
        public boolean hideHostileMobs = true;
        public boolean hidePassiveMobs = true;
        public boolean hideItemEntities = true;
        public boolean hideProjectiles = true;
        public boolean hideArmorStands = true;
        public boolean hideExperienceOrbs = true;
        public boolean hideItemFrames = true;
        public boolean hidePaintings = true;
        public boolean hideLeashKnots = true;
        public boolean hideDisplayEntities = true;
        public boolean hideVehicles = true;
        public boolean hideMiscellaneousEntities = true;
    }

    public static final class NametagRendering {
        public boolean enabled = false;
        public boolean hidePlayers = true;
        public boolean hideMobs = true;
        public boolean hideArmorStands = true;
    }

    public static final class ParticleRendering {
        public boolean enabled = false;
        public ParticleFilterMode mode = ParticleFilterMode.ALL;
        public boolean blockParticles = true;
        public boolean itemParticles = true;
        public boolean smokeParticles = true;
        public boolean flameParticles = true;
        public boolean explosionParticles = true;
        public boolean spellParticles = true;
        public boolean waterParticles = true;
        public boolean redstoneParticles = true;
        public boolean ambientParticles = true;
        public boolean otherParticles = true;
    }

    public static final class BlockEntityRendering {
        public boolean enabled = false;
        public boolean hideChests = true;
        public boolean hideSigns = true;
        public boolean hideBeacons = true;
        public boolean hideSpawners = true;
        public boolean hideShulkerBoxes = true;
        public boolean hideBanners = true;
        public boolean hideFurnaces = true;
        public boolean hideEnchantingTables = true;
        public boolean hideMiscellaneousBlockEntities = true;
    }

    public static final class BlockStateRendering {
        public boolean enabled = false;
        public boolean freezeRedstone = true;
        public boolean freezePistons = true;
        public boolean freezeDoors = true;
        public boolean freezeRails = true;
        public boolean freezeSculk = true;
        public boolean freezeCrafters = true;
        public boolean freezeObservers = true;
        public boolean freezeRepeatersComparators = true;
        public boolean freezeBells = true;
        public boolean freezeOtherDynamic = false;
    }

    public static final class WorldRendering {
        public boolean enabled = false;
        public boolean disableClouds = false;
        public boolean disableWeather = false;
        public boolean disableVignette = false;
        public boolean disableHandBob = false;
        public boolean disableFog = false;
        public boolean disableOverlays = false;
    }

    public static final class DistanceCulling {
        public boolean enabled = false;
        public boolean entityDistanceLimitEnabled = false;
        public int entityRenderDistance = DEFAULT_ENTITY_RENDER_DISTANCE;
        public boolean blockEntityDistanceLimitEnabled = false;
        public int blockEntityRenderDistance = DEFAULT_BLOCK_ENTITY_RENDER_DISTANCE;
    }
}