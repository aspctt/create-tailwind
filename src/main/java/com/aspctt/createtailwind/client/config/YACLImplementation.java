package com.aspctt.createtailwind.client.config;

import com.aspctt.createtailwind.TailwindClientConfig;
import com.aspctt.createtailwind.TailwindConfig;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

// Only loaded once TailwindConfigScreen has checked that YACL is present, so nothing else may reference it.
// Structured after Do Another Barrel Roll's YACL screen, by the same author.
public class YACLImplementation {
    public static Screen generateConfigScreen(Screen parent) {
        boolean serverEditable = isServerConfigEditable();

        var builder = YetAnotherConfigLib.createBuilder()
                .title(getText("title"))
                .category(ConfigCategory.createBuilder()
                        .name(getText("client"))
                        .group(OptionGroup.createBuilder()
                                .name(getText("effects"))
                                .option(getBooleanOption("effects", "flame")
                                        .binding(true, TailwindClientConfig.EXHAUST_FLAME::get, TailwindClientConfig.EXHAUST_FLAME::set)
                                        .build())
                                .option(getBooleanOption("effects", "sparks")
                                        .binding(true, TailwindClientConfig.EXHAUST_SPARKS::get, TailwindClientConfig.EXHAUST_SPARKS::set)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("particles"))
                                .option(getBooleanOption("particles", "enabled")
                                        .binding(true, TailwindClientConfig.EXHAUST_PARTICLES::get, TailwindClientConfig.EXHAUST_PARTICLES::set)
                                        .build())
                                .option(getOption(Integer.class, "particles", "lifetime")
                                        .controller(option -> IntegerSliderControllerBuilder.create(option)
                                                .range(5, 60)
                                                .step(1)
                                                .formatValue(ticks -> getText("format", "ticks", ticks, ticks / 20.0)))
                                        .binding(20, TailwindClientConfig.PARTICLE_LIFETIME::get, TailwindClientConfig.PARTICLE_LIFETIME::set)
                                        .build())
                                .option(getOption(Integer.class, "particles", "density")
                                        .controller(option -> getPercentSlider(option))
                                        .binding(100, TailwindClientConfig.PARTICLE_DENSITY::get, TailwindClientConfig.PARTICLE_DENSITY::set)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("sound"))
                                .option(getBooleanOption("sound", "enabled")
                                        .binding(true, TailwindClientConfig.EXHAUST_SOUND::get, TailwindClientConfig.EXHAUST_SOUND::set)
                                        .build())
                                .option(getOption(Integer.class, "sound", "volume")
                                        .controller(option -> getPercentSlider(option))
                                        .binding(100, TailwindClientConfig.EXHAUST_VOLUME::get, TailwindClientConfig.EXHAUST_VOLUME::set)
                                        .build())
                                .build())
                        .build());

        // The server config belongs to the world. It is only safe to change here when this client is the server
        // and nobody else is connected: a remote server's copy is read-only, and players on a LAN world would
        // keep the values they were sent on joining.
        var server = ConfigCategory.createBuilder()
                .name(getText("server"));
        if (serverEditable) {
            server.group(OptionGroup.createBuilder()
                    .name(getText("jetpack"))
                    .option(getOption(Double.class, "jetpack", "air_consumption")
                            .controller(option -> DoubleFieldControllerBuilder.create(option)
                                    .range(0.0, 1000.0))
                            .binding(0.3, TailwindConfig.AIR_CONSUMPTION_PER_TICK::get, TailwindConfig.AIR_CONSUMPTION_PER_TICK::set)
                            .build())
                    .option(getBooleanOption("jetpack", "immune_to_fall_damage")
                            .binding(false, TailwindConfig.IMMUNE_TO_FALL_DAMAGE::get, TailwindConfig.IMMUNE_TO_FALL_DAMAGE::set)
                            .build())
                    .option(getBooleanOption("jetpack", "remove_invisibility")
                            .binding(true, TailwindConfig.REMOVE_INVISIBILITY::get, TailwindConfig.REMOVE_INVISIBILITY::set)
                            .build())
                    .build());
        } else {
            server.option(LabelOption.create(getText("server", "unavailable")));
        }
        builder.category(server.build());

        return builder
                .save(() -> {
                    TailwindClientConfig.SPEC.save();
                    if (serverEditable) {
                        TailwindConfig.SPEC.save();
                    }
                })
                .build()
                .generateScreen(parent);
    }

    private static boolean isServerConfigEditable() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        return server != null && !server.isPublished() && TailwindConfig.SPEC.isLoaded();
    }

    private static <T> Option.Builder<T> getOption(Class<T> clazz, String category, String key) {
        return Option.<T>createBuilder()
                .name(getText(category, key))
                .description(OptionDescription.of(getText(category, key + ".description")));
    }

    private static Option.Builder<Boolean> getBooleanOption(String category, String key) {
        return getOption(Boolean.class, category, key)
                .controller(TickBoxControllerBuilder::create);
    }

    private static IntegerSliderControllerBuilder getPercentSlider(Option<Integer> option) {
        return IntegerSliderControllerBuilder.create(option)
                .range(0, 100)
                .step(5)
                .formatValue(percent -> getText("format", "percent", percent));
    }

    private static MutableComponent getText(String category, String key, Object... args) {
        return Component.translatable("config.createtailwind." + category + "." + key, args);
    }

    private static MutableComponent getText(String key) {
        return Component.translatable("config.createtailwind." + key);
    }
}
