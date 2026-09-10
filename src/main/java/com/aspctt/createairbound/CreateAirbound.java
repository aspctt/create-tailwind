package com.aspctt.createairbound;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateAirbound.MOD_ID)
public class CreateAirbound {
    // Must match mod_id in gradle.properties.
    public static final String MOD_ID = "createairbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateAirbound(IEventBus modEventBus, ModContainer modContainer) {
    }
}
