package newnonsick.disable_entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import newnonsick.disable_entity.compat.CompatibilityDecisions;
import newnonsick.disable_entity.config.DisableEntityConfig;
import newnonsick.disable_entity.config.DisableEntityConfigManager;

/**
 * Centralized rendering decisions shared by all client-side mixins.
 */
public final class RenderRules {
    private RenderRules() {
    }

    public static boolean shouldHideEntity(Entity entity, double squaredDistanceToCamera) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        if (!config.globalEnabled || !config.entityRendering.enabled) {
            return false;
        }

        if (CompatibilityDecisions.shouldUseEntityDistanceCulling(config)
                && isBeyondDistanceLimit(squaredDistanceToCamera, config.distanceCulling.entityRenderDistance)) {
            return true;
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
        if (isHostileEntity(entity)) {
            return config.entityRendering.hideHostileMobs;
        }
        if (isPassiveEntity(entity)) {
            return config.entityRendering.hidePassiveMobs;
        }

        return config.entityRendering.hideMiscellaneousEntities;
    }

    public static boolean shouldHideEntityShadow() {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        return config.globalEnabled && config.disableEntityShadows;
    }

    public static boolean shouldHideNametag(EntityRenderState state) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
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
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
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
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        return config.globalEnabled && config.particles.enabled && config.particles.mode == ParticleFilterMode.ALL;
    }

    public static boolean shouldHideParticleCategory(ParticleCategory category) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
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
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        return shouldFreezeBlockState(config, state);
    }

    static boolean shouldFreezeBlockState(DisableEntityConfig config, BlockState state) {
        DynamicBlockFamily family = DynamicBlockRegistry.getInstance().getFamily(state.getBlock());
        if (family == null) {
            return false;
        }

        return shouldFreezeBlockState(config, family);
    }

    public static boolean shouldDisableWorldRender(String feature) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
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

    private static boolean isVehicleEntity(Entity entity) {
        if (entity instanceof BoatEntity || entity instanceof AbstractMinecartEntity) {
            return true;
        }

        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "boat", "minecart", "cart", "vehicle", "raft",
                "barge");
    }

    private static boolean isProjectileEntity(Entity entity) {
        if (entity instanceof ProjectileEntity || entity instanceof FishingBobberEntity
                || entity instanceof FireworkRocketEntity) {
            return true;
        }

        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "projectile", "arrow", "bolt", "trident",
                "fireball", "snowball", "egg", "pearl", "dart", "shuriken", "bullet");
    }

    private static boolean isItemEntity(Entity entity) {
        if (entity instanceof net.minecraft.entity.ItemEntity) {
            return true;
        }

        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "item", "drop", "orb", "pickup");
    }

    private static boolean isHostileEntity(Entity entity) {
        if (entity instanceof HostileEntity || entity.getType().getSpawnGroup() == SpawnGroup.MONSTER) {
            return true;
        }

        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "zombie", "skeleton", "creeper", "spider",
                "witch", "enderman", "blaze", "ghast", "drowned", "slime", "magma", "guardian",
                "illager", "pillager", "raider", "wraith", "demon", "hostile", "monster");
    }

    private static boolean isPassiveEntity(Entity entity) {
        SpawnGroup spawnGroup = entity.getType().getSpawnGroup();
        if (entity instanceof PassiveEntity || entity instanceof AmbientEntity || spawnGroup == SpawnGroup.CREATURE
                || spawnGroup == SpawnGroup.AMBIENT || spawnGroup == SpawnGroup.WATER_CREATURE
                || spawnGroup == SpawnGroup.WATER_AMBIENT || spawnGroup == SpawnGroup.AXOLOTLS
                || spawnGroup == SpawnGroup.UNDERGROUND_WATER_CREATURE) {
            return true;
        }

        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "cow", "pig", "sheep", "chicken",
                "horse", "animal", "passive", "friendly", "fish", "turtle", "axolotl", "goat",
                "villager", "frog", "cat", "wolf", "fox", "bat");
    }

    private static boolean isExperienceOrbEntity(Entity entity) {
        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "experience_orb");
    }

    private static boolean isItemFrameEntity(Entity entity) {
        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "item_frame");
    }

    private static boolean isPaintingEntity(Entity entity) {
        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "painting");
    }

    private static boolean isLeashKnotEntity(Entity entity) {
        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "leash_knot");
    }

    private static boolean isDisplayEntity(Entity entity) {
        Identifier identifier = getEntityIdentifier(entity);
        return identifier != null && containsAny(identifier.getPath(), "block_display", "item_display", "text_display",
                "display");
    }

    private static boolean isChestLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        if (type == BlockEntityType.CHEST || type == BlockEntityType.TRAPPED_CHEST
                || type == BlockEntityType.ENDER_CHEST
                || type == BlockEntityType.BARREL) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "chest", "barrel", "crate", "storage");
    }

    private static boolean isSignLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        if (type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "sign", "notice", "board");
    }

    private static boolean isBeaconLike(BlockEntity blockEntity) {
        if (blockEntity.getType() == BlockEntityType.BEACON) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "beacon", "beam");
    }

    private static boolean isSpawnerLike(BlockEntity blockEntity) {
        if (blockEntity.getType() == BlockEntityType.MOB_SPAWNER) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "spawner", "generator");
    }

    private static boolean isShulkerLike(BlockEntity blockEntity) {
        if (blockEntity.getType() == BlockEntityType.SHULKER_BOX) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "shulker");
    }

    private static boolean isBannerLike(BlockEntity blockEntity) {
        if (blockEntity.getType() == BlockEntityType.BANNER) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "banner", "flag");
    }

    private static boolean isFurnaceLike(BlockEntity blockEntity) {
        BlockEntityType<?> type = blockEntity.getType();
        if (type == BlockEntityType.FURNACE || type == BlockEntityType.BLAST_FURNACE
                || type == BlockEntityType.SMOKER) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "furnace", "smoker", "kiln");
    }

    private static boolean isEnchantingTableLike(BlockEntity blockEntity) {
        if (blockEntity.getType() == BlockEntityType.ENCHANTING_TABLE) {
            return true;
        }

        Identifier identifier = getBlockEntityIdentifier(blockEntity);
        return identifier != null && containsAny(identifier.getPath(), "enchant", "altar");
    }

    private static Identifier getEntityIdentifier(Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType());
    }

    private static Identifier getBlockEntityIdentifier(BlockEntity blockEntity) {
        return Registries.BLOCK_ENTITY_TYPE.getId(blockEntity.getType());
    }

    private static boolean containsAny(String path, String... fragments) {
        for (String fragment : fragments) {
            if (path.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}