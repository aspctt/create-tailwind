package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.CreateTailwind;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

// Models baked alongside the block models but drawn on their own. Each must be created before models load, which
// init() makes sure of by loading the class from the client constructor.
public final class ModPartialModels {
    // The shaft input on top of a placed jetpack, which turns with the shaft that charges it.
    public static final PartialModel JETPACK_SHAFT_INPUT = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, "block/jetpack_shaft_input"));

    public static void init() {
    }

    private ModPartialModels() {
    }
}
