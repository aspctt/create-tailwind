package com.aspctt.createtailwind;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

// The jetpack engine's two layers. Both play existing sound files by reference (see sounds.json) as events of
// their own, so they carry this mod's subtitle and ranges rather than those of the blocks the files come from.
public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, CreateTailwind.MOD_ID);

    // Create's cogwheel rumble: the mechanical layer.
    public static final Supplier<SoundEvent> JETPACK_ENGINE = register("jetpack.engine");
    // Vanilla's beacon hum: the drone underneath, played at a lower pitch.
    public static final Supplier<SoundEvent> JETPACK_HUM = register("jetpack.hum");

    private static Supplier<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    private ModSounds() {
    }
}
