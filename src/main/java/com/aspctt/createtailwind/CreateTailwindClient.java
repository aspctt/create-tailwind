package com.aspctt.createtailwind;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateTailwind.MOD_ID, dist = Dist.CLIENT)
public class CreateTailwindClient {
    public CreateTailwindClient(IEventBus modEventBus, ModContainer modContainer) {
        // The config button in the mod list. Server config is only editable there in singleplayer.
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
