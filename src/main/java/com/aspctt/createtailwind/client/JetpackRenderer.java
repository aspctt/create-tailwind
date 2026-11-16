package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.jetpack.JetpackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

// Turns the shaft input on top of a placed jetpack, and fills its air gauges as it charges, for when Flywheel is not
// drawing it. When Flywheel is running, JetpackVisual draws both instead, as Create's backtank does.
public class JetpackRenderer extends KineticBlockEntityRenderer<BacktankBlockEntity> {
    public JetpackRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BacktankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
            int light, int overlay) {
        // Flywheel already skips this renderer for a block with a visual; Create checks again in its own kinetic
        // renderers, and this does the same
        if (VisualizationManager.supportsVisualization(be.getLevel())) {
            return;
        }

        SuperByteBuffer shaft = CachedBuffers.partial(ModPartialModels.JETPACK_SHAFT_INPUT, be.getBlockState());
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        // Every jetpack's block entity is this mod's own kind, which knows how full its tank is
        if (!(be instanceof JetpackBlockEntity jetpack) || jetpack.getFullness() <= 0.0F) {
            return;
        }

        // Left shaded like the chunk's own faces, which the solid type draws without lighting of its own
        JetpackGauge.fill(be.getBlockState(), JetpackGauge.yRotation(be.getBlockState()), jetpack.getFullness())
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
