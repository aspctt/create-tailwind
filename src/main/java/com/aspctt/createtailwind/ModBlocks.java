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
    public static final DeferredBlock<JetpackBlock> JETPACK = BLOCKS.registerBlock("netherite_jetpack", JetpackBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion());

    // Up to 1.0.x the jetpack was registered as "jetpack". The alias loads jetpacks placed under that name as this
    // one; ModItems and ModBlockEntityTypes alias the item and the block entity the same way. Keep all three for as
    // long as worlds from those versions may be loaded.
    static {
        BLOCKS.addAlias(CreateTailwind.id("jetpack"), JETPACK.getId());
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private ModBlocks() {
    }
}
