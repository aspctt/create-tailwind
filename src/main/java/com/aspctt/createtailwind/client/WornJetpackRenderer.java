package com.aspctt.createtailwind.client;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.aspctt.createtailwind.CreateTailwind;
import com.aspctt.createtailwind.ModBlocks;
import com.aspctt.createtailwind.TailwindClientConfig;
import com.aspctt.createtailwind.jetpack.JetpackBlock;
import com.aspctt.createtailwind.jetpack.Jetpacks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.InventoryMenu;
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

    // How long the flame is drawn, as a multiple of its model, while hovering and at full speed, and how far its
    // length flickers either way.
    private static final float FLAME_HOVERING = 0.55F;
    private static final float FLAME_AT_FULL_SPEED = 1.3F;
    private static final float FLAME_FLICKER = 0.08F;

    // The nozzle openings' colour as they cool, hottest first, evenly spaced from flight (heat 1) to cold (heat 0):
    // the flame's white, then hot metal's ember colours, then the black of the cold opening under the glow. The glow
    // texture darkens and warms this towards the rim, so the middle of each opening stays the hottest.
    private static final int[] NOZZLE_GLOW = {0xE8ECF2, 0xFFD696, 0xF58C3C, 0x962D14, 0x000000};

    // The halo round each glowing opening stands in for bloom, which neither Minecraft nor Create draws. Where the
    // nozzles are in the block model, in pixels: their axes and the plane of their mouths.
    private static final float[] NOZZLE_AXIS_X = {4, 12};
    private static final float NOZZLE_AXIS_Z = 8;
    private static final float NOZZLE_MOUTH_Y = 0;
    // The halo's half width, and where it sits: a little below the mouth, and pulled towards the camera past the
    // nozzle's own surface, which would otherwise cut a hard edge across the halo where it passes into the metal.
    // All in pixels.
    private static final float HALO_RADIUS = 5;
    private static final float HALO_BELOW_MOUTH = 1;
    private static final float HALO_TOWARDS_CAMERA = 2.5F;
    // How bright the halo is against the opening's own colour
    private static final float HALO_STRENGTH = 0.6F;
    private static final ResourceLocation HALO_TEXTURE = CreateTailwind.id("textures/misc/jetpack_nozzle_halo.png");

    // Z puts the tanks against the back, or against a chestplate under them, so no gap shows between the jetpack
    // and the player from above.
    public static float offsetZ(LivingEntity entity) {
        return 1 - (FRONT_Z - Jetpacks.backDepth(entity)) / 16f;
    }

    // Draws the jetpack's block model with its corner at the current origin, which the caller has placed at the
    // offset above, its gauges filled to the air in it, its flame while it flies, and its nozzle openings glowing
    // while they cool. Facing north, the block model is turned half way round, as Create turns its backtank, and the
    // flip below then brings the harness against the body. The shaft input is left off.
    public static void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, ItemStack stack) {
        if (entity.getPose() == Pose.SLEEPING) {
            return;
        }

        // Not Create's choice of the cutout block sheet: that one has a buffer of its own that LevelRenderer only
        // draws after every shared buffer, the flame's included, so the black nozzle openings were drawn last and
        // painted over the glow. This type shares the common buffer, which is drawn as soon as the flame's type is
        // asked for below, so the body is down, depth and all, before anything see-through goes over it.
        VertexConsumer vc = ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS),
                false, stack.hasFoil());
        BlockState renderedState = ModBlocks.JETPACK.get().defaultBlockState()
                .setValue(JetpackBlock.HORIZONTAL_FACING, Direction.NORTH);

        ms.pushPose();
        ms.scale(1, -1, -1);
        CachedBuffers.block(renderedState)
                .disableDiffuse()
                .light(light)
                .renderInto(ms, vc);
        // Lit and drawn like the body, glint and all, so the fill reads as part of it
        float fullness = JetpackGauge.fullness(stack);
        if (fullness > 0.0F) {
            JetpackGauge.fill(renderedState, 180, fullness)
                    .disableDiffuse()
                    .light(light)
                    .renderInto(ms, vc);
        }
        if (TailwindClientConfig.EXHAUST_FLAME.get()) {
            if (JetpackFlight.isFlying(entity)) {
                renderFlame(ms, buffer, entity, renderedState);
            }
            float heat = JetpackFlight.nozzleHeat(entity);
            if (heat > 0.0F) {
                int colour = nozzleColour(heat);
                renderNozzleGlow(ms, buffer, colour, renderedState);
                renderNozzleHalos(ms, buffer, colour);
            }
        }
        ms.popPose();
    }

    // The openings' colour at this heat, between the two colours of the ramp either side of it
    private static int nozzleColour(float heat) {
        float position = (1.0F - heat) * (NOZZLE_GLOW.length - 1);
        int index = Math.min((int) position, NOZZLE_GLOW.length - 2);
        return FastColor.ARGB32.lerp(position - index, NOZZLE_GLOW[index], NOZZLE_GLOW[index + 1]);
    }

    // Lights the nozzle openings, which the flat flame leaves dark from straight below. The glow model's texture is
    // white in the middle, so the tint is its colour there, and it is drawn emissive like the flame so it glows in
    // the dark. At heat 0 the tint is black, matching the opening it covers, so it can stop being drawn without a
    // visible change.
    //
    // It shares the flame's type rather than having one of its own: drawn with Create's glowing type instead, it
    // turned the flame's see-through edges black.
    private static void renderNozzleGlow(PoseStack ms, MultiBufferSource buffer, int colour, BlockState state) {
        CachedBuffers.partial(ModPartialModels.JETPACK_NOZZLE_GLOW, state)
                .center()
                .rotateYDegrees(180)
                .uncenter()
                .color(colour)
                .disableDiffuse()
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, buffer.getBuffer(RenderType.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS)));
    }

    // Draws a soft halo facing the camera round each opening, the way bloom spreads a bright light over what is
    // around it. Vanilla's eyes type adds its colour to what is already drawn rather than covering it, so the halo
    // brightens the nozzle and the sky behind without ever darkening them, and black adds nothing. It fades with the
    // opening's colour, and with how squarely the camera looks into the downward facing opening: from the side or
    // from above the opening is out of sight, and a halo round it would glow from nowhere.
    //
    // The halo is built where the pose puts the mouth, then turned to the camera there. The pose can't turn it: by
    // then it holds the body's own turn, and the camera's turn comes after it. The world's poses start out unturned
    // at the camera, which is what particles rely on too, so the camera's turn alone faces it to the camera.
    private static void renderNozzleHalos(PoseStack ms, MultiBufferSource buffer, int colour) {
        Matrix4f pose = ms.last().pose();
        Quaternionf facing = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        Vector3f towardsCamera = new Vector3f(0, 0, 1).rotate(facing);
        float visible = pose.transformDirection(0, -1, 0, new Vector3f()).normalize().dot(towardsCamera);
        if (visible <= 0.0F) {
            return;
        }

        // The pose carries the player model's scale, which the halo, built outside it, takes on by hand
        float pixel = pose.getScale(new Vector3f()).x / 16.0F;
        float brightness = HALO_STRENGTH * visible;
        int red = Math.round(FastColor.ARGB32.red(colour) * brightness);
        int green = Math.round(FastColor.ARGB32.green(colour) * brightness);
        int blue = Math.round(FastColor.ARGB32.blue(colour) * brightness);
        Vector3f across = new Vector3f(HALO_RADIUS * pixel, 0, 0).rotate(facing);
        Vector3f up = new Vector3f(0, HALO_RADIUS * pixel, 0).rotate(facing);
        Vector3f forward = new Vector3f(towardsCamera).mul(HALO_TOWARDS_CAMERA * pixel);

        VertexConsumer vc = buffer.getBuffer(RenderType.eyes(HALO_TEXTURE));
        for (float axisX : NOZZLE_AXIS_X) {
            Vector3f centre = pose.transformPosition(axisX / 16.0F, (NOZZLE_MOUTH_Y - HALO_BELOW_MOUTH) / 16.0F,
                    NOZZLE_AXIS_Z / 16.0F, new Vector3f()).add(forward);
            // Both windings: the type culls back faces, and which way round faces the camera depends on whether the
            // pose mirrors, as the inventory screen's does. Exactly one of the two is ever drawn.
            haloVertex(vc, centre, across, up, 1, -1, 1, 1, red, green, blue);
            haloVertex(vc, centre, across, up, 1, 1, 1, 0, red, green, blue);
            haloVertex(vc, centre, across, up, -1, 1, 0, 0, red, green, blue);
            haloVertex(vc, centre, across, up, -1, -1, 0, 1, red, green, blue);
            haloVertex(vc, centre, across, up, -1, -1, 0, 1, red, green, blue);
            haloVertex(vc, centre, across, up, -1, 1, 0, 0, red, green, blue);
            haloVertex(vc, centre, across, up, 1, 1, 1, 0, red, green, blue);
            haloVertex(vc, centre, across, up, 1, -1, 1, 1, red, green, blue);
        }
    }

    // One corner of a halo, the given multiples of its half widths across and up from its centre. The eyes type uses
    // the entity vertex format, so the overlay, light and normal it ignores still have to be filled in.
    private static void haloVertex(VertexConsumer vc, Vector3f centre, Vector3f across, Vector3f up, float right,
            float upward, float u, float v, int red, int green, int blue) {
        vc.addVertex(centre.x + across.x * right + up.x * upward, centre.y + across.y * right + up.y * upward,
                        centre.z + across.z * right + up.z * upward)
                .setColor(red, green, blue, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 1, 0);
    }

    // The flame model hangs from the nozzle mouths, which sit at the bottom of the block model, so stretching it
    // from there lengthens it with speed. It is turned with the block model to stay on the nozzles, and drawn
    // see-through at full brightness from both sides, so it glows in any light.
    private static void renderFlame(PoseStack ms, MultiBufferSource buffer, LivingEntity entity, BlockState state) {
        float length = FLAME_HOVERING + (FLAME_AT_FULL_SPEED - FLAME_HOVERING) * JetpackFlight.effort(entity);
        float time = AnimationTickHolder.getRenderTime(entity.level());
        length *= 1.0F + FLAME_FLICKER * Mth.sin(time * 2.3F + entity.getId());
        CachedBuffers.partial(ModPartialModels.JETPACK_FLAME, state)
                .center()
                .rotateYDegrees(180)
                .uncenter()
                .scale(1, length, 1)
                .disableDiffuse()
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, buffer.getBuffer(RenderType.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS)));
    }

    private WornJetpackRenderer() {
    }
}
