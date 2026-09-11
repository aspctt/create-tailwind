package com.aspctt.createtailwind;

import net.neoforged.neoforge.common.ModConfigSpec;

// Server config: per world, and synced to clients that join.
public final class TailwindConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue AIR_CONSUMPTION_PER_TICK;
    public static final ModConfigSpec.BooleanValue IMMUNE_TO_FALL_DAMAGE;
    public static final ModConfigSpec.BooleanValue REMOVE_INVISIBILITY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.translation("createtailwind.configuration.jetpack").push("jetpack");

        AIR_CONSUMPTION_PER_TICK = builder
                .comment("Average air drawn from the backtank per tick of jetpack flight. Fractions are drawn as a chance each tick.")
                .translation("createtailwind.configuration.airConsumptionPerTick")
                .defineInRange("airConsumptionPerTick", 0.3, 0.0, 1000.0);

        IMMUNE_TO_FALL_DAMAGE = builder
                .comment("Whether a worn jetpack protects from fall damage the way creative flight does.")
                .translation("createtailwind.configuration.immuneToFallDamage")
                .define("immuneToFallDamage", false);

        REMOVE_INVISIBILITY = builder
                .comment("Whether jetpack flight removes Invisibility, since the exhaust gives the player away.")
                .translation("createtailwind.configuration.removeInvisibility")
                .define("removeInvisibility", true);

        builder.pop();

        SPEC = builder.build();
    }

    private TailwindConfig() {
    }
}
