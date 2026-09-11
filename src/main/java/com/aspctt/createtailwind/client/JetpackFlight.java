package com.aspctt.createtailwind.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

// Which players the server says are jetpack flying, for everything this client shows of it: the engine sound, the
// flame and the sparks. Main thread only.
public final class JetpackFlight {
    // Blocks per tick at which a jetpack is working hardest: about the top speed of jetpack flight, which cannot
    // sprint.
    private static final double FULL_SPEED = 0.55;

    private static final Set<Integer> FLYING = new HashSet<>();
    private static final Set<Integer> FLYING_VIEW = Collections.unmodifiableSet(FLYING);

    public static void set(int entityId, boolean flying) {
        if (flying) {
            FLYING.add(entityId);
        } else {
            FLYING.remove(entityId);
        }
    }

    public static boolean isFlying(Entity entity) {
        return FLYING.contains(entity.getId());
    }

    public static Set<Integer> flyingIds() {
        return FLYING_VIEW;
    }

    // How hard a jetpack is working, from 0 when hovering to 1 at full speed, going by how far the entity moved
    // since last tick.
    public static float effort(Entity entity) {
        double speed = Math.sqrt(entity.distanceToSqr(entity.xo, entity.yo, entity.zo));
        return (float) Mth.clamp(speed / FULL_SPEED, 0.0, 1.0);
    }

    // Entity ids mean nothing in the next level, and the server announces flight again after a respawn or a
    // dimension change.
    public static void onLevelChange(ClientPlayerNetworkEvent.Clone event) {
        FLYING.clear();
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FLYING.clear();
    }

    private JetpackFlight() {
    }
}
