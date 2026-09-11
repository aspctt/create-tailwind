package com.aspctt.createtailwind.jetpack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.aspctt.createtailwind.ModParticles;
import com.aspctt.createtailwind.TailwindConfig;
import com.aspctt.createtailwind.network.JetpackFlightPayload;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

// Adapted from Create: Backtank is Jetpack by Hueihuea (github.com/mchhui/Create-BacktankIsJetpack), used under
// the MIT License. The licence text is in NOTICE.
//
// A worn jetpack with air in it grants creative-style flight, which draws on that air while the player is flying.
// Flight is granted through NeoForge's creative flight attribute rather than the deprecated Abilities#mayfly, so
// only this mod's own modifier is ever added or taken away, and other sources of flight are left alone. The
// attribute is synced, so the client sees the modifier too.
public final class JetpackHandler {
    private static final AttributeModifier FLIGHT =
            new AttributeModifier(Jetpacks.FLIGHT_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE);

    // Where the exhaust leaves the jetpack: the mouths of its two nozzles, as the jetpack is drawn on the body. In
    // model pixels, the nozzles end 10 below the body's pivot, 7 behind it and 4 to either side. PlayerRenderer
    // draws the model at 15/16 scale and LivingEntityRenderer lifts it by 1.501, both before the player's own scale.
    private static final double PLAYER_MODEL_SCALE = 0.9375;
    private static final double NOZZLE_HEIGHT = PLAYER_MODEL_SCALE * (1.501 - 10 / 16.0);
    private static final double NOZZLE_BEHIND = PLAYER_MODEL_SCALE * 7 / 16.0;
    private static final double NOZZLE_SIDE = PLAYER_MODEL_SCALE * 4 / 16.0;

    // Players whose clients, and the clients tracking them, were last told they are jetpack flying. Server thread
    // only. A player is dropped from it whenever their own client starts over, so the next tick announces again.
    private static final Set<UUID> ANNOUNCED = new HashSet<>();

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight == null) {
            return;
        }

        // Creative and spectator flight are the game mode's own, and do not draw on the jetpack.
        ItemStack jetpack = player.isCreative() || player.isSpectator() ? ItemStack.EMPTY : Jetpacks.findWorn(player);
        boolean fuelled = !jetpack.isEmpty() && BacktankUtil.hasAirRemaining(jetpack);

        // Once the modifier is gone, and nothing else lets the player fly, ServerPlayer takes them out of the air.
        if (!fuelled) {
            flight.removeModifier(Jetpacks.FLIGHT_MODIFIER_ID);
        } else if (!flight.hasModifier(Jetpacks.FLIGHT_MODIFIER_ID)) {
            flight.addTransientModifier(FLIGHT);
        }

        boolean flying = fuelled && player.getAbilities().flying;
        announce(player, flying);
        if (!flying) {
            return;
        }

        consumeAir(player, jetpack);
        if (TailwindConfig.REMOVE_INVISIBILITY.get() && player.hasEffect(MobEffects.INVISIBILITY)) {
            player.removeEffect(MobEffects.INVISIBILITY);
        }
        player.resetFallDistance();
        emitExhaust(player);
    }

    // Being able to fly is what vanilla takes as a reason to skip fall damage entirely. Unless the config says
    // otherwise, the fall is taken as though the jetpack were not there.
    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || TailwindConfig.IMMUNE_TO_FALL_DAMAGE.get()) {
            return;
        }
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight == null || !flight.hasModifier(Jetpacks.FLIGHT_MODIFIER_ID)) {
            return;
        }

        // The jetpack's flight is set aside for the one call. If something else still lets the player fly, the
        // fall is that source's business, and the call would only land back here. NeoForge passes the distance
        // as the multiplier, so it is replaced with the 1 a normal fall uses.
        flight.removeModifier(Jetpacks.FLIGHT_MODIFIER_ID);
        try {
            if (!player.mayFly()) {
                player.causeFallDamage(event.getDistance(), 1.0F, player.damageSources().fall());
            }
        } finally {
            flight.addTransientModifier(FLIGHT);
        }
    }

    // A client that starts tracking a player learns whether they are jetpack flying. Sent either way: a client
    // that stopped tracking a flying player missed hearing that they landed.
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player target && event.getEntity() instanceof ServerPlayer tracker) {
            PacketDistributor.sendToPlayer(tracker,
                    new JetpackFlightPayload(target.getId(), ANNOUNCED.contains(target.getUUID())));
        }
    }

    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ANNOUNCED.remove(event.getEntity().getUUID());
    }

    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ANNOUNCED.remove(event.getEntity().getUUID());
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ANNOUNCED.remove(event.getEntity().getUUID());
    }

    // An integrated server can be started again in the same game.
    public static void onServerStopped(ServerStoppedEvent event) {
        ANNOUNCED.clear();
    }

    private static void announce(Player player, boolean flying) {
        boolean changed = flying ? ANNOUNCED.add(player.getUUID()) : ANNOUNCED.remove(player.getUUID());
        if (changed) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new JetpackFlightPayload(player.getId(), flying));
        }
    }

    // Whole units every tick, and the fraction left over as a chance, so the average matches the config.
    private static void consumeAir(Player player, ItemStack jetpack) {
        double perTick = TailwindConfig.AIR_CONSUMPTION_PER_TICK.get();
        int amount = (int) perTick;
        if (player.getRandom().nextDouble() < perTick - amount) {
            amount++;
        }
        if (amount > 0) {
            BacktankUtil.consumeAir(player, jetpack, amount);
        }
    }

    private static void emitExhaust(Player player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        // The jetpack turns with the body rather than the head, so the nozzles follow the body's yaw. Behind the
        // body is (sin, -cos) of that yaw, and across it is (cos, sin).
        double scale = player.getScale();
        double yaw = Math.toRadians(player.yBodyRot);
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double x = player.getX() + sin * NOZZLE_BEHIND * scale;
        double y = player.getY() + NOZZLE_HEIGHT * scale;
        double z = player.getZ() - cos * NOZZLE_BEHIND * scale;
        double side = NOZZLE_SIDE * scale;
        // Campfire signal smoke, the particle Do a Barrel Roll uses for its thrust trail, cut down to a second.
        // Spawned in place with no spread or speed, so it hangs where the player was and draws the flight path.
        // One puff per nozzle. Each client applies its own particle settings when it receives them.
        for (int direction = -1; direction <= 1; direction += 2) {
            level.sendParticles(ModParticles.JETPACK_SMOKE.get(), x + cos * side * direction, y,
                    z + sin * side * direction, 1, 0, 0, 0, 0);
        }
    }

    private JetpackHandler() {
    }
}
