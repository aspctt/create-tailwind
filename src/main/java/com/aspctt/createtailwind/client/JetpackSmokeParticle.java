package com.aspctt.createtailwind.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

// Vanilla's campfire signal smoke, cut down to a second at most so a jetpack leaves a short trail rather than a
// column that hangs in the air for fifteen seconds.
public class JetpackSmokeParticle extends CampfireSmokeParticle {
    private static final float START_ALPHA = 0.95F;
    private static final int MIN_LIFETIME = 15;
    private static final int MAX_LIFETIME = 20;

    protected JetpackSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
        this.lifetime = MIN_LIFETIME + this.random.nextInt(MAX_LIFETIME - MIN_LIFETIME + 1);
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

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            JetpackSmokeParticle particle = new JetpackSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
