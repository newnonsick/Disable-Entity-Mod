package newnonsick.disable_entity.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import newnonsick.disable_entity.config.DisableEntityConfig;

class BlockStateRenderRulesTest {

    @Test
    void shouldFreezeRepresentativeFamilies() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.globalEnabled = true;
        config.blockStates.enabled = true;

        assertTrue(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.REDSTONE));
        assertTrue(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.PISTON));
        assertTrue(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.DOOR));
        assertFalse(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.OTHER_DYNAMIC));
    }

    @Test
    void shouldNotFreezeWhenDisabled() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.globalEnabled = true;
        config.blockStates.enabled = false;

        assertFalse(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.REDSTONE));
    }

    @Test
    void shouldFreezeOtherDynamicOnlyWhenEnabled() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.globalEnabled = true;
        config.blockStates.enabled = true;

        assertFalse(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.OTHER_DYNAMIC));

        config.blockStates.freezeOtherDynamic = true;

        assertTrue(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.OTHER_DYNAMIC));
    }

    @Test
    void shouldExposeFamilyLevelFreezeDecisionForSpecialRenderers() {
        DisableEntityConfig config = new DisableEntityConfig();
        config.globalEnabled = true;
        config.blockStates.enabled = true;

        assertTrue(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.PISTON));

        config.blockStates.freezePistons = false;

        assertFalse(RenderRules.shouldFreezeBlockState(config, DynamicBlockFamily.PISTON));
    }
}
