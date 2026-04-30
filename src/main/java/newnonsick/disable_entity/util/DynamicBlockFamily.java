package newnonsick.disable_entity.util;

/**
 * Coarse block-family categories for blocks whose visual state changes
 * frequently on the client (redstone signals, piston motion, door state, etc.).
 * <p>
 * Each family carries a set of vanilla block-ID path fragments used for
 * classification. Modded blocks are matched by the same path-fragment
 * heuristics so they join a family automatically when their registry ID
 * contains a recognisable keyword.
 */
public enum DynamicBlockFamily {

    REDSTONE(
            new String[] { "redstone_wire", "redstone_torch", "redstone_wall_torch", "redstone_lamp" },
            new String[] { "redstone" }),

    PISTON(
            new String[] { "piston", "sticky_piston", "piston_head", "moving_piston" },
            new String[] { "piston" }),

    REPEATER_COMPARATOR(
            new String[] { "repeater", "comparator" },
            new String[] { "repeater", "comparator" }),

    DOOR(
            new String[] { "_door", "_trapdoor", "_fence_gate" },
            new String[] { "door", "trapdoor", "fence_gate" }),

    RAIL(
            new String[] { "rail", "powered_rail", "detector_rail", "activator_rail" },
            new String[] { "rail" }),

    SCULK(
            new String[] { "sculk_sensor", "calibrated_sculk_sensor", "sculk_shrieker" },
            new String[] { "sculk_sensor", "sculk_shrieker" }),

    OBSERVER(
            new String[] { "observer" },
            new String[] { "observer" }),

    CRAFTER(
            new String[] { "crafter" },
            new String[] { "crafter" }),

    BELL(
            new String[] { "bell" },
            new String[] { "bell" }),

    OTHER_DYNAMIC(
            new String[] {},
            new String[] {});

    private final String[] vanillaIdPaths;
    private final String[] moddedFragments;

    DynamicBlockFamily(String[] vanillaIdPaths, String[] moddedFragments) {
        this.vanillaIdPaths = vanillaIdPaths;
        this.moddedFragments = moddedFragments;
    }

    /**
     * Returns {@code true} when the given block-ID path exactly matches one of
     * this family's vanilla entries.
     */
    public boolean matchesVanillaPath(String path) {
        for (String vanillaPath : vanillaIdPaths) {
            if (path.equals(vanillaPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} when the given block-ID path contains one of this
     * family's modded-heuristic fragments. Only used for non-{@code minecraft}
     * namespaces so vanilla blocks are never double-matched.
     */
    public boolean matchesModdedFragment(String path) {
        for (String fragment : moddedFragments) {
            if (path.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} when the given vanilla block-ID path contains one of
     * the DOOR family's suffix patterns (needed because vanilla doors use
     * material-prefixed IDs like {@code oak_door}, {@code spruce_trapdoor}, etc.).
     */
    public boolean matchesVanillaSuffix(String path) {
        if (this != DOOR) {
            return matchesVanillaPath(path);
        }
        for (String suffix : vanillaIdPaths) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
