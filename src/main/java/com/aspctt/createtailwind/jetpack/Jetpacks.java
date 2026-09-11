package com.aspctt.createtailwind.jetpack;

import java.util.function.Predicate;

import com.aspctt.createtailwind.CreateTailwind;
import com.aspctt.createtailwind.compat.AccessoriesCompat;
import com.aspctt.createtailwind.compat.CuriosCompat;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;

public final class Jetpacks {
    // This mod's modifier on NeoForge's creative flight attribute, present while a jetpack is what lets the
    // player fly.
    public static final ResourceLocation FLIGHT_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, "jetpack_flight");

    private static final boolean CURIOS = ModList.get().isLoaded("curios");
    private static final boolean ACCESSORIES = ModList.get().isLoaded("accessories");

    public static boolean isJetpack(ItemStack stack) {
        return stack.getItem() instanceof JetpackItem;
    }

    // How far behind the body's pivot a worn jetpack rests, in model pixels. It sits on the back, or on the
    // chestplate when one is worn under a jetpack in a back slot, which vanilla draws a pixel out from the body.
    public static int backDepth(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem ? 3 : 2;
    }

    // The jetpack the entity is wearing, preferring one with air left when it wears several. Empty when it wears none.
    public static ItemStack findWorn(LivingEntity entity) {
        ItemStack fuelled = find(entity, stack -> isJetpack(stack) && BacktankUtil.hasAirRemaining(stack));
        return fuelled.isEmpty() ? find(entity, Jetpacks::isJetpack) : fuelled;
    }

    // Whether the entity wears an elytra that can fly right now, in the chest slot or in a Curios or Accessories
    // slot. Elytra Slot and Accessories Compat: Vanilla, which let a glide start from those slots, both judge an
    // elytra by canElytraFly as vanilla does, so a broken elytra counts in none of them.
    public static boolean hasElytra(LivingEntity entity) {
        return !find(entity, stack -> stack.canElytraFly(entity)).isEmpty();
    }

    // Whether the player's jetpack boosts an elytra glide rather than flying on its own: it has air left, and an
    // elytra is worn alongside it. A jetpack does nothing for creative and spectator players, who fly by their game
    // mode. Both sides can answer this, since the player's own client is sent its equipment and the air in it.
    public static boolean isBooster(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }
        ItemStack jetpack = findWorn(player);
        return !jetpack.isEmpty() && BacktankUtil.hasAirRemaining(jetpack) && hasElytra(player);
    }

    // Flying on a jetpack rather than by creative or spectator flight. Both sides can answer this, since the
    // creative flight attribute and its modifiers are synced to the player's own client.
    public static boolean isJetpackFlying(Player player) {
        if (!player.getAbilities().flying || player.isCreative() || player.isSpectator()) {
            return false;
        }
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        return flight != null && flight.hasModifier(FLIGHT_MODIFIER_ID);
    }

    // Chest slot first, then Curios, then Accessories.
    private static ItemStack find(LivingEntity entity, Predicate<ItemStack> filter) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (filter.test(chest)) {
            return chest;
        }
        if (CURIOS) {
            ItemStack stack = CuriosCompat.findFirst(entity, filter);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        if (ACCESSORIES) {
            ItemStack stack = AccessoriesCompat.findFirst(entity, filter);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private Jetpacks() {
    }
}
