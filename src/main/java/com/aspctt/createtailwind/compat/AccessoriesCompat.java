package com.aspctt.createtailwind.compat;

import java.util.List;
import java.util.function.Predicate;

import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Only loaded when Accessories is, so nothing outside this class may reference the Accessories API.
public final class AccessoriesCompat {
    public static ItemStack findFirst(LivingEntity entity, Predicate<ItemStack> filter) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return ItemStack.EMPTY;
        }
        SlotEntryReference equipped = capability.getFirstEquipped(filter);
        return equipped == null ? ItemStack.EMPTY : equipped.stack();
    }

    // Lets Create's air-using equipment, such as the diving helmet, draw on a jetpack worn in an Accessories slot.
    // Create's own Curios support does the same for Curios slots, and Create has no Accessories support.
    public static void registerAirSource() {
        Predicate<ItemStack> airSource = AllItemTags.PRESSURIZED_AIR_SOURCES::matches;
        BacktankUtil.addBacktankSupplier(entity -> {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);
            if (capability == null) {
                return List.of();
            }
            return capability.getEquipped(airSource).stream().map(SlotEntryReference::stack).toList();
        });
    }

    private AccessoriesCompat() {
    }
}
