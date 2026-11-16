package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.jetpack.JetpackBlock;
import com.aspctt.createtailwind.jetpack.JetpackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

// Turns the shaft input on top of a placed jetpack, and fills its air gauges as it charges. Create hands its kinetic
// blocks to Flywheel when Flywheel is running and skips this renderer then, but the jetpack has no Flywheel visual,
// so it is always drawn here.
public class JetpackRenderer extends KineticBlockEntityRenderer<BacktankBlockEntity> {
    public JetpackRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BacktankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
            int light, int overlay) {
        SuperByteBuffer shaft = CachedBuffers.partial(ModPartialModels.JETPACK_SHAFT_INPUT, be.getBlockState());
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        // Every jetpack's block entity is this mod's own kind, which knows how full its tank is
        if (!(be instanceof JetpackBlockEntity jetpack) || jetpack.getFullness() <= 0.0F) {
            return;
        }

        // Turned the way the blockstate turns the block model, which rotates the other way round the y axis. Left
        // shaded like the chunk's own faces, which the solid type draws without lighting of its own.
        float yRotation = -be.getBlockState().getValue(JetpackBlock.HORIZONTAL_FACING).toYRot();
        JetpackGauge.fill(be.getBlockState(), yRotation, jetpack.getFullness())
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
