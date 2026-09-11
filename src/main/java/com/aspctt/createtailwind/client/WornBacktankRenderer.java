package com.aspctt.createtailwind.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

// Draws a backtank on an entity's back the way Create draws one worn in the chest slot, for backtanks worn in a
// Curios or Accessories slot, which Create's own layer does not look at. Adapted from Create's BacktankArmorLayer,
// used under the MIT License. The licence text is in NOTICE.
public final class WornBacktankRenderer {
    // Where Create's layer puts the tank relative to the body's pivot, in blocks.
    public static final float OFFSET_X = -1 / 2f;
    public static final float OFFSET_Y = 10 / 16f;
    public static final float OFFSET_Z = 1f;

    // Draws the tank with its corner at the current origin, which the caller has placed at the offset above.
    public static void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, ItemStack stack) {
        if (entity.getPose() == Pose.SLEEPING || !(stack.getItem() instanceof BacktankItem item)) {
            return;
        }

        VertexConsumer vc = ItemRenderer.getFoilBuffer(buffer, Sheets.cutoutBlockSheet(), false, stack.hasFoil());
        BlockState renderedState = item.getBlock().defaultBlockState()
                .setValue(BacktankBlock.HORIZONTAL_FACING, Direction.SOUTH);
        SuperByteBuffer backtank = CachedBuffers.block(renderedState);
        SuperByteBuffer cogs = CachedBuffers.partial(BacktankRenderer.getCogsModel(renderedState), renderedState);
        SuperByteBuffer nob = CachedBuffers.partial(BacktankRenderer.getShaftModel(renderedState), renderedState);

        ms.pushPose();
        ms.scale(1, -1, -1);

        backtank.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        nob.disableDiffuse()
                .translate(0, -3f / 16, 0)
                .light(light)
                .renderInto(ms, vc);

        cogs.center()
                .rotateYDegrees(180)
                .uncenter()
                .translate(0, 6.5f / 16, 11f / 16)
                .rotate(AngleHelper.rad(2 * AnimationTickHolder.getRenderTime(entity.level()) % 360), Direction.EAST)
                .translate(0, -6.5f / 16, -11f / 16);

        cogs.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();
    }

    private WornBacktankRenderer() {
    }
}
