package com.aspctt.createtailwind.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

// Turns the shaft input on top of a placed jetpack. Create hands its kinetic blocks to Flywheel when Flywheel is
// running and skips this renderer then, but the jetpack has no Flywheel visual, so it is always drawn here.
public class JetpackRenderer extends KineticBlockEntityRenderer<BacktankBlockEntity> {
    public JetpackRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BacktankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
            int light, int overlay) {
        SuperByteBuffer shaft = CachedBuffers.partial(ModPartialModels.JETPACK_SHAFT_INPUT, be.getBlockState());
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
