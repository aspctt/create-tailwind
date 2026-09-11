package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.ModParticles;
import com.aspctt.createtailwind.TailwindClientConfig;
import com.aspctt.createtailwind.jetpack.JetpackNozzles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

// The exhaust this client spawns itself. The sparks a flying jetpack throws from its nozzles, for every player it
// sees flying, rather than the server sending them, so they cost nothing on the network and each player can turn
// them off. And the smoke of this client's own player, which the server sends to everyone else.
//
// Both come out of the nozzles where the camera sees them. In first person that means placing a gliding body the
// way the camera sits in it, as JetpackNozzles explains, or they would spawn in front of the camera.
public final class JetpackExhaust {
    // Sparks per nozzle per tick while hovering and at full speed. The fraction is spawned as a chance.
    private static final float SPARKS_HOVERING = 0.1F;
    private static final float SPARKS_AT_FULL_SPEED = 0.4F;
    // How fast sparks leave the nozzle, give or take two fifths, and how widely they scatter sideways, in blocks
    // per tick.
    private static final double EJECT_SPEED = 0.15;
    private static final double SCATTER = 0.04;
    // How much of the player's own movement a spark carries away with it.
    private static final double INHERITED_MOTION = 0.5;

    // Nothing may spawn while singleplayer is paused: the particle engine stops ticking, so the particles would pile
    // up and burst out on unpausing.
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) {
            return;
        }
        boolean sparks = TailwindClientConfig.EXHAUST_SPARKS.get();
        for (int entityId : JetpackFlight.flyingIds()) {
            if (!(level.getEntity(entityId) instanceof Player player) || player.isRemoved()) {
                continue;
            }
            boolean fromEyes = player == minecraft.getCameraEntity() && minecraft.options.getCameraType().isFirstPerson();
            if (sparks) {
                emitSparks(level, player, fromEyes);
            }
            if (player == minecraft.player) {
                emitSmoke(level, player, fromEyes);
            }
        }
    }

    // The same puff per nozzle per tick the server sends everyone else, going through the same provider and so the
    // same particle settings. It also starts from where this client has moved the player, rather than where the
    // server last heard they were.
    private static void emitSmoke(ClientLevel level, Player player, boolean fromEyes) {
        JetpackNozzles.forEach(player, fromEyes, (x, y, z) ->
                level.addParticle(ModParticles.JETPACK_SMOKE.get(), x, y, z, 0, 0, 0));
    }

    private static void emitSparks(ClientLevel level, Player player, boolean fromEyes) {
        RandomSource random = level.random;
        float rate = SPARKS_HOVERING + (SPARKS_AT_FULL_SPEED - SPARKS_HOVERING) * JetpackFlight.effort(player);
        double dx = player.getX() - player.xo;
        double dy = player.getY() - player.yo;
        double dz = player.getZ() - player.zo;
        // Out of the nozzles: down when flying upright, back along the glide when boosting an elytra.
        Vec3 out = JetpackNozzles.exhaustDirection(player);
        JetpackNozzles.forEach(player, fromEyes, (x, y, z) -> {
            int count = (int) rate + (random.nextFloat() < rate - (int) rate ? 1 : 0);
            for (int i = 0; i < count; i++) {
                // Somewhere along the path the nozzle took this tick, so a fast jetpack leaves an even stream
                // rather than clumps a tick apart.
                double back = random.nextDouble();
                double eject = EJECT_SPEED * (0.6 + random.nextDouble() * 0.8);
                level.addParticle(ModParticles.JETPACK_SPARK.get(),
                        x - dx * back, y - dy * back, z - dz * back,
                        dx * INHERITED_MOTION + out.x * eject + random.nextGaussian() * SCATTER,
                        dy * INHERITED_MOTION + out.y * eject,
                        dz * INHERITED_MOTION + out.z * eject + random.nextGaussian() * SCATTER);
            }
        });
    }

    private JetpackExhaust() {
    }
}
