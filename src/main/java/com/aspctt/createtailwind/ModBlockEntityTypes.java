package com.aspctt.createtailwind;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateTailwind.MOD_ID);

    // Create's own backtank block entity, which charges from rotation and keeps the air, enchantments and name of
    // the item it was placed from. Only the type is this mod's, since Create's type accepts Create's blocks only.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BacktankBlockEntity>> JETPACK =
            BLOCK_ENTITY_TYPES.register("netherite_jetpack",
                    () -> BlockEntityType.Builder.of(ModBlockEntityTypes::createJetpack, ModBlocks.JETPACK.get()).build(null));

    // Placed jetpacks saved up to 1.0.x name their block entity "jetpack"; without the alias they would lose their
    // air on loading. See ModBlocks.
    static {
        BLOCK_ENTITY_TYPES.addAlias(CreateTailwind.id("jetpack"), JETPACK.getId());
    }

    private static BacktankBlockEntity createJetpack(BlockPos pos, BlockState state) {
        return new BacktankBlockEntity(JETPACK.get(), pos, state);
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    private ModBlockEntityTypes() {
    }
}
