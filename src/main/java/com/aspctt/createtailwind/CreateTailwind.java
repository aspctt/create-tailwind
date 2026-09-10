package com.aspctt.createtailwind;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateTailwind.MOD_ID)
public class CreateTailwind {
    // Must match mod_id in gradle.properties.
    public static final String MOD_ID = "createtailwind";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateTailwind(IEventBus modEventBus, ModContainer modContainer) {
    }
}
