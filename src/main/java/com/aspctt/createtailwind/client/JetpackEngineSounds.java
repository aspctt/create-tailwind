package com.aspctt.createtailwind.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aspctt.createtailwind.ModSounds;
import com.aspctt.createtailwind.TailwindClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

// The jetpack engine: two looping layers that follow each flying player, started and stopped by what the server
// says about who is jetpack flying. Cogs carry the mechanical rumble, and the beacon hum, pitched well down, sits
// under it as the drone of the air. Both climb in pitch, and a little in volume, as the player speeds up, and
// fade in on takeoff and out on landing rather than cutting.
public final class JetpackEngineSounds {
    private static final float RUMBLE_VOLUME = 0.8F;
    private static final float RUMBLE_PITCH = 0.9F;
    private static final float RUMBLE_PITCH_GAIN = 0.4F;

    private static final float HUM_VOLUME = 0.4F;
    private static final float HUM_PITCH = 0.6F;
    private static final float HUM_PITCH_GAIN = 0.25F;

    // Blocks per tick at which the engine is working hardest: about the top speed of jetpack flight, which
    // cannot sprint.
    private static final double FULL_SPEED = 0.55;
    // Fade in and out over a quarter of a second.
    private static final float FADE_STEP = 0.2F;

    // Entity ids the server says are jetpack flying.
    private static final Set<Integer> FLYING = new HashSet<>();
    private static final Map<Integer, Engine> PLAYING = new HashMap<>();

    public static void setFlying(int entityId, boolean flying) {
        if (flying) {
            FLYING.add(entityId);
            start(entityId);
        } else {
            FLYING.remove(entityId);
        }
    }

    // Starts engines that should be running but are not: a player whose flight was announced before their entity
    // reached this client, or every flying player once the sound is switched back on.
    public static void onClientTick(ClientTickEvent.Post event) {
        PLAYING.values().removeIf(Engine::isStopped);
        for (int entityId : FLYING) {
            start(entityId);
        }
    }

    // Entity ids mean nothing in the next level, and the server announces flight again after a respawn or a
    // dimension change.
    public static void onLevelChange(ClientPlayerNetworkEvent.Clone event) {
        FLYING.clear();
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FLYING.clear();
        PLAYING.clear();
    }

    private static void start(int entityId) {
        if (!TailwindClientConfig.EXHAUST_SOUND.get() || PLAYING.containsKey(entityId)) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(entityId) instanceof Player player)) {
            return;
        }
        Engine engine = new Engine(
                new Layer(player, ModSounds.JETPACK_ENGINE.get(), RUMBLE_VOLUME, RUMBLE_PITCH, RUMBLE_PITCH_GAIN),
                new Layer(player, ModSounds.JETPACK_HUM.get(), HUM_VOLUME, HUM_PITCH, HUM_PITCH_GAIN));
        PLAYING.put(entityId, engine);
        Minecraft.getInstance().getSoundManager().play(engine.rumble());
        Minecraft.getInstance().getSoundManager().play(engine.hum());
    }

    private record Engine(Layer rumble, Layer hum) {
        boolean isStopped() {
            return rumble.isStopped() && hum.isStopped();
        }
    }

    private static final class Layer extends AbstractTickableSoundInstance {
        private final Entity entity;
        private final float baseVolume;
        private final float basePitch;
        private final float pitchGain;
        private float fade;

        Layer(Entity entity, SoundEvent event, float baseVolume, float basePitch, float pitchGain) {
            super(event, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.entity = entity;
            this.baseVolume = baseVolume;
            this.basePitch = basePitch;
            this.pitchGain = pitchGain;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0F;
            this.pitch = basePitch;
            follow();
        }

        // Starts at zero volume and fades in, which the sound engine would otherwise skip as inaudible.
        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            if (entity.isRemoved()) {
                stop();
                return;
            }
            boolean running = FLYING.contains(entity.getId()) && TailwindClientConfig.EXHAUST_SOUND.get();
            fade = running ? Math.min(1.0F, fade + FADE_STEP) : fade - FADE_STEP;
            if (fade <= 0.0F) {
                stop();
                return;
            }

            double speed = Math.sqrt(entity.distanceToSqr(entity.xo, entity.yo, entity.zo));
            float effort = (float) Mth.clamp(speed / FULL_SPEED, 0.0, 1.0);
            float configured = TailwindClientConfig.EXHAUST_VOLUME.get() / 100.0F;
            volume = baseVolume * fade * (0.75F + 0.25F * effort) * configured;
            pitch = basePitch + pitchGain * effort;
            follow();
        }

        private void follow() {
            x = entity.getX();
            y = entity.getY() + entity.getBbHeight() / 2.0;
            z = entity.getZ();
        }
    }

    private JetpackEngineSounds() {
    }
}
