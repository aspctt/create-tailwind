package com.aspctt.createtailwind;

import com.aspctt.createtailwind.jetpack.JetpackBlock;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateTailwind.MOD_ID);

    // The jetpack set down on the ground, where it charges from rotation the way Create's backtank does. Built
    // like Create's copper backtank, but it does not hide the faces of the blocks around it.
    public static final DeferredBlock<JetpackBlock> JETPACK = BLOCKS.registerBlock("jetpack", JetpackBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion());

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private ModBlocks() {
    }
}
