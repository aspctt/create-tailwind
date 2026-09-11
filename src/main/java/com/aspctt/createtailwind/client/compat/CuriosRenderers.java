package com.aspctt.createtailwind.client.compat;

import com.aspctt.createtailwind.ModItems;
import com.aspctt.createtailwind.client.WornJetpackRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// Only loaded when Curios is, so nothing outside this class may reference the Curios API.
public final class CuriosRenderers {
    private static final ICurioRenderer JETPACK = new JetpackCurioRenderer();

    public static void register() {
        CuriosRendererRegistry.register(ModItems.JETPACK.get(), () -> JETPACK);
    }

    // Curios has no counterpart to Accessories' transform API, so the jetpack follows the rendered body's pivot
    // exactly as the chest-slot layer does.
    private static final class JetpackCurioRenderer implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
                PoseStack ms, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> model)) {
                return;
            }
            ms.pushPose();
            model.body.translateAndRotate(ms);
            ms.translate(WornJetpackRenderer.OFFSET_X, WornJetpackRenderer.OFFSET_Y,
                    WornJetpackRenderer.offsetZ(slotContext.entity()));
            WornJetpackRenderer.render(ms, buffer, light, slotContext.entity(), stack);
            ms.popPose();
        }
    }

    private CuriosRenderers() {
    }
}
