package com.aspctt.createtailwind;

import com.aspctt.createtailwind.compat.AccessoriesCompat;
import com.aspctt.createtailwind.jetpack.JetpackHandler;
import com.aspctt.createtailwind.network.ModNetworking;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateTailwind.MOD_ID)
public class CreateTailwind {
    // Must match mod_id in gradle.properties.
    public static final String MOD_ID = "createtailwind";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The stress a placed jetpack puts on the shaft charging it, per RPM: the same as Create's backtanks.
    private static final double JETPACK_STRESS_IMPACT = 4.0;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateTailwind(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, TailwindConfig.SPEC);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModParticles.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(ModNetworking::register);
        modEventBus.addListener(CreateTailwind::onCommonSetup);
        modEventBus.addListener(CreateTailwind::onBuildCreativeTabs);

        NeoForge.EVENT_BUS.addListener(JetpackHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onPlayerFlyableFall);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onStartTracking);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onLoggedOut);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onChangedDimension);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onRespawn);
        NeoForge.EVENT_BUS.addListener(JetpackHandler::onServerStopped);
    }

    // Common setup runs in parallel with other mods', and neither of Create's registries below is documented as
    // thread-safe, so the registration waits for the main thread.
    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.IMPACTS.register(ModBlocks.JETPACK.get(), () -> JETPACK_STRESS_IMPACT);
            if (ModList.get().isLoaded("accessories")) {
                AccessoriesCompat.registerAirSource();
            }
        });
    }

    // Next to the elytra's tab mates, and in Create's own tab. An empty jetpack, then a full one beside it, the way
    // Create lists its backtanks full.
    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES
                || event.getTabKey() == AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()) {
            event.accept(ModItems.JETPACK.get());
            ItemStack full = new ItemStack(ModItems.JETPACK.get());
            full.set(AllDataComponents.BACKTANK_AIR, BacktankUtil.maxAirWithoutEnchants());
            event.accept(full);
        }
    }
}
