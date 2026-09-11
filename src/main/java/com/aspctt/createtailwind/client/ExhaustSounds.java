package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.ModSounds;
import com.aspctt.createtailwind.TailwindClientConfig;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

// Applies the client's sound settings to the jetpack hiss, which the server plays for everyone nearby.
public final class ExhaustSounds {
    public static void onPlaySound(PlaySoundEvent event) {
        SoundInstance sound = event.getSound();
        if (sound == null || !ModSounds.JETPACK_EXHAUST_ID.equals(sound.getLocation())) {
            return;
        }
        if (!TailwindClientConfig.EXHAUST_SOUND.get()) {
            event.setSound(null);
            return;
        }
        int volume = TailwindClientConfig.EXHAUST_VOLUME.get();
        if (volume < 100) {
            event.setSound(new Scaled(sound, volume / 100.0F));
        }
    }

    // The event fires before the sound is resolved, when its volume cannot be read yet, so the scaling is applied
    // as the sound engine asks for it. Everything else is the original sound's.
    private record Scaled(SoundInstance sound, float factor) implements SoundInstance {
        @Override
        public ResourceLocation getLocation() {
            return sound.getLocation();
        }

        @Override
        public WeighedSoundEvents resolve(SoundManager manager) {
            return sound.resolve(manager);
        }

        @Override
        public Sound getSound() {
            return sound.getSound();
        }

        @Override
        public SoundSource getSource() {
            return sound.getSource();
        }

        @Override
        public boolean isLooping() {
            return sound.isLooping();
        }

        @Override
        public boolean isRelative() {
            return sound.isRelative();
        }

        @Override
        public int getDelay() {
            return sound.getDelay();
        }

        @Override
        public float getVolume() {
            return sound.getVolume() * factor;
        }

        @Override
        public float getPitch() {
            return sound.getPitch();
        }

        @Override
        public double getX() {
            return sound.getX();
        }

        @Override
        public double getY() {
            return sound.getY();
        }

        @Override
        public double getZ() {
            return sound.getZ();
        }

        @Override
        public Attenuation getAttenuation() {
            return sound.getAttenuation();
        }

        @Override
        public boolean canStartSilent() {
            return sound.canStartSilent();
        }

        @Override
        public boolean canPlaySound() {
            return sound.canPlaySound();
        }
    }

    private ExhaustSounds() {
    }
}
