package com.aspctt.createtailwind.client.compat;

import com.aspctt.createtailwind.client.WornBacktankRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;

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
    private static final ICurioRenderer BACKTANK = new BacktankCurioRenderer();

    public static void register() {
        CuriosRendererRegistry.register(AllItems.COPPER_BACKTANK.get(), () -> BACKTANK);
        CuriosRendererRegistry.register(AllItems.NETHERITE_BACKTANK.get(), () -> BACKTANK);
    }

    // Curios has no counterpart to Accessories' transform API, so the tank follows the rendered body's pivot
    // exactly as Create's chest-slot layer does.
    private static final class BacktankCurioRenderer implements ICurioRenderer {
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
            ms.translate(WornBacktankRenderer.OFFSET_X, WornBacktankRenderer.OFFSET_Y, WornBacktankRenderer.OFFSET_Z);
            WornBacktankRenderer.render(ms, buffer, light, slotContext.entity(), stack);
            ms.popPose();
        }
    }

    private CuriosRenderers() {
    }
}
