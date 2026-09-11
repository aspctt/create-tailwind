package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.TailwindClientConfig;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

// Vanilla's campfire signal smoke, cut down to a second or so, so a jetpack leaves a short trail rather than a
// column that hangs in the air for fifteen seconds. How long it lasts, and whether it shows at all, is the
// client's choice.
public class JetpackSmokeParticle extends CampfireSmokeParticle {
    private static final float START_ALPHA = 0.95F;

    protected JetpackSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
        // Between three quarters of the configured lifetime and all of it, so the trail frays unevenly.
        int maxLifetime = TailwindClientConfig.PARTICLE_LIFETIME.get();
        int minLifetime = maxLifetime - maxLifetime / 4;
        this.lifetime = minLifetime + this.random.nextInt(maxLifetime - minLifetime + 1);
        this.alpha = START_ALPHA;
    }

    // Vanilla only starts fading sixty ticks before the end, which a lifetime this short never reaches, so the
    // smoke would vanish at close to full opacity. Fade it out evenly across its whole life instead.
    @Override
    public void tick() {
        super.tick();
        this.alpha = START_ALPHA * (1.0F - (float) this.age / this.lifetime);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        // The server sends every puff, and the client drops the ones its settings leave out.
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            if (!TailwindClientConfig.EXHAUST_PARTICLES.get()
                    || level.random.nextInt(100) >= TailwindClientConfig.PARTICLE_DENSITY.get()) {
                return null;
            }
            JetpackSmokeParticle particle = new JetpackSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
