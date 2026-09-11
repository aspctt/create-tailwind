package com.aspctt.createtailwind;

import java.util.function.Supplier;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, CreateTailwind.MOD_ID);

    // Jetpack exhaust: campfire signal smoke that lasts a second at most. Not forced past the player's particle
    // settings or render distance, unlike the vanilla signal smoke it is drawn from.
    public static final Supplier<SimpleParticleType> JETPACK_SMOKE =
            PARTICLE_TYPES.register("jetpack_smoke", () -> new SimpleParticleType(false));

    // Amber sparks thrown from a flying jetpack's nozzles. Spawned by each client, never sent by the server.
    public static final Supplier<SimpleParticleType> JETPACK_SPARK =
            PARTICLE_TYPES.register("jetpack_spark", () -> new SimpleParticleType(false));

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

    private ModParticles() {
    }
}
