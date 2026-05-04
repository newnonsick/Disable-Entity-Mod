package newnonsick.disable_entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.particle.ParticleEffect;
import newnonsick.disable_entity.compat.CompatibilityDecisions;
import newnonsick.disable_entity.config.DisableEntityConfig;
import newnonsick.disable_entity.config.DisableEntityConfigManager;

/**
 * Centralized rendering decisions shared by all client-side mixins.
 * Uses per-frame config caching and includes a self-render safeguard.
 */
public final class RenderRules {
    private static volatile DisableEntityConfig cachedConfig;

    private RenderRules() {
    }

    public static DisableEntityConfig config() {
        DisableEntityConfig c = cachedConfig;
        return c != null ? c : DisableEntityConfigManager.getConfig();
    }

    public static void cacheConfigForFrame() {
        cachedConfig = DisableEntityConfigManager.getConfig();
    }

    public static void clearFrameCache() {
        cachedConfig = null;
    }

    public static boolean shouldHideEntity(Entity entity, double squaredDistanceToCamera) {
        DisableEntityConfig config = config();
        if (!config.globalEnabled || !config.entityRendering.enabled) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null && entity == client.player) {
            return false;
        }

        if (CompatibilityDecisions.shouldUseEntityDistanceCulling(config)
                && isBeyondDistanceLimit(squaredDistanceToCamera, config.distanceCulling.entityRenderDistance)) {
            return true;
        }

        if (config.entityRendering.neverHideNamedEntities && entity.hasCustomName()) {
            return false;
        }
        if (config.entityRendering.neverHideTamedEntities && entity instanceof TameableEntity tameable
                && tameable.isTamed()) {
            return false;
        }

        if (entity instanceof ArmorStandEntity) {
            return config.entityRendering.hideArmorStands;
        }
        if (isExperienceOrbEntity(entity)) {
            return config.entityRendering.hideExperienceOrbs;
        }
        if (isItemFrameEntity(entity)) {
            return config.entityRendering.hideItemFrames;
        }
        if (isPaintingEntity(entity)) {
            return config.entityRendering.hidePaintings;
        }
        if (isLeashKnotEntity(entity)) {
            return config.entityRendering.hideLeashKnots;
        }
        if (isDisplayEntity(entity)) {
            return config.entityRendering.hideDisplayEntities;
        }
        if (isVehicleEntity(entity)) {
            return config.entityRendering.hideVehicles;
        }
        if (isProjectileEntity(entity)) {
            return config.entityRendering.hideProjectiles;
        }
        if (isItemEntity(entity)) {
            return config.entityRendering.hideItemEntities;
        }
        if (isPlayerEntity(entity)) {
            return config.entityRendering.hidePlayers;
        }
        if (isHostileEntity(entity)) {
            return config.entityRendering.hideHostileMobs;
        }
        if (isPassiveEntity(entity)) {
            return config.entityRendering.hidePassiveMobs;
        }

        return config.entityRendering.hideMiscellaneousEntities;
    }

    public static boolean shouldHideEntityShadow() {
        DisableEntityConfig config = config();
        return config.globalEnabled && config.disableEntityShadows;
    }

    public static boolean shouldHideNametag(EntityRenderState state) {
        DisableEntityConfig config = config();
        if (!config.globalEnabled || !config.nametags.enabled) {
            return false;
        }

        EntityType<?> entityType = state.entityType;
        if (entityType == null) {
            return false;
        }
        if (entityType == EntityType.PLAYER) {
            return config.nametags.hidePlayers;
        }
        if (entityType == EntityType.ARMOR_STAND) {
            return config.nametags.hideArmorStands;
        }

        return entityType.getSpawnGroup() != SpawnGroup.MISC && config.nametags.hideMobs;
    }

    public static boolean shouldHideBlockEntity(BlockEntity blockEntity, double squaredDistanceToCamera) {
        DisableEntityConfig config = config();
        if (!config.globalEnabled || !config.blockEntities.enabled) {
            return false;
        }

        if (CompatibilityDecisions.shouldUseBlockEntityDistanceCulling(config)
                && isBeyondDistanceLimit(squaredDistanceToCamera, config.distanceCulling.blockEntityRenderDistance)) {
            return true;
        }

        if (isChestLike(blockEntity)) {
            return config.blockEntities.hideChests;
        }
        if (isSignLike(blockEntity)) {
            return config.blockEntities.hideSigns;
        }
        if (isBeaconLike(blockEntity)) {
            return config.blockEntities.hideBeacons;
        }
        if (isSpawnerLike(blockEntity)) {
            return config.blockEntities.hideSpawners;
        }
        if (isShulkerLike(blockEntity)) {
            return config.blockEntities.hideShulkerBoxes;
        }
        if (isBannerLike(blockEntity)) {
            return config.blockEntities.hideBanners;
        }
        if (isFurnaceLike(blockEntity)) {
            return config.blockEntities.hideFurnaces;
        }
        if (isEnchantingTableLike(blockEntity)) {
            return config.blockEntities.hideEnchantingTables;
        }

        return config.blockEntities.hideMiscellaneousBlockEntities;
    }

    public static boolean shouldHideParticle(ParticleEffect effect) {
        return shouldHideParticleCategory(ParticleCategory.from(effect));
    }

    public static boolean shouldHideAllParticles() {
        DisableEntityConfig config = config();
        return config.globalEnabled && config.particles.enabled && config.particles.mode == ParticleFilterMode.ALL;
    }

    public static boolean shouldHideParticleCategory(ParticleCategory category) {
        DisableEntityConfig config = config();
        if (!config.globalEnabled || !config.particles.enabled) {
            return false;
        }

        return switch (config.particles.mode) {
            case ALL -> true;
            case WHITELIST -> !isParticleCategoryAllowed(config.particles, category);
            case BLACKLIST -> isParticleCategoryAllowed(config.particles, category);
        };
    }

    private static boolean isParticleCategoryAllowed(DisableEntityConfig.ParticleRendering config,
            ParticleCategory category) {
        return switch (category) {
            case BLOCK -> config.blockParticles;
            case ITEM -> config.itemParticles;
            case SMOKE -> config.smokeParticles;
            case FLAME -> config.flameParticles;
            case EXPLOSION -> config.explosionParticles;
            case SPELL -> config.spellParticles;
            case WATER -> config.waterParticles;
            case REDSTONE -> config.redstoneParticles;
            case AMBIENT -> config.ambientParticles;
            case OTHER -> config.otherParticles;
        };
    }

    public static boolean shouldFreezeBlockState(BlockState state) {
        DisableEntityConfig config = config();
        return shouldFreezeBlockState(config, state);
    }

    public static BlockState getRenderableBlockState(BlockState state) {
        if (!shouldFreezeBlockState(state)) {
            return state;
        }

        return DynamicBlockRegistry.getInstance().getFrozenState(state);
    }

    public static boolean shouldSkipFrozenBlockStateRerender(BlockState oldState, BlockState newState) {
        DisableEntityConfig config = config();
        return shouldSkipFrozenBlockStateRerender(config, oldState, newState);
    }

    public static boolean shouldFreezeDynamicBlockFamily(DynamicBlockFamily family) {
        DisableEntityConfig config = config();
        return shouldFreezeBlockState(config, family);
    }

    static boolean shouldSkipFrozenBlockStateRerender(DisableEntityConfig config, BlockState oldState,
            BlockState newState) {
        boolean oldStateFrozen = shouldFreezeBlockState(config, oldState);
        boolean newStateFrozen = shouldFreezeBlockState(config, newState);
        if (!oldStateFrozen && !newStateFrozen) {
            return false;
        }

        BlockState renderableOldState = oldStateFrozen
                ? DynamicBlockRegistry.getInstance().getFrozenState(oldState)
                : oldState;
        BlockState renderableNewState = newStateFrozen
                ? DynamicBlockRegistry.getInstance().getFrozenState(newState)
                : newState;
        return renderableOldState == renderableNewState || renderableOldState.equals(renderableNewState);
    }

    static boolean shouldFreezeBlockState(DisableEntityConfig config, BlockState state) {
        DynamicBlockFamily family = DynamicBlockRegistry.getInstance().getFamily(state.getBlock());
        if (family == null) {
            return false;
        }

        return shouldFreezeBlockState(config, family);
    }

    public static boolean shouldDisableWorldRender(String feature) {
        DisableEntityConfig config = config();
        if (!config.globalEnabled || !config.worldRendering.enabled) {
            return false;
        }

        return switch (feature) {
            case "clouds" -> config.worldRendering.disableClouds;
            case "weather" -> config.worldRendering.disableWeather;
            case "vignette" -> config.worldRendering.disableVignette;
            case "hand_bob" -> config.worldRendering.disableHandBob;
            case "fog" -> config.worldRendering.disableFog;
            case "overlays" -> config.worldRendering.disableOverlays;
            default -> false;
        };
    }

    static boolean shouldFreezeBlockState(DisableEntityConfig config, DynamicBlockFamily family) {
        if (!config.globalEnabled || !config.blockStates.enabled) {
            return false;
        }

        return isFamilyFrozen(config.blockStates, family);
    }

    private static boolean isFamilyFrozen(DisableEntityConfig.BlockStateRendering config,
            DynamicBlockFamily family) {
        return switch (family) {
            case REDSTONE -> config.freezeRedstone;
            case PISTON -> config.freezePistons;
            case DOOR -> config.freezeDoors;
            case RAIL -> config.freezeRails;
            case SCULK -> config.freezeSculk;
            case CRAFTER -> config.freezeCrafters;
            case OBSERVER -> config.freezeObservers;
            case REPEATER_COMPARATOR -> config.freezeRepeatersComparators;
            case BELL -> config.freezeBells;
            case OTHER_DYNAMIC -> config.freezeOtherDynamic;
        };
    }

    private static boolean isBeyondDistanceLimit(double squaredDistanceToCamera, int maxDistance) {
        double limit = (double) maxDistance * (double) maxDistance;
        return squaredDistanceToCamera > limit;
    }

    private static boolean isPlayerEntity(Entity entity) {
        return entity.getType() == EntityType.PLAYER;
    }

    private static boolean isVehicleEntity(Entity entity) {
        return entity instanceof BoatEntity || entity instanceof AbstractMinecartEntity;
    }

    private static boolean isProjectileEntity(Entity entity) {
        return entity instanceof ProjectileEntity || entity instanceof FishingBobberEntity
                || entity instanceof FireworkRocketEntity;
    }

    private static boolean isItemEntity(Entity entity) {
        return entity instanceof net.minecraft.entity.ItemEntity;
    }

    private static boolean isHostileEntity(Entity entity) {
        return entity instanceof HostileEntity || entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
    }

    private static boolean isPassiveEntity(Entity entity) {
        SpawnGroup spawnGroup = entity.getType().getSpawnGroup();
        return entity instanceof PassiveEntity || entity instanceof AmbientEntity || spawnGroup == SpawnGroup.CREATURE
                || spawnGroup == SpawnGroup.AMBIENT || spawnGroup == SpawnGroup.WATER_CREATURE
                || spawnGroup == SpawnGroup.WATER_AMBIENT || spawnGroup == SpawnGroup.AXOLOTLS
                || spawnGroup == SpawnGroup.UNDERGROUND_WATER_CREATURE;
    }

    private static boolean isExperienceOrbEntity(Entity entity) {
        return entity.getType() == EntityType.EXPERIENCE_ORB;
    }

    private static boolean isItemFrameEntity(Entity entity) {
        return entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME;
    }

    private static boolean isPaintingEntity(Entity entity) {
        return entity.getType() == EntityType.PAINTING;
    }

    private static boolean isLeashKnotEntity(Entity entity) {
        return entity.getType() == EntityType.LEASH_KNOT;
    }

    private static boolean isDisplayEntity(Entity entity) {
        return entity.getType() == EntityType.BLOCK_DISPLAY
                || entity.getType() == EntityType.ITEM_DISPLAY
                || entity.getType() == EntityType.TEXT_DISPLAY;
    }

    private static boolean isChestLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        return type == BlockEntityType.CHEST || type == BlockEntityType.TRAPPED_CHEST
                || type == BlockEntityType.ENDER_CHEST
                || type == BlockEntityType.BARREL;
    }

    private static boolean isSignLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        return type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN;
    }

    private static boolean isBeaconLike(BlockEntity blockEntity) {
        return blockEntity.getType() == BlockEntityType.BEACON;
    }

    private static boolean isSpawnerLike(BlockEntity blockEntity) {
        return blockEntity.getType() == BlockEntityType.MOB_SPAWNER;
    }

    private static boolean isShulkerLike(BlockEntity blockEntity) {
        return blockEntity.getType() == BlockEntityType.SHULKER_BOX;
    }

    private static boolean isBannerLike(BlockEntity blockEntity) {
        return blockEntity.getType() == BlockEntityType.BANNER;
    }

    private static boolean isFurnaceLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        return type == BlockEntityType.FURNACE || type == BlockEntityType.BLAST_FURNACE
                || type == BlockEntityType.SMOKER;
    }

    private static boolean isEnchantingTableLike(BlockEntity blockEntity) {
        return blockEntity.getType() == BlockEntityType.ENCHANTING_TABLE;
    }
}
