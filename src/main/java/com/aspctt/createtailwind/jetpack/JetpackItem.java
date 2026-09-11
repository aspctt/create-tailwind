package com.aspctt.createtailwind.jetpack;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

// The jetpack. It holds compressed air the way Create's backtank does, under the same data component, so Create's
// charging, air bar, Capacity enchantment and air-using equipment all work with it. It goes in the chest slot like
// an elytra rather than as armour, so it gives no protection and draws no armour texture over the body.
public class JetpackItem extends BlockItem implements Equipable {
    public JetpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return AllSoundEvents.COPPER_ARMOR_EQUIP.getMainEventHolder();
    }

    // Used on a block it is set down there, as a block item; used on nothing it is put on.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    // The copper backtank's, which it takes from Create's copper armour material.
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 7;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * Mth.clamp(BacktankUtil.getAir(stack) / (float) BacktankUtil.maxAir(stack), 0, 1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankItem.BAR_COLOR;
    }
}
