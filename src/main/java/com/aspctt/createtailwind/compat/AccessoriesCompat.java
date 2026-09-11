package com.aspctt.createtailwind.compat;

import java.util.function.Predicate;

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

    private AccessoriesCompat() {
    }
}
