package com.aspctt.createairbound;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateAirbound.MOD_ID, dist = Dist.CLIENT)
public class CreateAirboundClient {
    public CreateAirboundClient(IEventBus modEventBus, ModContainer modContainer) {
    }
}
