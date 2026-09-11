package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.jetpack.JetpackItem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// Draws a jetpack worn in the chest slot on the body's back. It is not armour, so nothing else draws it.
public class JetpackLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public JetpackLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing,
            float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!(stack.getItem() instanceof JetpackItem) || !(getParentModel() instanceof HumanoidModel<?> model)) {
            return;
        }
        ms.pushPose();
        model.body.translateAndRotate(ms);
        ms.translate(WornJetpackRenderer.OFFSET_X, WornJetpackRenderer.OFFSET_Y, WornJetpackRenderer.offsetZ(entity));
        WornJetpackRenderer.render(ms, buffer, light, entity, stack);
        ms.popPose();
    }

    // Every renderer with a humanoid body gets the layer, players and armour stands included, as Create does
    // for its backtank.
    public static void addToAll(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            addTo(event.getSkin(skin));
        }
        for (EntityType<?> type : event.getEntityTypes()) {
            addTo(event.getRenderer(type));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addTo(EntityRenderer<?> renderer) {
        if (renderer instanceof LivingEntityRenderer<?, ?> living && living.getModel() instanceof HumanoidModel) {
            living.addLayer(new JetpackLayer(living));
        }
    }
}
