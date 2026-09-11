package com.aspctt.createtailwind.mixin;

import com.aspctt.createtailwind.jetpack.Jetpacks;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Jetpack flight stays at the walking flying speed. Sprinting would double it (Player#getFlyingSpeed) and widen
// the field of view, so it may not start while jetpack flying, and a sprint carried into the air is dropped.
// Movement is simulated on the client, which is why this is client-side.
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void createtailwind$noSprintOnJetpack(CallbackInfoReturnable<Boolean> cir) {
        if (Jetpacks.isJetpackFlying((LocalPlayer) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    // Just before movement is applied, so it also catches the tick flight was switched on, which comes after the
    // sprint checks in aiStep.
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;aiStep()V"))
    private void createtailwind$stopSprintOnJetpack(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isSprinting() && Jetpacks.isJetpackFlying(player)) {
            player.setSprinting(false);
        }
    }
}
