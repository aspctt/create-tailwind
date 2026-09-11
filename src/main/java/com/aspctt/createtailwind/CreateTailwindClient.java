package com.aspctt.createtailwind;

import com.aspctt.createtailwind.client.JetpackEngineSounds;
import com.aspctt.createtailwind.client.JetpackExhaust;
import com.aspctt.createtailwind.client.JetpackFlight;
import com.aspctt.createtailwind.client.JetpackLayer;
import com.aspctt.createtailwind.client.JetpackRenderer;
import com.aspctt.createtailwind.client.JetpackSmokeParticle;
import com.aspctt.createtailwind.client.JetpackSparkParticle;
import com.aspctt.createtailwind.client.ModPartialModels;
import com.aspctt.createtailwind.client.compat.AccessoriesRenderers;
import com.aspctt.createtailwind.client.compat.CuriosRenderers;
import com.aspctt.createtailwind.client.config.TailwindConfigScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateTailwind.MOD_ID, dist = Dist.CLIENT)
public class CreateTailwindClient {
    public CreateTailwindClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TailwindClientConfig.SPEC);

        // The config button in the mod list. The screen needs YACL; without it the button offers to install it.
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parent) -> TailwindConfigScreen.create(parent));

        ModPartialModels.init();

        modEventBus.addListener(CreateTailwindClient::onClientSetup);
        modEventBus.addListener(CreateTailwindClient::onRegisterParticleProviders);
        modEventBus.addListener(CreateTailwindClient::onRegisterRenderers);
        modEventBus.addListener(CreateTailwindClient::onAddLayers);
        NeoForge.EVENT_BUS.addListener(JetpackFlight::onLevelChange);
        NeoForge.EVENT_BUS.addListener(JetpackFlight::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(JetpackEngineSounds::onClientTick);
        NeoForge.EVENT_BUS.addListener(JetpackEngineSounds::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(JetpackExhaust::onClientTick);
    }

    private static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.JETPACK_SMOKE.get(), JetpackSmokeParticle.Provider::new);
        event.registerSpriteSet(ModParticles.JETPACK_SPARK.get(), JetpackSparkParticle.Provider::new);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.JETPACK.get(), JetpackRenderer::new);
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        JetpackLayer.addToAll(event);
    }

    // Client setup runs in parallel with other mods', and neither registry is documented as thread-safe, so the
    // registration waits for the main thread.
    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("curios")) {
                CuriosRenderers.register();
            }
            if (ModList.get().isLoaded("accessories")) {
                AccessoriesRenderers.register();
            }
        });
    }
}
