package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FBPParticleSmokeNormal extends ParticleSmokeNormal {
   Minecraft mc;
   double startScale;
   double scaleAlpha;
   double prevParticleScale;
   double prevParticleAlpha;
   double endMult = 0.75D;
   Vec3d[] cube;
   Vec2f par;
   ParticleSmokeNormal original;

   protected FBPParticleSmokeNormal(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mX, double mY, double mZ, float scale, boolean b, TextureAtlasSprite tex, ParticleSmokeNormal original) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, mX, mY, mZ, scale);
      this.original = original;
      this.motionX = mX;
      this.motionY = mY;
      this.motionZ = mZ;
      this.mc = Minecraft.getMinecraft();
      this.particleTexture = tex;
      this.scaleAlpha = (double)this.particleScale * 0.85D;
      Block block = worldIn.getBlockState(new BlockPos(xCoordIn, yCoordIn, zCoordIn)).getBlock();
      if (block == Blocks.FIRE) {
         this.particleScale *= 0.65F;
         this.particleGravity *= 0.25F;
         this.motionX = FBP.random.nextDouble(-0.05D, 0.05D);
         this.motionY = FBP.random.nextDouble() * 0.5D;
         this.motionZ = FBP.random.nextDouble(-0.05D, 0.05D);
         this.motionY *= 0.3499999940395355D;
         this.scaleAlpha = (double)this.particleScale * 0.5D;
         this.particleMaxAge = FBP.random.nextInt(7, 18);
      } else if (block == Blocks.TORCH) {
         this.particleScale *= 0.45F;
         this.motionX = FBP.random.nextDouble(-0.05D, 0.05D);
         this.motionY = FBP.random.nextDouble() * 0.5D;
         this.motionZ = FBP.random.nextDouble(-0.05D, 0.05D);
         this.motionX *= 0.925000011920929D;
         this.motionY = 0.004999999888241291D;
         this.motionZ *= 0.925000011920929D;
         this.particleRed = 0.275F;
         this.particleGreen = 0.275F;
         this.particleBlue = 0.275F;
         this.scaleAlpha = (double)this.particleScale * 0.75D;
         this.particleMaxAge = FBP.random.nextInt(5, 10);
      } else {
         this.particleScale = scale;
         this.motionY *= 0.935D;
      }

      this.particleScale = (float)((double)this.particleScale * FBP.scaleMult);
      this.startScale = (double)this.particleScale;
      float angleY = this.rand.nextFloat() * 80.0F;
      this.cube = new Vec3d[FBP.CUBE.length];

      for(int i = 0; i < FBP.CUBE.length; ++i) {
         Vec3d vec = FBP.CUBE[i];
         this.cube[i] = FBPRenderUtil.rotatef_d(vec, 0.0F, angleY, 0.0F);
      }

      this.particleAlpha = 1.0F;
      if (FBP.randomFadingSpeed) {
         this.endMult = MathHelper.clamp(FBP.random.nextDouble(0.425D, 1.15D), 0.5432D, 1.0D);
      }

      this.multipleParticleScaleBy(1.0F);
   }

   public Particle multipleParticleScaleBy(float scale) {
      Particle p = super.multipleParticleScaleBy(scale);
      float f = this.particleScale / 20.0F;
      this.setBoundingBox(new AxisAlignedBB(this.posX - (double)f, this.posY - (double)f, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f, this.posZ + (double)f));
      return p;
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
      if (!FBP.fancySmoke) {
         this.isExpired = true;
      }

      if (++this.particleAge >= this.particleMaxAge) {
         if (FBP.randomFadingSpeed) {
            this.particleScale = (float)((double)this.particleScale * 0.8876543045043945D * this.endMult);
         } else {
            this.particleScale *= 0.8876543F;
         }

         if ((double)this.particleAlpha > 0.01D && (double)this.particleScale <= this.scaleAlpha) {
            if (FBP.randomFadingSpeed) {
               this.particleAlpha = (float)((double)this.particleAlpha * 0.7654321193695068D * this.endMult);
            } else {
               this.particleAlpha *= 0.7654321F;
            }
         }

         if ((double)this.particleAlpha <= 0.01D) {
            this.setExpired();
         }
      }

      this.motionY += 0.004D;
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1D;
         this.motionZ *= 1.1D;
      }

      this.motionX *= 0.9599999785423279D;
      this.motionY *= 0.9599999785423279D;
      this.motionZ *= 0.9599999785423279D;
      if (this.onGround) {
         this.motionX *= 0.899999988079071D;
         this.motionZ *= 0.899999988079071D;
      }

   }

   public void move(double x, double y, double z) {
      double Y = y;
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
      this.onGround = y != Y;
   }

   protected void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2.0D;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!FBP.isEnabled() && this.particleMaxAge != 0) {
         this.particleMaxAge = 0;
      }

      float f = this.particleTexture.getInterpolatedU(4.400000095367432D);
      float f1 = this.particleTexture.getInterpolatedV(4.400000095367432D);
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
      float f4 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
      this.par = new Vec2f(f, f1);
      worldRendererIn.setTranslation((double)f5, (double)f6, (double)f7);
      this.putCube(worldRendererIn, (double)(f4 / 20.0F), i >> 16 & '\uffff', i & '\uffff', this.particleRed, this.particleGreen, this.particleBlue, alpha);
      worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
   }

   public void putCube(BufferBuilder worldRendererIn, double scale, int j, int k, float r, float g, float b, float a) {
      float brightnessForRender = 1.0F;
      float R = 0.0F;
      float G = 0.0F;
      float B = 0.0F;

      for(int i = 0; i < this.cube.length; i += 4) {
         Vec3d v1 = this.cube[i];
         Vec3d v2 = this.cube[i + 1];
         Vec3d v3 = this.cube[i + 2];
         Vec3d v4 = this.cube[i + 3];
         R = r * brightnessForRender;
         G = g * brightnessForRender;
         B = b * brightnessForRender;
         brightnessForRender = (float)((double)brightnessForRender * 0.875D);
         this.addVt(worldRendererIn, scale, v1, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v2, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v3, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v4, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
      }

   }

   private void addVt(BufferBuilder worldRendererIn, double scale, Vec3d pos, double u, double v, int j, int k, float r, float g, float b, float a) {
      worldRendererIn.pos(pos.x * scale, pos.y * scale, pos.z * scale).tex(u, v).color(r, g, b, a).lightmap(j, k).endVertex();
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = 0;
      if (this.world.isBlockLoaded(new BlockPos(this.posX, this.posY, this.posZ))) {
         j = this.world.getCombinedLight(new BlockPos(this.posX, this.posY, this.posZ), 0);
      }

      return i == 0 ? j : i;
   }

   public void setExpired() {
      this.isExpired = true;
      this.original.setExpired();
   }
}
