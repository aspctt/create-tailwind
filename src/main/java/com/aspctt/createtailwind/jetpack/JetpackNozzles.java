package com.aspctt.createtailwind.jetpack;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

// Where the exhaust leaves a worn jetpack: the mouths of its two nozzles, as the jetpack is drawn on the body. In
// model pixels, the nozzles end 10 below the body's pivot, 2.5 behind the front of the tanks, which rests on the
// back, and 4 to either side. PlayerRenderer draws the model at 15/16 scale and LivingEntityRenderer lifts it by
// 1.501, both before the entity's own scale.
public final class JetpackNozzles {
    private static final double PLAYER_MODEL_SCALE = 0.9375;
    private static final double NOZZLE_HEIGHT = PLAYER_MODEL_SCALE * (1.501 - 10 / 16.0);
    private static final double NOZZLE_BEHIND_FRONT = 2.5;
    private static final double NOZZLE_SIDE = PLAYER_MODEL_SCALE * 4 / 16.0;

    @FunctionalInterface
    public interface Visitor {
        void accept(double x, double y, double z);
    }

    // Visits each nozzle mouth at the entity's position this tick. The jetpack turns with the body rather than the
    // head, so the nozzles follow the body's yaw: ahead of the body is (-sin, cos) of that yaw, and across it is
    // (cos, sin). A gliding body is also tipped forward, which swings the nozzles round to trail behind it.
    public static void forEach(LivingEntity entity, Visitor visitor) {
        double scale = entity.getScale();
        double yaw = Math.toRadians(entity.yBodyRot);
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double tilt = glideTilt(entity);
        double tiltSin = Math.sin(tilt);
        double tiltCos = Math.cos(tilt);
        double height = NOZZLE_HEIGHT * scale;
        double behind = PLAYER_MODEL_SCALE * (Jetpacks.backDepth(entity) + NOZZLE_BEHIND_FRONT) / 16.0 * scale;
        double side = NOZZLE_SIDE * scale;
        // Up the body is straight up, tipped forward by the tilt. Out of the back is straight behind, tipped up by it.
        double x = entity.getX() + (-sin * tiltSin) * height + (sin * tiltCos) * behind;
        double y = entity.getY() + tiltCos * height + tiltSin * behind;
        double z = entity.getZ() + (cos * tiltSin) * height + (-cos * tiltCos) * behind;
        visitor.accept(x - cos * side, y, z - sin * side);
        visitor.accept(x + cos * side, y, z + sin * side);
    }

    // The way the nozzles point: down the body, towards the feet. Straight down when standing, and back along the
    // glide when gliding.
    public static Vec3 exhaustDirection(LivingEntity entity) {
        double yaw = Math.toRadians(entity.yBodyRot);
        double tilt = glideTilt(entity);
        double tiltSin = Math.sin(tilt);
        return new Vec3(Math.sin(yaw) * tiltSin, -Math.cos(tilt), -Math.cos(yaw) * tiltSin);
    }

    // How far a gliding body is tipped forward, in radians, as PlayerRenderer#setupRotations tips it: about the
    // feet, until the body lies along where the player looks, easing in over the first ten ticks of the glide.
    private static double glideTilt(LivingEntity entity) {
        if (!entity.isFallFlying()) {
            return 0.0;
        }
        float ticks = entity.getFallFlyingTicks();
        float ease = Mth.clamp(ticks * ticks / 100.0F, 0.0F, 1.0F);
        return Math.toRadians(ease * (90.0F + entity.getXRot()));
    }

    private JetpackNozzles() {
    }
}
