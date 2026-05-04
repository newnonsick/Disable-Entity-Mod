package newnonsick.disable_entity.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public final class DynamicBlockRegistry {

    private static final DynamicBlockRegistry INSTANCE =
        new DynamicBlockRegistry();
    private static final int MAX_FROZEN_CACHE_SIZE = 4096;

    private final Map<Block, DynamicBlockFamily> familyCache =
        new ConcurrentHashMap<>();
    private final Map<BlockState, BlockState> frozenStateCache =
        new ConcurrentHashMap<>();
    private volatile boolean populated;

    private DynamicBlockRegistry() {}

    public static DynamicBlockRegistry getInstance() {
        DynamicBlockRegistry instance = INSTANCE;
        if (!instance.populated) {
            synchronized (DynamicBlockRegistry.class) {
                if (!INSTANCE.populated) {
                    INSTANCE.populate();
                }
            }
        }
        return instance;
    }

    public DynamicBlockFamily getFamily(Block block) {
        return familyCache.get(block);
    }

    public BlockState getFrozenState(BlockState state) {
        DynamicBlockFamily family = getFamily(state.getBlock());
        if (family == null) {
            return state;
        }

        if (frozenStateCache.size() >= MAX_FROZEN_CACHE_SIZE) {
            return freezeState(state, family);
        }

        return frozenStateCache.computeIfAbsent(state, s -> freezeState(s, family));
    }

    public void invalidate() {
        populated = false;
        familyCache.clear();
        frozenStateCache.clear();
    }

    private synchronized void populate() {
        if (populated) {
            return;
        }

        for (Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);
            if (id == null) {
                continue;
            }

            DynamicBlockFamily family = classify(id, block.getDefaultState());
            if (family != null) {
                familyCache.put(block, family);
            }
        }

        populated = true;
    }

    private static DynamicBlockFamily classify(Identifier id, BlockState defaultState) {
        String path = id.getPath();
        boolean isVanilla = "minecraft".equals(id.getNamespace());

        for (DynamicBlockFamily family : DynamicBlockFamily.values()) {
            if (family == DynamicBlockFamily.OTHER_DYNAMIC) {
                continue;
            }

            if (isVanilla) {
                if (family.matchesVanillaSuffix(path)) {
                    return family;
                }
            } else {
                if (family.matchesModdedFragment(path)) {
                    return family;
                }
            }
        }

        if (hasGenericDynamicProperty(defaultState)) {
            return DynamicBlockFamily.OTHER_DYNAMIC;
        }

        return null;
    }

    private static boolean hasGenericDynamicProperty(BlockState state) {
        return state.contains(Properties.OPEN)
            || state.contains(Properties.POWERED)
            || state.contains(Properties.LIT)
            || state.contains(Properties.TRIGGERED)
            || state.contains(Properties.CRAFTING)
            || state.contains(Properties.EXTENDED)
            || state.contains(Properties.SHORT)
            || state.contains(Properties.ENABLED)
            || state.contains(Properties.ATTACHED)
            || state.contains(Properties.DISARMED)
            || state.contains(Properties.LOCKED)
            || state.contains(Properties.CAN_SUMMON)
            || state.contains(Properties.POWER)
            || state.contains(Properties.LEVEL_15)
            || state.contains(Properties.SCULK_SENSOR_PHASE)
            || state.contains(Properties.SHRIEKING)
            || state.contains(Properties.BLOOM);
    }

    private static BlockState freezeState(
        BlockState state,
        DynamicBlockFamily family
    ) {
        return switch (family) {
            case REDSTONE -> freezeRedstone(state);
            case PISTON -> freezePiston(state);
            case DOOR -> freezeDoorLike(state);
            case RAIL -> freezeRail(state);
            case SCULK -> freezeSculk(state);
            case CRAFTER -> freezeCrafter(state);
            case OBSERVER -> freezeObserver(state);
            case REPEATER_COMPARATOR -> freezeRepeaterComparator(state);
            case BELL -> freezeBell(state);
            case OTHER_DYNAMIC -> freezeGenericDynamic(state);
        };
    }

    private static BlockState freezeRedstone(BlockState state) {
        state = setBooleanIfPresent(state, Properties.LIT, false);
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        state = setIntIfPresent(state, Properties.POWER, 0);
        state = setIntIfPresent(state, Properties.LEVEL_15, 0);
        return state;
    }

    private static BlockState freezePiston(BlockState state) {
        if (state.isOf(Blocks.PISTON_HEAD) || state.isOf(Blocks.MOVING_PISTON)) {
            return Blocks.AIR.getDefaultState();
        }

        state = setBooleanIfPresent(state, Properties.EXTENDED, false);
        state = setBooleanIfPresent(state, Properties.SHORT, false);
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        return state;
    }

    private static BlockState freezeDoorLike(BlockState state) {
        state = setBooleanIfPresent(state, Properties.OPEN, false);
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        return state;
    }

    private static BlockState freezeRail(BlockState state) {
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        return state;
    }

    private static BlockState freezeSculk(BlockState state) {
        state = setIntIfPresent(state, Properties.POWER, 0);
        state = setEnumIfPresent(
            state,
            Properties.SCULK_SENSOR_PHASE,
            SculkSensorPhase.INACTIVE
        );
        state = setBooleanIfPresent(state, Properties.SHRIEKING, false);
        state = setBooleanIfPresent(state, Properties.BLOOM, false);
        state = setBooleanIfPresent(state, Properties.CAN_SUMMON, false);
        return state;
    }

    private static BlockState freezeCrafter(BlockState state) {
        state = setBooleanIfPresent(state, Properties.TRIGGERED, false);
        state = setBooleanIfPresent(state, Properties.CRAFTING, false);
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        return state;
    }

    private static BlockState freezeObserver(BlockState state) {
        return setBooleanIfPresent(state, Properties.POWERED, false);
    }

    private static BlockState freezeRepeaterComparator(BlockState state) {
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        state = setBooleanIfPresent(state, Properties.LIT, false);
        state = setBooleanIfPresent(state, Properties.LOCKED, false);
        state = setIntIfPresent(state, Properties.POWER, 0);
        return state;
    }

    private static BlockState freezeBell(BlockState state) {
        return setBooleanIfPresent(state, Properties.POWERED, false);
    }

    private static BlockState freezeGenericDynamic(BlockState state) {
        state = setBooleanIfPresent(state, Properties.OPEN, false);
        state = setBooleanIfPresent(state, Properties.POWERED, false);
        state = setBooleanIfPresent(state, Properties.LIT, false);
        state = setBooleanIfPresent(state, Properties.TRIGGERED, false);
        state = setBooleanIfPresent(state, Properties.CRAFTING, false);
        state = setBooleanIfPresent(state, Properties.EXTENDED, false);
        state = setBooleanIfPresent(state, Properties.SHORT, false);
        state = setBooleanIfPresent(state, Properties.ENABLED, false);
        state = setBooleanIfPresent(state, Properties.ATTACHED, false);
        state = setBooleanIfPresent(state, Properties.DISARMED, false);
        state = setBooleanIfPresent(state, Properties.LOCKED, false);
        state = setBooleanIfPresent(state, Properties.CAN_SUMMON, false);
        state = setIntIfPresent(state, Properties.POWER, 0);
        state = setIntIfPresent(state, Properties.LEVEL_15, 0);
        return state;
    }

    private static BlockState setBooleanIfPresent(
        BlockState state,
        BooleanProperty property,
        boolean value
    ) {
        return state.contains(property) ? state.with(property, value) : state;
    }

    private static BlockState setIntIfPresent(
        BlockState state,
        IntProperty property,
        int value
    ) {
        return state.contains(property) ? state.with(property, value) : state;
    }

    private static <
        T extends Enum<T> & StringIdentifiable
    > BlockState setEnumIfPresent(
        BlockState state,
        EnumProperty<T> property,
        T value
    ) {
        return state.contains(property) ? state.with(property, value) : state;
    }
}
