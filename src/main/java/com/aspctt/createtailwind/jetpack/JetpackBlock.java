package com.aspctt.createtailwind.jetpack;

import java.util.List;

import com.aspctt.createtailwind.ModBlockEntityTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// A jetpack set down on the ground. It behaves as Create's backtank does: a shaft on top charges it, an empty
// hand picks it back up into the chest slot, and breaking it keeps the air. Its facing is the way the tanks look,
// which placement turns towards the player.
public class JetpackBlock extends BacktankBlock {
    // The tanks, and the shaft input on top, as modelled facing south.
    private static final VoxelShaper SHAPE = VoxelShaper.forHorizontal(
            Shapes.or(Block.box(2, 0, 4, 14, 13, 11), Block.box(6, 13, 6, 10, 16, 10)), Direction.SOUTH);

    public JetpackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public BlockEntityType<? extends BacktankBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.JETPACK.get();
    }

    // The loot table carries the air over. Create's backtank puts the rest of the item back, such as its
    // enchantments and name, but only onto Create's own backtank items, so the jetpack does the same for itself.
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BacktankBlockEntity be)) {
            return drops;
        }
        DataComponentPatch components = be.getComponentPatch()
                .forget(type -> type.equals(AllDataComponents.BACKTANK_AIR));
        if (!components.isEmpty()) {
            for (ItemStack stack : drops) {
                if (stack.getItem() instanceof JetpackItem) {
                    stack.applyComponents(components);
                }
            }
        }
        return drops;
    }
}
