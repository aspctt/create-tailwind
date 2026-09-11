package com.aspctt.createtailwind.jetpack;

import net.minecraft.world.entity.LivingEntity;

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
    // head, so the nozzles follow the body's yaw: behind the body is (sin, -cos) of that yaw, and across it is
    // (cos, sin).
    public static void forEach(LivingEntity entity, Visitor visitor) {
        double scale = entity.getScale();
        double yaw = Math.toRadians(entity.yBodyRot);
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double behind = PLAYER_MODEL_SCALE * (Jetpacks.backDepth(entity) + NOZZLE_BEHIND_FRONT) / 16.0 * scale;
        double side = NOZZLE_SIDE * scale;
        double x = entity.getX() + sin * behind;
        double y = entity.getY() + NOZZLE_HEIGHT * scale;
        double z = entity.getZ() - cos * behind;
        visitor.accept(x - cos * side, y, z - sin * side);
        visitor.accept(x + cos * side, y, z + sin * side);
    }

    private JetpackNozzles() {
    }
}
