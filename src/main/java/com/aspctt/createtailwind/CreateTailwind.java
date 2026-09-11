package com.aspctt.createtailwind;

import com.aspctt.createtailwind.jetpack.JetpackHandler;
import com.aspctt.createtailwind.network.ModNetworking;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
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
        modContainer.registerConfig(ModConfig.Type.SERVER, TailwindConfig.SPEC);
        ModParticles.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(ModNetworking::register);

        NeoForge.EVENT_BUS.addListener(JetpackHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onPlayerFlyableFall);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onStartTracking);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onLoggedOut);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onChangedDimension);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onRespawn);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onServerStopped);
    }
}
