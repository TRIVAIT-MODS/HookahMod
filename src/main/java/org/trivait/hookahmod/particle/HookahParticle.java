package org.trivait.hookahmod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class HookahParticle extends TextureSheetParticle {

    HookahParticle(ClientLevel clientLevel, double x, double y, double z,
                   double vx, double vy, double vz, boolean longLife) {
        super(clientLevel, x, y, z);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = longLife
                ? this.random.nextInt(50) + 280
                : this.random.nextInt(50) + 80;
        this.gravity = 3.0E-6F;
        this.xd = vx;
        this.yd = vy + this.random.nextFloat() / 500.0;
        this.zd = vz;
    }

    public void applyTint(int packedRgb, float coefficient) {
        float r = ((packedRgb >> 16) & 0xFF) / 255f;
        float g = ((packedRgb >> 8)  & 0xFF) / 255f;
        float b = ( packedRgb        & 0xFF) / 255f;
        this.rCol = 1f + (r - 1f) * coefficient;
        this.gCol = 1f + (g - 1f) * coefficient;
        this.bCol = 1f + (b - 1f) * coefficient;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && this.alpha > 0.0F) {
            this.xd += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            HookahParticle p = new HookahParticle(level, x, y, z, vx, vy, vz, false);
            p.setAlpha(0.9F);
            p.pickSprite(sprites);
            return p;
        }
    }
}
