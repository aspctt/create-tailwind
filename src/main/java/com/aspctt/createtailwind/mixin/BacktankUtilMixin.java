package com.aspctt.createtailwind.mixin;

import com.aspctt.createtailwind.jetpack.JetpackAirWarning;
import com.aspctt.createtailwind.jetpack.Jetpacks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Create warns "Backtank pressure low" whenever air drawn from any tank runs low, and a jetpack keeps its air the
// same way. When the air came from a jetpack, whether for flight or for Create's own equipment, the jetpack's
// wording is shown instead. Should a Create update change the call, the injection is skipped rather than failing,
// and Create's wording shows as before.
@Mixin(BacktankUtil.class)
public abstract class BacktankUtilMixin {
    @WrapOperation(method = "consumeAir", require = 0, at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/equipment/armor/BacktankUtil;sendWarning(Lnet/minecraft/server/level/ServerPlayer;FFF)V"))
    private static void createtailwind$jetpackWarning(ServerPlayer player, float air, float newAir, float threshold,
            Operation<Void> original, @Local(argsOnly = true) ItemStack backtank) {
        if (Jetpacks.isJetpack(backtank)) {
            JetpackAirWarning.send(player, air, newAir, threshold);
        } else {
            original.call(player, air, newAir, threshold);
        }
    }
}
