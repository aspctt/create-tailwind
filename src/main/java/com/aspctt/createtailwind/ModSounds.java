package com.aspctt.createtailwind;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, CreateTailwind.MOD_ID);

    // The jetpack hiss. It plays Create's steam sound (see sounds.json), but as an event of its own, so the client
    // can tell it apart from Create's steam engines when applying the sound settings, and give it its own subtitle.
    public static final ResourceLocation JETPACK_EXHAUST_ID =
            ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, "jetpack.exhaust");
    public static final Supplier<SoundEvent> JETPACK_EXHAUST =
            SOUND_EVENTS.register("jetpack.exhaust", () -> SoundEvent.createVariableRangeEvent(JETPACK_EXHAUST_ID));

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    private ModSounds() {
    }
}
