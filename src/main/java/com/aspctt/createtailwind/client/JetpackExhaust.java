package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.ModParticles;
import com.aspctt.createtailwind.TailwindClientConfig;
import com.aspctt.createtailwind.jetpack.JetpackNozzles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;

// The sparks a flying jetpack throws from its nozzles. Each client spawns them for the players it sees flying,
// rather than the server sending them, so they cost nothing on the network and each player can turn them off.
public final class JetpackExhaust {
    // Sparks per nozzle per tick while hovering and at full speed. The fraction is spawned as a chance.
    private static final float SPARKS_HOVERING = 0.3F;
    private static final float SPARKS_AT_FULL_SPEED = 1.2F;
    // How fast sparks leave the nozzle downwards, give or take two fifths, and how widely they scatter sideways,
    // in blocks per tick.
    private static final double EJECT_SPEED = 0.15;
    private static final double SCATTER = 0.04;
    // How much of the player's own movement a spark carries away with it.
    private static final double INHERITED_MOTION = 0.5;

    // Nothing may spawn while singleplayer is paused: the particle engine stops ticking, so the sparks would pile
    // up and burst out on unpausing.
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused() || !TailwindClientConfig.EXHAUST_SPARKS.get()) {
            return;
        }
        for (int entityId : JetpackFlight.flyingIds()) {
            if (level.getEntity(entityId) instanceof Player player && !player.isRemoved()) {
                emitSparks(level, player);
            }
        }
    }

    private static void emitSparks(ClientLevel level, Player player) {
        RandomSource random = level.random;
        float rate = SPARKS_HOVERING + (SPARKS_AT_FULL_SPEED - SPARKS_HOVERING) * JetpackFlight.effort(player);
        double dx = player.getX() - player.xo;
        double dy = player.getY() - player.yo;
        double dz = player.getZ() - player.zo;
        JetpackNozzles.forEach(player, (x, y, z) -> {
            int count = (int) rate + (random.nextFloat() < rate - (int) rate ? 1 : 0);
            for (int i = 0; i < count; i++) {
                // Somewhere along the path the nozzle took this tick, so a fast jetpack leaves an even stream
                // rather than clumps a tick apart.
                double back = random.nextDouble();
                level.addParticle(ModParticles.JETPACK_SPARK.get(),
                        x - dx * back, y - dy * back, z - dz * back,
                        dx * INHERITED_MOTION + random.nextGaussian() * SCATTER,
                        dy * INHERITED_MOTION - EJECT_SPEED * (0.6 + random.nextDouble() * 0.8),
                        dz * INHERITED_MOTION + random.nextGaussian() * SCATTER);
            }
        });
    }

    private JetpackExhaust() {
    }
}
