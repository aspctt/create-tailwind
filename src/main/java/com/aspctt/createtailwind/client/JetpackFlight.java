package com.aspctt.createtailwind.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

// Which players the server says are jetpack flying, for everything this client shows of it: the engine sound, the
// flame, the sparks and the glowing nozzles. Main thread only.
public final class JetpackFlight {
    // Blocks per tick at which a jetpack is working hardest: about the top speed of jetpack flight, which cannot
    // sprint.
    private static final double FULL_SPEED = 0.55;
    // Ticks the nozzles take to cool from flight to cold once a jetpack stops.
    private static final float COOLING_TICKS = 160.0F;

    private static final Set<Integer> FLYING = new HashSet<>();
    private static final Set<Integer> FLYING_VIEW = Collections.unmodifiableSet(FLYING);
    // When each jetpack that has stopped flying stopped, in render ticks, until its nozzles are cold. One that goes
    // out of view while cooling stays here until the next level change, a few bytes each.
    private static final Map<Integer, Float> STOPPED_AT = new HashMap<>();

    public static void set(int entityId, boolean flying) {
        if (flying) {
            FLYING.add(entityId);
            STOPPED_AT.remove(entityId);
            return;
        }

        // Only a jetpack that was flying has hot nozzles. The server also says "not flying" for players this client
        // never saw fly, such as everyone it is told about on joining.
        if (FLYING.remove(entityId)) {
            STOPPED_AT.put(entityId, AnimationTickHolder.getRenderTime());
        }
    }

    public static boolean isFlying(Entity entity) {
        return FLYING.contains(entity.getId());
    }

    // How hot a jetpack's nozzles are, from 1 while it flies down to 0 once they have cooled after it stops. Cold
    // jetpacks are forgotten here, the first time they are asked about.
    public static float nozzleHeat(Entity entity) {
        if (isFlying(entity)) {
            return 1.0F;
        }
        Float stoppedAt = STOPPED_AT.get(entity.getId());
        if (stoppedAt == null) {
            return 0.0F;
        }

        // Render ticks wrap round every 24 hours, which makes the time since stopping negative. Treating that as
        // cooled ends the glow early once a day instead of leaving it lit.
        float cooled = (AnimationTickHolder.getRenderTime() - stoppedAt) / COOLING_TICKS;
        if (cooled < 0.0F || cooled >= 1.0F) {
            STOPPED_AT.remove(entity.getId());
            return 0.0F;
        }
        return 1.0F - cooled;
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
        STOPPED_AT.clear();
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FLYING.clear();
        STOPPED_AT.clear();
    }

    private JetpackFlight() {
    }
}
