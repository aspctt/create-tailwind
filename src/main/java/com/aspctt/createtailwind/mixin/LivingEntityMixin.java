package com.aspctt.createtailwind.mixin;

import com.aspctt.createtailwind.client.JetpackBoost;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

// Adapted from Do a Barrel Roll by enjarai (github.com/enjarai/do-a-barrel-roll), by way of its NeoForge port Do
// Another Barrel Roll, and used under the GNU General Public License v3.0.
//
// Adds the jetpack's boost where travel sets a glide's velocity: the first setDeltaMovement after the isFallFlying
// check, whose argument ends in multiply(0.99, 0.98, 0.99). Do a Barrel Roll adds its own thrust to the same call,
// and BarrelRollCompat keeps the two from adding together. Client-side, since the player's own client moves them.
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyArg(
            method = "travel",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private Vec3 createtailwind$boostGlide(Vec3 velocity) {
        if ((Object) this instanceof LocalPlayer player && JetpackBoost.isBoosting(player)) {
            return velocity.add(JetpackBoost.thrust(player));
        }
        return velocity;
    }
}
