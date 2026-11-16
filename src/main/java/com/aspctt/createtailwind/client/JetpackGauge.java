package com.aspctt.createtailwind.client;

import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

// The white fill in the air gauges on a jetpack's sides, which rises and falls with the air in the tank. The frames
// and glass are part of the block model, which is baked once, so only the fill is drawn here, for both a worn and a
// placed jetpack.
public final class JetpackGauge {
    // The bottom of the gauges' glass in the block model, in blocks, which the fill grows up from. The generator's
    // GAUGE_Y holds the same height in pixels.
    private static final float FILL_BOTTOM = 9.0F / 16.0F;

    // How full a jetpack item is, from 0 to 1
    public static float fullness(ItemStack stack) {
        return Mth.clamp(BacktankUtil.getAir(stack) / (float) BacktankUtil.maxAir(stack), 0.0F, 1.0F);
    }

    // The fill for a tank this full, turned with the block model by the given degrees about its middle, ready for the
    // caller to light and draw. Callers skip an empty tank, whose gauges show bare glass. The fill is shaded across
    // only, so squashing it leaves its texture looking the same.
    public static SuperByteBuffer fill(BlockState state, float yRotation, float fullness) {
        return CachedBuffers.partial(ModPartialModels.JETPACK_GAUGE_FILL, state)
                .center()
                .rotateYDegrees(yRotation)
                .uncenter()
                .translateY(FILL_BOTTOM)
                .scale(1.0F, fullness, 1.0F)
                .translateY(-FILL_BOTTOM);
    }

    private JetpackGauge() {
    }
}
