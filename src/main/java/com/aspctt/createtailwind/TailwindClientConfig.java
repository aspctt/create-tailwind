package com.aspctt.createtailwind;

import net.neoforged.neoforge.common.ModConfigSpec;

// Client config: how this player sees and hears other jetpacks, including their own. Nothing here changes
// gameplay, so each player sets it for themselves.
public final class TailwindClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue EXHAUST_PARTICLES;
    public static final ModConfigSpec.IntValue PARTICLE_LIFETIME;
    public static final ModConfigSpec.IntValue PARTICLE_DENSITY;
    public static final ModConfigSpec.BooleanValue EXHAUST_SOUND;
    public static final ModConfigSpec.IntValue EXHAUST_VOLUME;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("exhaust");

        EXHAUST_PARTICLES = builder
                .comment("Whether jetpacks leave a smoke trail.")
                .define("particles", true);

        PARTICLE_LIFETIME = builder
                .comment("How long each puff of smoke lasts at most, in ticks. Each lives between three quarters of this and all of it.")
                .defineInRange("particleLifetime", 20, 5, 60);

        PARTICLE_DENSITY = builder
                .comment("Percentage of the smoke puffs to show. Lower it to thin the trail out.")
                .defineInRange("particleDensity", 100, 0, 100);

        EXHAUST_SOUND = builder
                .comment("Whether jetpacks make an engine sound while flying.")
                .define("sound", true);

        EXHAUST_VOLUME = builder
                .comment("Volume of the jetpack engine, as a percentage.")
                .defineInRange("soundVolume", 100, 0, 100);

        builder.pop();

        SPEC = builder.build();
    }

    private TailwindClientConfig() {
    }
}
