package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.TailwindClientConfig;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

// Vanilla's campfire signal smoke, cut down to a second or so, so a jetpack leaves a short trail rather than a
// column that hangs in the air for fifteen seconds. Each puff shoots down out of the nozzle, then turns and rises
// like campfire smoke, only slower. How long it lasts, and whether it shows at all, is the client's choice.
public class JetpackSmokeParticle extends CampfireSmokeParticle {
    private static final float START_ALPHA = 0.95F;
    // Four fifths the size of campfire smoke.
    private static final float SIZE = 0.8F;
    // Campfire smoke rises at 0.07 blocks a tick. This rises at half that.
    private static final double RISE_SPEED = 0.035;
    // The speed a puff leaves the nozzle at, heading down, and how much of the gap between its speed and the rise
    // it keeps each tick. It sinks for about four ticks and two pixels before it starts to climb.
    private static final double MUZZLE_SPEED = -0.06;
    private static final double SETTLE = 0.8;

    protected JetpackSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
        // Between three quarters of the configured lifetime and all of it, so the trail frays unevenly.
        int maxLifetime = TailwindClientConfig.PARTICLE_LIFETIME.get();
        int minLifetime = maxLifetime - maxLifetime / 4;
        this.lifetime = minLifetime + this.random.nextInt(maxLifetime - minLifetime + 1);
        this.alpha = START_ALPHA;
        // Scaled directly rather than through scale(), which would also shrink the collision box vanilla set.
        this.quadSize *= SIZE;
        this.yd = MUZZLE_SPEED;
    }

    // The speed is eased towards the rise before vanilla moves the puff. Vanilla's tick otherwise only adds a
    // faint sideways drift, as it does for campfire smoke. Vanilla also only starts fading sixty ticks before the
    // end, which a lifetime this short never reaches, so the smoke would vanish at close to full opacity. It fades
    // out evenly across its whole life instead.
    @Override
    public void tick() {
        this.yd = RISE_SPEED + (this.yd - RISE_SPEED) * SETTLE;
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
