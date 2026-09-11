package com.aspctt.createtailwind.client.compat;

import com.aspctt.createtailwind.ModItems;
import com.aspctt.createtailwind.client.WornJetpackRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Only loaded when Accessories is, so nothing outside this class may reference the Accessories API.
public final class AccessoriesRenderers {
    private static final AccessoryRenderer JETPACK = new JetpackAccessoryRenderer();

    // How far the back of the vanilla body is behind its pivot, in blocks. The jetpack's offset is measured from
    // the pivot, and the jetpack here is anchored to the back instead.
    private static final float VANILLA_BACK_DEPTH = 2 / 16f;

    public static void register() {
        AccessoriesRendererRegistry.registerRenderer(ModItems.JETPACK.get(), () -> JETPACK);
    }

    private static final class JetpackAccessoryRenderer implements AccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack ms,
                EntityModel<M> model, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount,
                float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!(model instanceof HumanoidModel<?> humanoid)) {
                return;
            }
            // Anchored to the top centre of the body's back through Accessories' transform API, which measures the
            // part as it is actually rendered. A resource pack that reshapes the body, such as Fresh Animations
            // through EMF, still gets the jetpack on its back rather than floating where the vanilla body would be.
            AccessoryRenderer.transformToModelPart(ms, humanoid.body, 0, 1, -1);
            // The API leaves the pose at half a block per unit and turned over on X. Undoing both puts it back in
            // blocks and the body's own orientation, which is what the jetpack's offset is written in.
            ms.mulPose(Axis.XP.rotationDegrees(180));
            ms.scale(2, 2, 2);
            ms.translate(WornJetpackRenderer.OFFSET_X, WornJetpackRenderer.OFFSET_Y,
                    WornJetpackRenderer.OFFSET_Z - VANILLA_BACK_DEPTH);
            WornJetpackRenderer.render(ms, buffer, light, reference.entity(), stack);
        }
    }

    private AccessoriesRenderers() {
    }
}
