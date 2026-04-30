package newnonsick.disable_entity.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screen.Screen;
import newnonsick.disable_entity.client.gui.DisableEntityConfigScreenFactory;

/**
 * Mod Menu integration that exposes the Cloth Config settings screen.
 */
public final class DisableEntityModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new ConfigScreenFactory<Screen>() {
            @Override
            public Screen create(Screen parent) {
                return DisableEntityConfigScreenFactory.create(parent);
            }
        };
    }
}