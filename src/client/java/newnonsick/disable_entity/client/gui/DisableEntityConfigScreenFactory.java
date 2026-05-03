package newnonsick.disable_entity.client.gui;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import newnonsick.disable_entity.client.util.ClientRenderRefresh;
import newnonsick.disable_entity.compat.ModCompatibility;
import newnonsick.disable_entity.config.DisableEntityConfig;
import newnonsick.disable_entity.config.DisableEntityConfigManager;
import newnonsick.disable_entity.config.OptimizationPreset;
import newnonsick.disable_entity.util.ParticleFilterMode;

/**
 * Builds the Cloth Config screen used by Mod Menu.
 */
public final class DisableEntityConfigScreenFactory {

    private DisableEntityConfigScreenFactory() {}

    public static Screen create(Screen parent) {
        DisableEntityConfig config = DisableEntityConfigManager.getConfig();
        OptimizationPreset initialPreset = config.activePreset;
        String initialBlockStateSignature = blockStateSignature(config);
        AtomicReference<OptimizationPreset> selectedPreset =
            new AtomicReference<>(initialPreset);

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.disable_entity.title"))
            .setSavingRunnable(() -> {
                OptimizationPreset preset = selectedPreset.get();
                if (preset == null || preset == initialPreset) {
                    DisableEntityConfigManager.save();
                    refreshAfterSave(initialBlockStateSignature, config);
                    return;
                }

                if (preset == OptimizationPreset.CUSTOM) {
                    DisableEntityConfigManager.saveWithActivePreset(preset);
                    refreshAfterSave(initialBlockStateSignature, config);
                    return;
                }

                DisableEntityConfigManager.applyPreset(preset);
                refreshAfterSave(initialBlockStateSignature, config);
            });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory generalCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.general")
        );
        addDescription(
            generalCategory,
            entryBuilder,
            "text.disable_entity.overview.description"
        );
        generalCategory.addEntry(
            entryBuilder
                .startEnumSelector(
                    Text.translatable("text.disable_entity.option.preset"),
                    OptimizationPreset.class,
                    config.activePreset
                )
                .setDefaultValue(OptimizationPreset.CUSTOM)
                .setEnumNameProvider(value ->
                    presetLabel((OptimizationPreset) value)
                )
                .setSaveConsumer(selectedPreset::set)
                .setTooltip(
                    Text.translatable("text.disable_entity.tooltip.preset")
                )
                .build()
        );
        generalCategory.addEntry(
            entryBuilder
                .startTextDescription(buildFeatureSummary(config))
                .build()
        );
        generalCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.global_enabled"),
                config.globalEnabled,
                value -> config.globalEnabled = value,
                "text.disable_entity.tooltip.global_enabled"
            )
        );
        generalCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.disable_entity_shadows"
                ),
                config.disableEntityShadows,
                value -> config.disableEntityShadows = value,
                "text.disable_entity.tooltip.disable_entity_shadows"
            )
        );

        ConfigCategory entityCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.entities")
        );
        addDescription(
            entityCategory,
            entryBuilder,
            "text.disable_entity.section.entities.description"
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.entity_rendering_enabled"
                ),
                config.entityRendering.enabled,
                value -> config.entityRendering.enabled = value,
                "text.disable_entity.tooltip.entity_rendering_enabled"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_players"),
                config.entityRendering.hidePlayers,
                value -> config.entityRendering.hidePlayers = value,
                "text.disable_entity.tooltip.hide_players"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_hostile_mobs"
                ),
                config.entityRendering.hideHostileMobs,
                value -> config.entityRendering.hideHostileMobs = value,
                "text.disable_entity.tooltip.hide_hostile_mobs"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_passive_mobs"
                ),
                config.entityRendering.hidePassiveMobs,
                value -> config.entityRendering.hidePassiveMobs = value,
                "text.disable_entity.tooltip.hide_passive_mobs"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_item_entities"
                ),
                config.entityRendering.hideItemEntities,
                value -> config.entityRendering.hideItemEntities = value,
                "text.disable_entity.tooltip.hide_item_entities"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_projectiles"
                ),
                config.entityRendering.hideProjectiles,
                value -> config.entityRendering.hideProjectiles = value,
                "text.disable_entity.tooltip.hide_projectiles"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_armor_stands"
                ),
                config.entityRendering.hideArmorStands,
                value -> config.entityRendering.hideArmorStands = value,
                "text.disable_entity.tooltip.hide_armor_stands"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_experience_orbs"
                ),
                config.entityRendering.hideExperienceOrbs,
                value -> config.entityRendering.hideExperienceOrbs = value,
                "text.disable_entity.tooltip.hide_experience_orbs"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_item_frames"
                ),
                config.entityRendering.hideItemFrames,
                value -> config.entityRendering.hideItemFrames = value,
                "text.disable_entity.tooltip.hide_item_frames"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_paintings"),
                config.entityRendering.hidePaintings,
                value -> config.entityRendering.hidePaintings = value,
                "text.disable_entity.tooltip.hide_paintings"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_leash_knots"
                ),
                config.entityRendering.hideLeashKnots,
                value -> config.entityRendering.hideLeashKnots = value,
                "text.disable_entity.tooltip.hide_leash_knots"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_display_entities"
                ),
                config.entityRendering.hideDisplayEntities,
                value -> config.entityRendering.hideDisplayEntities = value,
                "text.disable_entity.tooltip.hide_display_entities"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_vehicles"),
                config.entityRendering.hideVehicles,
                value -> config.entityRendering.hideVehicles = value,
                "text.disable_entity.tooltip.hide_vehicles"
            )
        );
        entityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_misc_entities"
                ),
                config.entityRendering.hideMiscellaneousEntities,
                value ->
                    config.entityRendering.hideMiscellaneousEntities = value,
                "text.disable_entity.tooltip.hide_misc_entities"
            )
        );

        ConfigCategory nametagCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.nametags")
        );
        addDescription(
            nametagCategory,
            entryBuilder,
            "text.disable_entity.section.nametags.description"
        );
        nametagCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.nametags_enabled"
                ),
                config.nametags.enabled,
                value -> config.nametags.enabled = value,
                "text.disable_entity.tooltip.nametags_enabled"
            )
        );
        nametagCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_player_nametags"
                ),
                config.nametags.hidePlayers,
                value -> config.nametags.hidePlayers = value,
                "text.disable_entity.tooltip.hide_player_nametags"
            )
        );
        nametagCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_mob_nametags"
                ),
                config.nametags.hideMobs,
                value -> config.nametags.hideMobs = value,
                "text.disable_entity.tooltip.hide_mob_nametags"
            )
        );
        nametagCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_armor_stand_labels"
                ),
                config.nametags.hideArmorStands,
                value -> config.nametags.hideArmorStands = value,
                "text.disable_entity.tooltip.hide_armor_stand_labels"
            )
        );

        ConfigCategory particlesCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.particles")
        );
        addDescription(
            particlesCategory,
            entryBuilder,
            "text.disable_entity.section.particles.description"
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.particles_enabled"
                ),
                config.particles.enabled,
                value -> config.particles.enabled = value,
                "text.disable_entity.tooltip.particles_enabled"
            )
        );
        particlesCategory.addEntry(
            entryBuilder
                .startEnumSelector(
                    Text.translatable(
                        "text.disable_entity.option.particle_mode"
                    ),
                    ParticleFilterMode.class,
                    config.particles.mode
                )
                .setDefaultValue(ParticleFilterMode.ALL)
                .setSaveConsumer(value -> config.particles.mode = value)
                .setTooltip(
                    Text.translatable(
                        "text.disable_entity.tooltip.particle_mode"
                    )
                )
                .build()
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_block"),
                config.particles.blockParticles,
                value -> config.particles.blockParticles = value,
                "text.disable_entity.tooltip.particle_block"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_item"),
                config.particles.itemParticles,
                value -> config.particles.itemParticles = value,
                "text.disable_entity.tooltip.particle_item"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_smoke"),
                config.particles.smokeParticles,
                value -> config.particles.smokeParticles = value,
                "text.disable_entity.tooltip.particle_smoke"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_flame"),
                config.particles.flameParticles,
                value -> config.particles.flameParticles = value,
                "text.disable_entity.tooltip.particle_flame"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.particle_explosion"
                ),
                config.particles.explosionParticles,
                value -> config.particles.explosionParticles = value,
                "text.disable_entity.tooltip.particle_explosion"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_spell"),
                config.particles.spellParticles,
                value -> config.particles.spellParticles = value,
                "text.disable_entity.tooltip.particle_spell"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_water"),
                config.particles.waterParticles,
                value -> config.particles.waterParticles = value,
                "text.disable_entity.tooltip.particle_water"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.particle_redstone"
                ),
                config.particles.redstoneParticles,
                value -> config.particles.redstoneParticles = value,
                "text.disable_entity.tooltip.particle_redstone"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.particle_ambient"
                ),
                config.particles.ambientParticles,
                value -> config.particles.ambientParticles = value,
                "text.disable_entity.tooltip.particle_ambient"
            )
        );
        particlesCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.particle_other"),
                config.particles.otherParticles,
                value -> config.particles.otherParticles = value,
                "text.disable_entity.tooltip.particle_other"
            )
        );

        ConfigCategory blockEntityCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.block_entities")
        );
        addDescription(
            blockEntityCategory,
            entryBuilder,
            "text.disable_entity.section.block_entities.description"
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.block_entities_enabled"
                ),
                config.blockEntities.enabled,
                value -> config.blockEntities.enabled = value,
                "text.disable_entity.tooltip.block_entities_enabled"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_chests"),
                config.blockEntities.hideChests,
                value -> config.blockEntities.hideChests = value,
                "text.disable_entity.tooltip.hide_chests"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_signs"),
                config.blockEntities.hideSigns,
                value -> config.blockEntities.hideSigns = value,
                "text.disable_entity.tooltip.hide_signs"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_beacons"),
                config.blockEntities.hideBeacons,
                value -> config.blockEntities.hideBeacons = value,
                "text.disable_entity.tooltip.hide_beacons"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_spawners"),
                config.blockEntities.hideSpawners,
                value -> config.blockEntities.hideSpawners = value,
                "text.disable_entity.tooltip.hide_spawners"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_shulker_boxes"
                ),
                config.blockEntities.hideShulkerBoxes,
                value -> config.blockEntities.hideShulkerBoxes = value,
                "text.disable_entity.tooltip.hide_shulker_boxes"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_banners"),
                config.blockEntities.hideBanners,
                value -> config.blockEntities.hideBanners = value,
                "text.disable_entity.tooltip.hide_banners"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.hide_furnaces"),
                config.blockEntities.hideFurnaces,
                value -> config.blockEntities.hideFurnaces = value,
                "text.disable_entity.tooltip.hide_furnaces"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_enchanting_tables"
                ),
                config.blockEntities.hideEnchantingTables,
                value -> config.blockEntities.hideEnchantingTables = value,
                "text.disable_entity.tooltip.hide_enchanting_tables"
            )
        );
        blockEntityCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.hide_misc_block_entities"
                ),
                config.blockEntities.hideMiscellaneousBlockEntities,
                value ->
                    config.blockEntities.hideMiscellaneousBlockEntities = value,
                "text.disable_entity.tooltip.hide_misc_block_entities"
            )
        );

        ConfigCategory blockStateCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.block_states")
        );
        addDescription(
            blockStateCategory,
            entryBuilder,
            "text.disable_entity.section.block_states.description"
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.block_states_enabled"
                ),
                config.blockStates.enabled,
                value -> config.blockStates.enabled = value,
                "text.disable_entity.tooltip.block_states_enabled"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_redstone"),
                config.blockStates.freezeRedstone,
                value -> config.blockStates.freezeRedstone = value,
                "text.disable_entity.tooltip.freeze_redstone"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_pistons"),
                config.blockStates.freezePistons,
                value -> config.blockStates.freezePistons = value,
                "text.disable_entity.tooltip.freeze_pistons"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_doors"),
                config.blockStates.freezeDoors,
                value -> config.blockStates.freezeDoors = value,
                "text.disable_entity.tooltip.freeze_doors"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_rails"),
                config.blockStates.freezeRails,
                value -> config.blockStates.freezeRails = value,
                "text.disable_entity.tooltip.freeze_rails"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_sculk"),
                config.blockStates.freezeSculk,
                value -> config.blockStates.freezeSculk = value,
                "text.disable_entity.tooltip.freeze_sculk"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_crafters"),
                config.blockStates.freezeCrafters,
                value -> config.blockStates.freezeCrafters = value,
                "text.disable_entity.tooltip.freeze_crafters"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.freeze_observers"
                ),
                config.blockStates.freezeObservers,
                value -> config.blockStates.freezeObservers = value,
                "text.disable_entity.tooltip.freeze_observers"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.freeze_repeaters_comparators"
                ),
                config.blockStates.freezeRepeatersComparators,
                value -> config.blockStates.freezeRepeatersComparators = value,
                "text.disable_entity.tooltip.freeze_repeaters_comparators"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.freeze_bells"),
                config.blockStates.freezeBells,
                value -> config.blockStates.freezeBells = value,
                "text.disable_entity.tooltip.freeze_bells"
            )
        );
        blockStateCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.freeze_other_dynamic"
                ),
                config.blockStates.freezeOtherDynamic,
                value -> config.blockStates.freezeOtherDynamic = value,
                "text.disable_entity.tooltip.freeze_other_dynamic"
            )
        );

        ConfigCategory worldRenderCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.world_rendering")
        );
        addDescription(
            worldRenderCategory,
            entryBuilder,
            "text.disable_entity.section.world_rendering.description"
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.world_rendering_enabled"
                ),
                config.worldRendering.enabled,
                value -> config.worldRendering.enabled = value,
                "text.disable_entity.tooltip.world_rendering_enabled"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.disable_clouds"),
                config.worldRendering.disableClouds,
                value -> config.worldRendering.disableClouds = value,
                "text.disable_entity.tooltip.disable_clouds"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.disable_weather"),
                config.worldRendering.disableWeather,
                value -> config.worldRendering.disableWeather = value,
                "text.disable_entity.tooltip.disable_weather"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.disable_vignette"
                ),
                config.worldRendering.disableVignette,
                value -> config.worldRendering.disableVignette = value,
                "text.disable_entity.tooltip.disable_vignette"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.disable_hand_bob"
                ),
                config.worldRendering.disableHandBob,
                value -> config.worldRendering.disableHandBob = value,
                "text.disable_entity.tooltip.disable_hand_bob"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable("text.disable_entity.option.disable_fog"),
                config.worldRendering.disableFog,
                value -> config.worldRendering.disableFog = value,
                "text.disable_entity.tooltip.disable_fog"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.disable_overlays"
                ),
                config.worldRendering.disableOverlays,
                value -> config.worldRendering.disableOverlays = value,
                "text.disable_entity.tooltip.disable_overlays"
            )
        );
        worldRenderCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.show_performance_overlay"
                ),
                config.showPerformanceOverlay,
                value -> config.showPerformanceOverlay = value,
                "text.disable_entity.tooltip.show_performance_overlay"
            )
        );

        ConfigCategory distanceCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.distance_culling")
        );
        addDescription(
            distanceCategory,
            entryBuilder,
            "text.disable_entity.section.distance_culling.description"
        );
        distanceCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.distance_culling_enabled"
                ),
                config.distanceCulling.enabled,
                value -> config.distanceCulling.enabled = value,
                "text.disable_entity.tooltip.distance_culling_enabled"
            )
        );
        distanceCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.entity_distance_limit_enabled"
                ),
                config.distanceCulling.entityDistanceLimitEnabled,
                value ->
                    config.distanceCulling.entityDistanceLimitEnabled = value,
                "text.disable_entity.tooltip.entity_distance_limit_enabled"
            )
        );
        distanceCategory.addEntry(
            entryBuilder
                .startIntSlider(
                    Text.translatable(
                        "text.disable_entity.option.entity_render_distance"
                    ),
                    config.distanceCulling.entityRenderDistance,
                    DisableEntityConfig.MIN_RENDER_DISTANCE,
                    DisableEntityConfig.MAX_ENTITY_RENDER_DISTANCE
                )
                .setDefaultValue(
                    DisableEntityConfig.DEFAULT_ENTITY_RENDER_DISTANCE
                )
                .setSaveConsumer(value ->
                    config.distanceCulling.entityRenderDistance = value
                )
                .setTextGetter(value ->
                    Text.translatable("text.disable_entity.value.blocks", value)
                )
                .setTooltip(
                    Text.translatable(
                        "text.disable_entity.tooltip.entity_render_distance"
                    )
                )
                .build()
        );
        distanceCategory.addEntry(
            booleanEntry(
                entryBuilder,
                Text.translatable(
                    "text.disable_entity.option.block_entity_distance_limit_enabled"
                ),
                config.distanceCulling.blockEntityDistanceLimitEnabled,
                value ->
                    config.distanceCulling.blockEntityDistanceLimitEnabled =
                        value,
                "text.disable_entity.tooltip.block_entity_distance_limit_enabled"
            )
        );
        distanceCategory.addEntry(
            entryBuilder
                .startIntSlider(
                    Text.translatable(
                        "text.disable_entity.option.block_entity_render_distance"
                    ),
                    config.distanceCulling.blockEntityRenderDistance,
                    DisableEntityConfig.MIN_RENDER_DISTANCE,
                    DisableEntityConfig.MAX_BLOCK_ENTITY_RENDER_DISTANCE
                )
                .setDefaultValue(
                    DisableEntityConfig.DEFAULT_BLOCK_ENTITY_RENDER_DISTANCE
                )
                .setSaveConsumer(value ->
                    config.distanceCulling.blockEntityRenderDistance = value
                )
                .setTextGetter(value ->
                    Text.translatable("text.disable_entity.value.blocks", value)
                )
                .setTooltip(
                    Text.translatable(
                        "text.disable_entity.tooltip.block_entity_render_distance"
                    )
                )
                .build()
        );

        ConfigCategory hotkeyCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.hotkeys")
        );
        addDescription(
            hotkeyCategory,
            entryBuilder,
            "text.disable_entity.section.hotkeys.description"
        );
        hotkeyCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable("text.disable_entity.hotkeys.description")
                )
                .build()
        );

        ConfigCategory compatCategory = builder.getOrCreateCategory(
            Text.translatable("text.disable_entity.category.compatibility")
        );
        addDescription(
            compatCategory,
            entryBuilder,
            "text.disable_entity.section.compatibility.description"
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.SODIUM_LOADED
                            ? "text.disable_entity.compat.sodium.present"
                            : "text.disable_entity.compat.sodium.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.IRIS_LOADED
                            ? "text.disable_entity.compat.iris.present"
                            : "text.disable_entity.compat.iris.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.ENTITY_CULLING_LOADED
                            ? "text.disable_entity.compat.entity_culling.present"
                            : "text.disable_entity.compat.entity_culling.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.LITHIUM_LOADED
                            ? "text.disable_entity.compat.lithium.present"
                            : "text.disable_entity.compat.lithium.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.MORE_CULLING_LOADED
                            ? "text.disable_entity.compat.more_culling.present"
                            : "text.disable_entity.compat.more_culling.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.IMMEDIATELY_FAST_LOADED
                            ? "text.disable_entity.compat.immediately_fast.present"
                            : "text.disable_entity.compat.immediately_fast.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.ENHANCED_BLOCK_ENTITIES_LOADED
                            ? "text.disable_entity.compat.enhanced_block_entities.present"
                            : "text.disable_entity.compat.enhanced_block_entities.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.DYNAMIC_FPS_LOADED
                            ? "text.disable_entity.compat.dynamic_fps.present"
                            : "text.disable_entity.compat.dynamic_fps.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.INDIUM_LOADED
                            ? "text.disable_entity.compat.indium.present"
                            : "text.disable_entity.compat.indium.absent"
                    )
                )
                .build()
        );
        compatCategory.addEntry(
            entryBuilder
                .startTextDescription(
                    Text.translatable(
                        ModCompatibility.CONTINUITY_LOADED
                            ? "text.disable_entity.compat.continuity.present"
                            : "text.disable_entity.compat.continuity.absent"
                    )
                )
                .build()
        );

        return builder.build();
    }

    private static void addDescription(
        ConfigCategory category,
        ConfigEntryBuilder entryBuilder,
        String translationKey
    ) {
        category.addEntry(
            entryBuilder
                .startTextDescription(Text.translatable(translationKey))
                .build()
        );
    }

    private static Text buildFeatureSummary(DisableEntityConfig config) {
        int activeFeatureGroups = 0;
        if (config.disableEntityShadows) {
            activeFeatureGroups++;
        }
        if (config.entityRendering.enabled) {
            activeFeatureGroups++;
        }
        if (config.nametags.enabled) {
            activeFeatureGroups++;
        }
        if (config.particles.enabled) {
            activeFeatureGroups++;
        }
        if (config.blockEntities.enabled) {
            activeFeatureGroups++;
        }
        if (config.blockStates.enabled) {
            activeFeatureGroups++;
        }
        if (config.worldRendering.enabled) {
            activeFeatureGroups++;
        }
        if (config.distanceCulling.enabled) {
            activeFeatureGroups++;
        }

        return Text.literal(
            "Preset: " +
                presetLabel(config.activePreset).getString() +
                ". Active feature groups: " +
                activeFeatureGroups +
                "/8. Master switch: " +
                (config.globalEnabled ? "on" : "off") +
                "."
        );
    }

    private static Text presetLabel(OptimizationPreset preset) {
        return switch (preset) {
            case CUSTOM -> Text.translatable(
                "text.disable_entity.preset.custom"
            );
            case BALANCED -> Text.translatable(
                "text.disable_entity.preset.balanced"
            );
            case PERFORMANCE -> Text.translatable(
                "text.disable_entity.preset.performance"
            );
            case AGGRESSIVE -> Text.translatable(
                "text.disable_entity.preset.aggressive"
            );
        };
    }

    private static void refreshAfterSave(
        String initialBlockStateSignature,
        DisableEntityConfig config
    ) {
        if (!initialBlockStateSignature.equals(blockStateSignature(config))) {
            ClientRenderRefresh.refreshBlockStates();
            return;
        }

        ClientRenderRefresh.refreshAll();
    }

    private static String blockStateSignature(DisableEntityConfig config) {
        return config.blockStates.enabled +
            ":" +
            config.blockStates.freezeRedstone +
            ":" +
            config.blockStates.freezePistons +
            ":" +
            config.blockStates.freezeDoors +
            ":" +
            config.blockStates.freezeRails +
            ":" +
            config.blockStates.freezeSculk +
            ":" +
            config.blockStates.freezeCrafters +
            ":" +
            config.blockStates.freezeObservers +
            ":" +
            config.blockStates.freezeRepeatersComparators +
            ":" +
            config.blockStates.freezeBells +
            ":" +
            config.blockStates.freezeOtherDynamic;
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<
        Boolean
    > booleanEntry(
        ConfigEntryBuilder entryBuilder,
        Text title,
        boolean currentValue,
        Consumer<Boolean> saveConsumer,
        String tooltipKey
    ) {
        return entryBuilder
            .startBooleanToggle(title, currentValue)
            .setDefaultValue(currentValue)
            .setSaveConsumer(saveConsumer)
            .setTooltip(Text.translatable(tooltipKey))
            .build();
    }
}
