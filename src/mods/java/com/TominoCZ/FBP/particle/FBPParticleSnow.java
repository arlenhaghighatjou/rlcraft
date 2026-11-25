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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPParticleSnow extends ParticleDigging {
   private final IBlockState sourceState;
   Minecraft mc;
   double scaleAlpha;
   double prevParticleScale;
   double prevParticleAlpha;
   double endMult = 1.0D;
   FBPVector3d rot;
   FBPVector3d prevRot;
   FBPVector3d rotStep;
   Vec2f[] par;

   public FBPParticleSnow(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

      try {
         FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
      } catch (Throwable var16) {
         var16.printStackTrace();
      }

      this.rot = new FBPVector3d();
      this.prevRot = new FBPVector3d();
      this.createRotationMatrix();
      this.motionX = xSpeedIn;
      this.motionY = -ySpeedIn;
      this.motionZ = zSpeedIn;
      this.particleGravity = 1.0F;
      this.sourceState = state;
      this.mc = Minecraft.getMinecraft();
      this.particleScale = (float)((double)this.particleScale * FBP.random.nextDouble(FBP.scaleMult - 0.25D, FBP.scaleMult + 0.25D));
      this.particleMaxAge = (int)FBP.random.nextDouble(250.0D, 300.0D);
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.scaleAlpha = (double)this.particleScale * 0.75D;
      this.particleAlpha = 0.0F;
      this.particleScale = 0.0F;
      this.canCollide = true;
      if (FBP.randomFadingSpeed) {
         this.endMult *= FBP.random.nextDouble(0.7D, 1.0D);
      }

      this.multipleParticleScaleBy(1.0F);
   }

   private void createRotationMatrix() {
      double rx = FBP.random.nextDouble();
      double ry = FBP.random.nextDouble();
      double rz = FBP.random.nextDouble();
      this.rotStep = new FBPVector3d(rx > 0.5D ? 1.0D : -1.0D, ry > 0.5D ? 1.0D : -1.0D, rz > 0.5D ? 1.0D : -1.0D);
      this.rot.copyFrom(this.rotStep);
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
   }

   public Particle multipleParticleScaleBy(float scale) {
      Particle p = super.multipleParticleScaleBy(scale);
      float f = this.particleScale / 10.0F;
      this.setBoundingBox(new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)(2.0F * f), this.posZ + (double)f));
      return p;
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
      this.prevRot.copyFrom(this.rot);
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevParticleAlpha = (double)this.particleAlpha;
      this.prevParticleScale = (double)this.particleScale;
      if (!this.mc.isGamePaused()) {
         ++this.particleAge;
         if (this.posY < this.mc.player.posY - (double)(this.mc.gameSettings.renderDistanceChunks * 16)) {
            this.setExpired();
         }

         this.rot.add(this.rotStep.multiply(FBP.rotationMult * 5.0D));
         if (this.particleAge >= this.particleMaxAge) {
            if (FBP.randomFadingSpeed) {
               this.particleScale = (float)((double)this.particleScale * 0.75D * this.endMult);
            } else {
               this.particleScale *= 0.75F;
            }

            if ((double)this.particleAlpha > 0.01D && (double)this.particleScale <= this.scaleAlpha) {
               if (FBP.randomFadingSpeed) {
                  this.particleAlpha = (float)((double)this.particleAlpha * 0.6499999761581421D * this.endMult);
               } else {
                  this.particleAlpha *= 0.65F;
               }
            }

            if ((double)this.particleAlpha <= 0.01D) {
               this.setExpired();
            }
         } else {
            if (this.particleScale < 1.0F) {
               if (FBP.randomFadingSpeed) {
                  this.particleScale = (float)((double)this.particleScale + 0.07500000298023224D * this.endMult);
               } else {
                  this.particleScale += 0.075F;
               }

               if (this.particleScale > 1.0F) {
                  this.particleScale = 1.0F;
               }
            }

            if (this.particleAlpha < 1.0F) {
               if (FBP.randomFadingSpeed) {
                  this.particleAlpha = (float)((double)this.particleAlpha + 0.04500000178813934D * this.endMult);
               } else {
                  this.particleAlpha += 0.045F;
               }

               if (this.particleAlpha > 1.0F) {
                  this.particleAlpha = 1.0F;
               }
            }
         }

         if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial().isLiquid()) {
            this.setExpired();
         }

         this.motionY -= 0.04D * (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.onGround && FBP.restOnFloor) {
            this.rot.x = (double)((float)Math.round(this.rot.x / 90.0D) * 90.0F);
            this.rot.z = (double)((float)Math.round(this.rot.z / 90.0D) * 90.0F);
         }

         this.motionX *= 0.9800000190734863D;
         if (this.motionY < -0.2D) {
            this.motionY *= 0.7500000190734863D;
         }

         this.motionZ *= 0.9800000190734863D;
         if (this.onGround) {
            this.motionX *= 0.680000190734863D;
            this.motionZ *= 0.6800000190734863D;
            this.rotStep = this.rotStep.multiply(0.85D);
            this.particleAge += 2;
         }
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
      if (!FBP.lowTraction && !FBP.bounceOffWalls) {
         if (x != X) {
            this.motionX *= 0.699999988079071D;
         }

         if (z != Z) {
            this.motionZ *= 0.699999988079071D;
         }
      }

   }

   public void renderParticle(BufferBuilder buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
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
      if (FBP.restOnFloor) {
         f6 += f4 / 10.0F;
      }

      FBPVector3d smoothRot = new FBPVector3d(0.0D, 0.0D, 0.0D);
      if (FBP.rotationMult > 0.0D) {
         smoothRot.y = this.rot.y;
         smoothRot.z = this.rot.z;
         if (!FBP.randomRotation) {
            smoothRot.x = this.rot.x;
         }

         if (!FBP.frozen) {
            FBPVector3d vec = this.rot.partialVec(this.prevRot, partialTicks);
            if (FBP.randomRotation) {
               smoothRot.y = vec.y;
               smoothRot.z = vec.z;
            } else {
               smoothRot.x = vec.x;
            }
         }
      }

      GlStateManager.enableCull();
      this.par = new Vec2f[]{new Vec2f(f1, f3), new Vec2f(f1, f2), new Vec2f(f, f2), new Vec2f(f, f3)};
      FBPRenderUtil.renderCubeShaded_S(buf, this.par, f5, f6, f7, (double)(f4 / 10.0F), smoothRot, i >> 16 & '\uffff', i & '\uffff', this.particleRed, this.particleGreen, this.particleBlue, alpha, FBP.cartoonMode);
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
