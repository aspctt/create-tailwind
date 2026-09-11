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
//
// Worn with an elytra, the jetpack grants no flight and boosts the glide instead. The push itself is applied by the
// player's own client (JetpackBoost), which says when it is boosting; the air and the exhaust are handled here.
public final class JetpackHandler {
    private static final AttributeModifier FLIGHT =
            new AttributeModifier(Jetpacks.FLIGHT_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE);

    // Players whose clients, and the clients tracking them, were last told they are jetpack flying. Server thread
    // only. A player is dropped from it whenever their own client starts over, so the next tick announces again.
    private static final Set<UUID> ANNOUNCED = new HashSet<>();

    // Players whose own clients say they are holding forward to boost a glide. Server thread only.
    private static final Set<UUID> BOOSTING = new HashSet<>();

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
        boolean booster = fuelled && Jetpacks.hasElytra(player);

        // Once the modifier is gone, and nothing else lets the player fly, ServerPlayer takes them out of the air.
        if (!fuelled || booster) {
            flight.removeModifier(Jetpacks.FLIGHT_MODIFIER_ID);
        } else if (!flight.hasModifier(Jetpacks.FLIGHT_MODIFIER_ID)) {
            flight.addTransientModifier(FLIGHT);
        }

        boolean flying = fuelled && !booster && player.getAbilities().flying;
        boolean boosting = booster && player.isFallFlying() && BOOSTING.contains(player.getUUID());
        announce(player, flying || boosting);
        if (!flying && !boosting) {
            return;
        }

        consumeAir(player, jetpack);
        if (TailwindConfig.REMOVE_INVISIBILITY.get() && player.hasEffect(MobEffects.INVISIBILITY)) {
            player.removeEffect(MobEffects.INVISIBILITY);
        }
        // A glide's fall is the elytra's business, as it is without a jetpack.
        if (flying) {
            player.resetFallDistance();
        }
        emitExhaust(player);
    }

    // What the player's own client says about boosting a glide. Only taken up while they are gliding with a
    // jetpack that can boost, so a client can at worst spend its own air.
    public static void setBoosting(Player player, boolean boosting) {
        if (boosting) {
            BOOSTING.add(player.getUUID());
        } else {
            BOOSTING.remove(player.getUUID());
        }
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
        BOOSTING.remove(event.getEntity().getUUID());
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
        BOOSTING.clear();
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
        // Campfire signal smoke, the particle Do a Barrel Roll uses for its thrust trail, cut down to a second.
        // Spawned in place without the player's speed, so it stays where the player was and draws the flight
        // path. The particle gives itself its kick down out of the nozzle and its slow rise. One puff per nozzle.
        // Each client applies its own particle settings when it receives them.
        JetpackNozzles.forEach(player, (x, y, z) ->
                level.sendParticles(ModParticles.JETPACK_SMOKE.get(), x, y, z, 1, 0, 0, 0, 0));
    }

    private JetpackHandler() {
    }
}
