package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.ModBlocks;
import com.aspctt.createtailwind.jetpack.JetpackBlock;
import com.aspctt.createtailwind.jetpack.Jetpacks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

// Draws a jetpack on an entity's back, for the chest slot and for Curios and Accessories slots. Adapted from
// Create's BacktankArmorLayer, used under the MIT License. The licence text is in NOTICE.
public final class WornJetpackRenderer {
    // Where the jetpack goes relative to the body's pivot, in blocks. X and Y are Create's backtank offset.
    public static final float OFFSET_X = -1 / 2f;
    public static final float OFFSET_Y = 10 / 16f;
    // How far into the block model the tanks start, in pixels: the fronts of their collars and bands, the widest
    // parts. The harness starts further forward, and sinks into the body out of sight.
    private static final float FRONT_Z = 5.5f;

    // Z puts the tanks against the back, or against a chestplate under them, so no gap shows between the jetpack
    // and the player from above.
    public static float offsetZ(LivingEntity entity) {
        return 1 - (FRONT_Z - Jetpacks.backDepth(entity)) / 16f;
    }

    // Draws the jetpack's block model with its corner at the current origin, which the caller has placed at the
    // offset above. Facing north, the block model is turned half way round, as Create turns its backtank, and the
    // flip below then brings the harness against the body. The shaft input is left off.
    public static void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, ItemStack stack) {
        if (entity.getPose() == Pose.SLEEPING) {
            return;
        }

        VertexConsumer vc = ItemRenderer.getFoilBuffer(buffer, Sheets.cutoutBlockSheet(), false, stack.hasFoil());
        BlockState renderedState = ModBlocks.JETPACK.get().defaultBlockState()
                .setValue(JetpackBlock.HORIZONTAL_FACING, Direction.NORTH);

        ms.pushPose();
        ms.scale(1, -1, -1);
        CachedBuffers.block(renderedState)
                .disableDiffuse()
                .light(light)
                .renderInto(ms, vc);
        ms.popPose();
    }

    private WornJetpackRenderer() {
    }
}
