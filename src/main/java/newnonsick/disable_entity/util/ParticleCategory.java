package newnonsick.disable_entity.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Coarse particle categories used for cheap spawn-time filtering.
 */
public enum ParticleCategory {
    BLOCK,
    ITEM,
    SMOKE,
    FLAME,
    EXPLOSION,
    SPELL,
    WATER,
    REDSTONE,
    AMBIENT,
    OTHER;

    private static final Map<Identifier, ParticleCategory> CACHE = new ConcurrentHashMap<>();

    public static ParticleCategory from(ParticleEffect effect) {
        Identifier identifier = Registries.PARTICLE_TYPE.getId(effect.getType());
        if (identifier == null) {
            return OTHER;
        }

        return CACHE.computeIfAbsent(identifier, ParticleCategory::classify);
    }

    private static ParticleCategory classify(Identifier identifier) {
        if (!"minecraft".equals(identifier.getNamespace())) {
            return OTHER;
        }

        String path = identifier.getPath();
        if (containsAny(path, "redstone", "dust", "note")) {
            return REDSTONE;
        }
        if (containsAny(path, "explosion", "dragon_breath", "sonic_boom")) {
            return EXPLOSION;
        }
        if (containsAny(path, "flame", "soul")) {
            return FLAME;
        }
        if (containsAny(path, "smoke", "ash", "campfire")) {
            return SMOKE;
        }
        if (containsAny(path, "water", "bubble", "splash", "drip", "rain")) {
            return WATER;
        }
        if (containsAny(path, "spell", "enchant", "effect", "instant", "witch")) {
            return SPELL;
        }
        if (containsAny(path, "block", "falling", "terrain")) {
            return BLOCK;
        }
        if (containsAny(path, "item", "pickup", "flying_item")) {
            return ITEM;
        }
        if (containsAny(path, "ambient", "portal", "vibration", "sculk", "shriek")) {
            return AMBIENT;
        }
        return OTHER;
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