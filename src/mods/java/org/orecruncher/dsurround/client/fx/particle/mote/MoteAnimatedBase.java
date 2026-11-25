package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class MoteAnimatedBase extends MoteMotionBase {
   protected final int textureIdx;
   protected final int numAgingFrames;
   protected final float baseAirFriction = 0.91F;
   protected float fadeTargetRed;
   protected float fadeTargetGreen;
   protected float fadeTargetBlue;
   protected boolean fadingColor;
   protected double xAcceleration;
   protected double yAcceleration;
   protected double zAcceleration;
   protected boolean doRender;
   protected int particleTextureIndexX;
   protected int particleTextureIndexY;
   protected float texU1;
   protected float texU2;
   protected float texV1;
   protected float texV2;
   protected float particleScale;

   public MoteAnimatedBase(int textureIdx, int numFrames, @Nonnull World world, double x, double y, double z, double dX, double dY, double dZ) {
      super(world, x, y, z, dX, dY, dZ);
      this.textureIdx = textureIdx;
      this.numAgingFrames = numFrames;
      this.particleScale = (RANDOM.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public void setColor(int rgb) {
      this.red = (rgb & 16711680) >> 16;
      this.green = (rgb & '\uff00') >> 8;
      this.blue = (rgb & 255) >> 0;
      this.alpha = 255;
   }

   public void setColorFade(int rgb) {
      this.fadeTargetRed = (float)((rgb & 16711680) >> 16) / 255.0F;
      this.fadeTargetGreen = (float)((rgb & '\uff00') >> 8) / 255.0F;
      this.fadeTargetBlue = (float)((rgb & 255) >> 0) / 255.0F;
      this.fadingColor = true;
   }

   public void handleCollision() {
      this.motionX *= 0.699999988079071D;
      this.motionZ *= 0.699999988079071D;
   }

   public void update() {
      this.motionY += this.yAcceleration;
      this.motionX += this.xAcceleration;
      this.motionZ += this.zAcceleration;
      double var10001 = this.motionX;
      this.getClass();
      this.motionX = var10001 * 0.9100000262260437D;
      var10001 = this.motionY;
      this.getClass();
      this.motionY = var10001 * 0.9100000262260437D;
      var10001 = this.motionZ;
      this.getClass();
      this.motionZ = var10001 * 0.9100000262260437D;
      super.update();
      if (!this.isAlive()) {
         if (this.age > this.maxAge / 2) {
            this.alpha = (int)((1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge) * 255.0F);
            if (this.fadingColor) {
               this.red = (int)((float)this.red + (this.fadeTargetRed - (float)this.red) * 0.2F);
               this.green = (int)((float)this.green + (this.fadeTargetGreen - (float)this.green) * 0.2F);
               this.blue = (int)((float)this.blue + (this.fadeTargetBlue - (float)this.blue) * 0.2F);
            }
         }

         this.setParticleTextureIndex(this.textureIdx + (this.numAgingFrames - 1 - this.age * this.numAgingFrames / this.maxAge));
      }

   }

   public int getBrightnessForRender(float partialTick) {
      return 15728880;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      double x = (double)this.renderX(partialTicks);
      double y = (double)this.renderY(partialTicks);
      double z = (double)this.renderZ(partialTicks);
      this.drawVertex(buffer, x + (double)(-rotationX * this.particleScale - rotationXY * this.particleScale), y + (double)(-rotationZ * this.particleScale), z + (double)(-rotationYZ * this.particleScale - rotationXZ * this.particleScale), (double)this.texU2, (double)this.texV2);
      this.drawVertex(buffer, x + (double)(-rotationX * this.particleScale + rotationXY * this.particleScale), y + (double)(rotationZ * this.particleScale), z + (double)(-rotationYZ * this.particleScale + rotationXZ * this.particleScale), (double)this.texU2, (double)this.texV1);
      this.drawVertex(buffer, x + (double)(rotationX * this.particleScale + rotationXY * this.particleScale), y + (double)(rotationZ * this.particleScale), z + (double)(rotationYZ * this.particleScale + rotationXZ * this.particleScale), (double)this.texU1, (double)this.texV1);
      this.drawVertex(buffer, x + (double)(rotationX * this.particleScale - rotationXY * this.particleScale), y + (double)(-rotationZ * this.particleScale), z + (double)(rotationYZ * this.particleScale - rotationXZ * this.particleScale), (double)this.texU1, (double)this.texV2);
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      this.particleTextureIndexX = particleTextureIndex % 16;
      this.particleTextureIndexY = particleTextureIndex / 16;
      this.texU1 = (float)this.particleTextureIndexX / 16.0F;
      this.texU2 = this.texU1 + 0.0624375F;
      this.texV1 = (float)this.particleTextureIndexY / 16.0F;
      this.texV2 = this.texV1 + 0.0624375F;
   }
}
