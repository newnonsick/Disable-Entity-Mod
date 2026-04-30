package newnonsick.disable_entity.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class DynamicBlockRegistry {

    private static final DynamicBlockRegistry INSTANCE = new DynamicBlockRegistry();

    private final Map<Block, DynamicBlockFamily> familyCache = new ConcurrentHashMap<>();
    private final Map<Block, BlockState> frozenStateCache = new ConcurrentHashMap<>();
    private volatile boolean populated;

    private DynamicBlockRegistry() {
    }

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

    public BlockState getFrozenState(Block block) {
        return frozenStateCache.get(block);
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

            DynamicBlockFamily family = classify(id);
            if (family != null) {
                familyCache.put(block, family);
                frozenStateCache.put(block, block.getDefaultState());
            }
        }

        populated = true;
    }

    private static DynamicBlockFamily classify(Identifier id) {
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

        return null;
    }
}
