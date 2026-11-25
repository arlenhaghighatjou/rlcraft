package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPParticleRain extends ParticleDigging {
   private final IBlockState sourceState;
   Minecraft mc;
   double AngleY;
   double particleHeight;
   double prevParticleScale;
   double prevParticleHeight;
   double prevParticleAlpha;
   double scalar = FBP.scaleMult;
   double endMult = 1.0D;
   Vec2f[] par;

   public FBPParticleRain(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

      try {
         FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
      } catch (Throwable var16) {
         var16.printStackTrace();
      }

      this.AngleY = FBP.random.nextDouble() * 45.0D;
      this.motionX = xSpeedIn;
      this.motionY = -ySpeedIn;
      this.motionZ = zSpeedIn;
      this.particleGravity = 0.025F;
      this.sourceState = state;
      this.mc = Minecraft.getMinecraft();
      this.particleMaxAge = (int)FBP.random.nextDouble(50.0D, 70.0D);
      this.particleAlpha = 0.0F;
      this.particleScale = 0.0F;
      this.canCollide = true;
      if (FBP.randomFadingSpeed) {
         this.endMult *= FBP.random.nextDouble(0.85D, 1.0D);
      }

   }

   public void setParticleTextureIndex(int particleTextureIndex) {
   }

   public Particle MultiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY = (this.motionY - 0.10000000149011612D) * (double)(multiplier / 2.0F) + 0.10000000149011612D;
      this.motionZ *= (double)multiplier;
      return this;
   }

   protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
      int i = this.mc.getBlockColors().colorMultiplier(this.sourceState, this.world, p_187154_1_, 0);
      this.particleRed *= (float)(i >> 16 & 255) / 255.0F;
      this.particleGreen *= (float)(i >> 8 & 255) / 255.0F;
      this.particleBlue *= (float)(i & 255) / 255.0F;
   }

   public int getFXLayer() {
      return 1;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevParticleAlpha = (double)this.particleAlpha;
      this.prevParticleScale = (double)this.particleScale;
      this.prevParticleHeight = this.particleHeight;
      if (!this.mc.isGamePaused()) {
         ++this.particleAge;
         if (this.posY < this.mc.player.posY - (double)(this.mc.gameSettings.renderDistanceChunks * 9)) {
            this.setExpired();
         }

         if (!this.onGround) {
            if (this.particleAge < this.particleMaxAge) {
               double max = this.scalar * 0.5D;
               if ((double)this.particleScale < max) {
                  if (FBP.randomFadingSpeed) {
                     this.particleScale = (float)((double)this.particleScale + 0.05000000074505806D * this.endMult);
                  } else {
                     this.particleScale += 0.05F;
                  }

                  if ((double)this.particleScale > max) {
                     this.particleScale = (float)max;
                  }

                  this.particleHeight = (double)this.particleScale;
               }

               if (this.particleAlpha < 0.65F) {
                  if (FBP.randomFadingSpeed) {
                     this.particleAlpha = (float)((double)this.particleAlpha + 0.08500000089406967D * this.endMult);
                  } else {
                     this.particleAlpha += 0.085F;
                  }

                  if (this.particleAlpha > 0.65F) {
                     this.particleAlpha = 0.65F;
                  }
               }
            } else {
               this.setExpired();
            }
         }

         if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial().isLiquid()) {
            this.setExpired();
         }

         this.motionY -= 0.04D * (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionY *= 1.0002500019073486D;
         if (this.onGround) {
            this.motionX = 0.0D;
            this.motionY = -0.25D;
            this.motionZ = 0.0D;
            if (this.particleHeight > 0.07500000298023224D) {
               this.particleHeight *= 0.7250000238418579D;
            }

            float max = (float)this.scalar * 4.25F;
            if (this.particleScale < max) {
               this.particleScale += max / 10.0F;
               if (this.particleScale > max) {
                  this.particleScale = max;
               }
            }

            if (this.particleScale >= max / 2.0F) {
               if (FBP.randomFadingSpeed) {
                  this.particleAlpha = (float)((double)this.particleAlpha * 0.75D * this.endMult);
               } else {
                  this.particleAlpha *= 0.75F;
               }

               if (this.particleAlpha <= 0.001F) {
                  this.setExpired();
               }
            }
         }
      }

      Vec3d rgb = this.mc.world.getSkyColor(this.mc.player, 0.0F);
      this.particleRed = (float)rgb.x;
      this.particleGreen = (float)MathHelper.clamp(rgb.y + 0.25D, 0.25D, 1.0D);
      this.particleBlue = (float)MathHelper.clamp(rgb.z + 0.5D, 0.5D, 1.0D);
      if (this.particleGreen > 1.0F) {
         this.particleGreen = 1.0F;
      }

      if (this.particleBlue > 1.0F) {
         this.particleBlue = 1.0F;
      }

   }

   public void move(double x, double y, double z) {
      double X = x;
      double Y = y;
      double Z = z;
      List<AxisAlignedBB> list = this.world.getCollisionBoxes((Entity)null, this.getBoundingBox().expand(x, y, z));

      for(AxisAlignedBB axisalignedbb : list) {
         y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
      }

      this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

      for(AxisAlignedBB axisalignedbb : list) {
         x = axisalignedbb.calculateXOffset(this.getBoundingBox(), x);
      }

      this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

      for(AxisAlignedBB axisalignedbb : list) {
         z = axisalignedbb.calculateZOffset(this.getBoundingBox(), z);
      }

      this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
      this.resetPositionToBB();
      this.onGround = y != Y && Y < 0.0D;
      if (x != X) {
         this.motionX *= 0.699999988079071D;
      }

      if (z != Z) {
         this.motionZ *= 0.699999988079071D;
      }

   }

   public void renderParticle(WorldRenderer buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!FBP.isEnabled() && this.particleMaxAge != 0) {
         this.particleMaxAge = 0;
      }

      float f = 0.0F;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (this.particleTexture != null) {
         if (!FBP.cartoonMode) {
            f = this.particleTexture.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            f2 = this.particleTexture.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
         }

         f1 = this.particleTexture.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
         f3 = this.particleTexture.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
      } else {
         f = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
         f1 = f + 0.015609375F;
         f2 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
         f3 = f2 + 0.015609375F;
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
      float f4 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
      float height = (float)(this.prevParticleHeight + (this.particleHeight - this.prevParticleHeight) * (double)partialTicks);
      this.par = new Vec2f[]{new Vec2f(f1, f3), new Vec2f(f1, f2), new Vec2f(f, f2), new Vec2f(f, f3)};
      FBPRenderUtil.renderCubeShaded_WH(buf, this.par, f5, f6 + height / 10.0F, f7, (double)(f4 / 10.0F), (double)(height / 10.0F), new FBPVector3d(0.0D, this.AngleY, 0.0D), i >> 16 & '\uffff', i & '\uffff', this.particleRed, this.particleGreen, this.particleBlue, alpha, FBP.cartoonMode);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = 0;
      if (this.world.isBlockLoaded(new BlockPos(this.posX, this.posY, this.posZ))) {
         j = this.world.getCombinedLight(new BlockPos(this.posX, this.posY, this.posZ), 0);
      }

      return i == 0 ? j : i;
   }
}
