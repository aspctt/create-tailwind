package com.aspctt.createtailwind;

import net.neoforged.neoforge.common.ModConfigSpec;

// Server config: per world, and synced to clients that join.
public final class TailwindConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue AIR_CONSUMPTION_PER_TICK;
    public static final ModConfigSpec.BooleanValue IMMUNE_TO_FALL_DAMAGE;
    public static final ModConfigSpec.BooleanValue REMOVE_INVISIBILITY;

    public static final ModConfigSpec.DoubleValue BOOST_ACCELERATION;
    public static final ModConfigSpec.DoubleValue BOOST_MAX_SPEED;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("jetpack");

        AIR_CONSUMPTION_PER_TICK = builder
                .comment("Average air drawn from the jetpack per tick of flight or elytra boost. Fractions are drawn as a chance each tick.")
                .defineInRange("airConsumptionPerTick", 0.3, 0.0, 1000.0);

        IMMUNE_TO_FALL_DAMAGE = builder
                .comment("Whether a worn jetpack protects from fall damage the way creative flight does.")
                .define("immuneToFallDamage", false);

        REMOVE_INVISIBILITY = builder
                .comment("Whether jetpack flight removes Invisibility, since the exhaust gives the player away.")
                .define("removeInvisibility", true);

        builder.pop();

        // Do a Barrel Roll's thrust defaults. Its thrust settings are the client's own; these are the server's, so
        // every player on it boosts alike.
        builder.push("elytraBoost");

        BOOST_ACCELERATION = builder
                .comment("How hard a jetpack worn with an elytra pushes a glide along while forward is held, in blocks per tick gained each tick from a standstill. The push fades as the glide nears the top speed.")
                .defineInRange("acceleration", 0.1, 0.0, 10.0);

        BOOST_MAX_SPEED = builder
                .comment("The glide speed, in blocks per tick, at which the jetpack stops adding to it. A dive can still go faster.")
                .defineInRange("maxSpeed", 2.0, 0.1, 100.0);

        builder.pop();

        SPEC = builder.build();
    }

    private TailwindConfig() {
    }
}
