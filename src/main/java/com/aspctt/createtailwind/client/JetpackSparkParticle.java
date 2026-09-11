package com.aspctt.createtailwind.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

// A spark thrown from a jetpack's nozzle: a speck of amber that glows whatever the light around it, falls, and
// cools towards red as it dies.
public class JetpackSparkParticle extends TextureSheetParticle {
    // The last part of its life over which a spark fades out.
    private static final float FADE_FROM = 0.7F;

    private final float startGreen;
    private final float startBlue;

    protected JetpackSparkParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
        // Particle's constructor that takes a speed scatters it, and the spawner has already scattered it.
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.lifetime = 6 + this.random.nextInt(7);
        this.gravity = 0.5F;
        this.friction = 0.92F;
        this.quadSize = 0.06F + this.random.nextFloat() * 0.03F;
        this.startGreen = 0.62F + this.random.nextFloat() * 0.18F;
        this.startBlue = 0.15F + this.random.nextFloat() * 0.1F;
        setColor(1.0F, startGreen, startBlue);
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) this.age / this.lifetime;
        setColor(1.0F, startGreen * (1.0F - 0.6F * life), startBlue * (1.0F - life));
        this.alpha = life < FADE_FROM ? 1.0F : (1.0F - life) / (1.0F - FADE_FROM);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            JetpackSparkParticle particle = new JetpackSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
