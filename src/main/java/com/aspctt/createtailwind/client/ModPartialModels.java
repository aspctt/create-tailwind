package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.CreateTailwind;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

// Models baked alongside the block models but drawn on their own. Each must be created before models load, which
// init() makes sure of by loading the class from the client constructor.
public final class ModPartialModels {
    // The shaft input on top of a placed jetpack, which turns with the shaft that charges it.
    public static final PartialModel JETPACK_SHAFT_INPUT =
            PartialModel.of(CreateTailwind.id("block/netherite_jetpack_shaft_input"));
    // The jet of air under each nozzle of a flying jetpack.
    public static final PartialModel JETPACK_FLAME = PartialModel.of(CreateTailwind.id("block/jetpack_flame"));
    // White octagons just under the nozzle openings, tinted to make them glow while a jetpack flies and cools. The
    // flame is flat, so from straight below it vanishes edge on, and the glowing openings show the jet instead.
    public static final PartialModel JETPACK_NOZZLE_GLOW =
            PartialModel.of(CreateTailwind.id("block/netherite_jetpack_nozzle_glow"));
    // The white fill behind the glass of the air gauge on each side, scaled to how full the tank is
    public static final PartialModel JETPACK_GAUGE_FILL =
            PartialModel.of(CreateTailwind.id("block/netherite_jetpack_gauge_fill"));

    public static void init() {
    }

    private ModPartialModels() {
    }
}
