package com.aspctt.createtailwind.jetpack;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// Create's backtank block entity, which charges from rotation and keeps the air, enchantments and name of the item it
// was placed from, with what the air gauges on a placed jetpack need on top. Create only tells clients the air level
// once the tank is full, so the gauges would sit still while it charges and then jump; this also tells them each
// time the gauges' fill moves a step. And it keeps its own copy of the Capacity level, which Create keeps private, to
// work out how full the tank is.
public class JetpackBlockEntity extends BacktankBlockEntity {
    // How many steps the gauges' fill takes from empty to full. Each step is one update sent to the clients nearby,
    // so a whole charge sends a few dozen at most.
    private static final int GAUGE_STEPS = 28;

    private int capacityLevel;

    public JetpackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // How full the tank is, from 0 to 1. The client's answer is as fresh as the last update it was sent.
    public float getFullness() {
        return Mth.clamp(airLevel / (float) BacktankUtil.maxAir(capacityLevel), 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        int before = gaugeStep();
        super.tick();
        if (level == null || level.isClientSide()) {
            return;
        }

        // Create sends its own update on filling up, so the last step is left to that
        int after = gaugeStep();
        if (after != before && after < GAUGE_STEPS) {
            sendData();
        }
    }

    @Override
    public void setCapacityEnchantLevel(int capacityEnchantLevel) {
        super.setCapacityEnchantLevel(capacityEnchantLevel);
        capacityLevel = capacityEnchantLevel;
    }

    // Create writes the Capacity level under this key in every save and update, so it is read back here on both sides
    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        capacityLevel = compound.getInt("CapacityEnchantment");
    }

    private int gaugeStep() {
        return (int) (getFullness() * GAUGE_STEPS);
    }
}
