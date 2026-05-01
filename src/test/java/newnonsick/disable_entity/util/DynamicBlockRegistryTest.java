package newnonsick.disable_entity.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


class DynamicBlockRegistryTest {

    @Test
    void vanillaBlocksAreClassifiedProperly() {
        // Redstone
        assertTrue(DynamicBlockFamily.REDSTONE.matchesVanillaPath("redstone_wire"));
        assertTrue(DynamicBlockFamily.REDSTONE.matchesVanillaPath("redstone_torch"));

        // Piston
        assertTrue(DynamicBlockFamily.PISTON.matchesVanillaPath("piston"));
        assertTrue(DynamicBlockFamily.PISTON.matchesVanillaPath("sticky_piston"));
        assertTrue(DynamicBlockFamily.PISTON.matchesVanillaPath("moving_piston"));

        // Doors/Trapdoors (these use suffix matching for vanilla)
        assertTrue(DynamicBlockFamily.DOOR.matchesVanillaSuffix("oak_door"));
        assertTrue(DynamicBlockFamily.DOOR.matchesVanillaSuffix("spruce_trapdoor"));
        assertTrue(DynamicBlockFamily.DOOR.matchesVanillaSuffix("acacia_fence_gate"));
        assertFalse(DynamicBlockFamily.DOOR.matchesVanillaSuffix("iron_door_knob")); // Should be exactly ending

        // Rails
        assertTrue(DynamicBlockFamily.RAIL.matchesVanillaPath("rail"));
        assertTrue(DynamicBlockFamily.RAIL.matchesVanillaPath("powered_rail"));

        // Observers/Repeaters
        assertTrue(DynamicBlockFamily.OBSERVER.matchesVanillaPath("observer"));
        assertTrue(DynamicBlockFamily.REPEATER_COMPARATOR.matchesVanillaPath("repeater"));
        assertTrue(DynamicBlockFamily.REPEATER_COMPARATOR.matchesVanillaPath("comparator"));
    }

    @Test
    void moddedBlocksAreClassifiedByHeuristic() {
        // Mock a modded block namespace by just testing the path fragment matcher
        // directly
        assertTrue(DynamicBlockFamily.REDSTONE.matchesModdedFragment("create:redstone_contact"));
        assertTrue(DynamicBlockFamily.PISTON.matchesModdedFragment("mechanical_piston"));
        assertTrue(DynamicBlockFamily.DOOR.matchesModdedFragment("glass_door"));
        assertTrue(DynamicBlockFamily.RAIL.matchesModdedFragment("high_speed_rail"));
    }
}
