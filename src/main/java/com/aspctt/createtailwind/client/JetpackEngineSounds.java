package com.aspctt.createtailwind.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aspctt.createtailwind.ModSounds;
import com.aspctt.createtailwind.TailwindClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

// The jetpack engine, started and stopped by what the server says about who is jetpack flying. Three looping
// layers follow each flying player: the rush of the exhaust, which does most of the work of sounding like a
// jetpack, with Create's cogwheel rumble and a low beacon drone behind it for the machinery. On top of those the
// tank vents a faint steam hiss on takeoff and then every few seconds. The loops climb in pitch as the player speeds
// up, the rush most of all, and fade in on takeoff and out on landing rather than cutting.
public final class JetpackEngineSounds {
    // Each loop: volume at rest, how much of that volume waits for speed, pitch at rest, and pitch gained at
    // full speed.
    private static final LayerSettings RUSH = new LayerSettings(0.25F, 0.6F, 1.2F, 0.4F);
    private static final LayerSettings RUMBLE = new LayerSettings(0.45F, 0.25F, 0.9F, 0.4F);
    private static final LayerSettings HUM = new LayerSettings(0.3F, 0.25F, 0.6F, 0.25F);

    // The hiss: the old exhaust sound's pitch, jittered a little, at irregular gaps of three to five seconds. Kept
    // faint, since Create's steam recording is loud enough to bury the loops underneath it.
    private static final float HISS_VOLUME = 0.05F;
    private static final float HISS_PITCH = 0.5F;
    private static final float HISS_PITCH_JITTER = 0.05F;
    private static final int HISS_MIN_TICKS = 60;
    private static final int HISS_MAX_TICKS = 100;

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
    // reached this client, or every flying player once the sound is switched back on. Then vents the hisses due.
    public static void onClientTick(ClientTickEvent.Post event) {
        PLAYING.values().removeIf(Engine::isStopped);
        for (int entityId : FLYING) {
            start(entityId);
        }
        for (Engine engine : PLAYING.values()) {
            engine.tick();
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
        Engine engine = new Engine(player);
        PLAYING.put(entityId, engine);
        engine.play();
    }

    private static boolean isRunning(Entity entity) {
        return !entity.isRemoved() && FLYING.contains(entity.getId()) && TailwindClientConfig.EXHAUST_SOUND.get();
    }

    private static float configuredVolume() {
        return TailwindClientConfig.EXHAUST_VOLUME.get() / 100.0F;
    }

    private record LayerSettings(float volume, float volumeFromSpeed, float pitch, float pitchFromSpeed) {
    }

    private static final class Engine {
        private final Player player;
        private final List<Layer> layers;
        // Zero, so the first tick vents the takeoff hiss.
        private int ticksToHiss;

        Engine(Player player) {
            this.player = player;
            this.layers = List.of(
                    new Layer(player, ModSounds.JETPACK_RUSH.get(), RUSH),
                    new Layer(player, ModSounds.JETPACK_ENGINE.get(), RUMBLE),
                    new Layer(player, ModSounds.JETPACK_HUM.get(), HUM));
        }

        void play() {
            for (Layer layer : layers) {
                Minecraft.getInstance().getSoundManager().play(layer);
            }
        }

        boolean isStopped() {
            return layers.stream().allMatch(Layer::isStopped);
        }

        void tick() {
            if (!isRunning(player) || --ticksToHiss > 0) {
                return;
            }
            RandomSource random = player.getRandom();
            ticksToHiss = HISS_MIN_TICKS + random.nextInt(HISS_MAX_TICKS - HISS_MIN_TICKS + 1);
            float volume = HISS_VOLUME * configuredVolume();
            if (volume <= 0.0F) {
                return;
            }
            float pitch = HISS_PITCH + (random.nextFloat() * 2.0F - 1.0F) * HISS_PITCH_JITTER;
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(ModSounds.JETPACK_HISS.get(),
                    SoundSource.PLAYERS, volume, pitch, random, player.getX(), player.getY() + player.getBbHeight() / 2.0,
                    player.getZ()));
        }
    }

    private static final class Layer extends AbstractTickableSoundInstance {
        private final Entity entity;
        private final LayerSettings settings;
        private float fade;

        Layer(Entity entity, SoundEvent event, LayerSettings settings) {
            super(event, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.entity = entity;
            this.settings = settings;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0F;
            this.pitch = settings.pitch();
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
            fade = isRunning(entity) ? Math.min(1.0F, fade + FADE_STEP) : fade - FADE_STEP;
            if (fade <= 0.0F) {
                stop();
                return;
            }

            double speed = Math.sqrt(entity.distanceToSqr(entity.xo, entity.yo, entity.zo));
            float effort = (float) Mth.clamp(speed / FULL_SPEED, 0.0, 1.0);
            float fromSpeed = settings.volumeFromSpeed();
            volume = settings.volume() * fade * (1.0F - fromSpeed + fromSpeed * effort) * configuredVolume();
            pitch = settings.pitch() + settings.pitchFromSpeed() * effort;
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
