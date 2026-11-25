package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FBPParticleFlame extends ParticleFlame {
   Minecraft mc;
   double startScale;
   double scaleAlpha;
   double prevParticleScale;
   double prevParticleAlpha;
   double endMult = 1.0D;
   boolean spawnAnother = true;
   Vec3d startPos;
   Vec3d[] cube;
   Vec2f par;

   protected FBPParticleFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mX, double mY, double mZ, boolean spawnAnother) {
      super(worldIn, xCoordIn, yCoordIn - 0.06D, zCoordIn, mX, mY, mZ);
      IBlockState bs = worldIn.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
      this.spawnAnother = spawnAnother;
      if (bs.getBlock() != Blocks.TORCH) {
         spawnAnother = false;
      }

      if (bs == Blocks.TORCH.getDefaultState()) {
         this.prevPosY = this.posY += 0.03999999910593033D;
      }

      this.startPos = new Vec3d(this.posX, this.posY, this.posZ);
      this.mc = Minecraft.getMinecraft();
      this.motionY = -8.500000112690032E-4D;
      this.particleGravity = -0.05F;
      this.particleTexture = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.SNOW.getDefaultState());
      this.particleScale = (float)((double)this.particleScale * FBP.scaleMult * 2.5D);
      this.particleMaxAge = FBP.random.nextInt(3, 5);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 0.0F;
      float angleY = this.rand.nextFloat() * 80.0F;
      this.cube = new Vec3d[FBP.CUBE.length];

      for(int i = 0; i < FBP.CUBE.length; ++i) {
         Vec3d vec = FBP.CUBE[i];
         this.cube[i] = FBPRenderUtil.rotatef_d(vec, 0.0F, angleY, 0.0F);
      }

      this.particleAlpha = 1.0F;
      if (FBP.randomFadingSpeed) {
         this.endMult *= FBP.random.nextDouble(0.9875D, 1.0D);
      }

      this.multipleParticleScaleBy(1.0F);
   }

   public Particle multipleParticleScaleBy(float scale) {
      Particle p = super.multipleParticleScaleBy(scale);
      this.startScale = (double)this.particleScale;
      this.scaleAlpha = (double)this.particleScale * 0.35D;
      float f = this.particleScale / 80.0F;
      this.setBoundingBox(new AxisAlignedBB(this.posX - (double)f, this.posY - (double)f, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f, this.posZ + (double)f));
      return p;
   }

   public int getFXLayer() {
      return 0;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevParticleAlpha = (double)this.particleAlpha;
      this.prevParticleScale = (double)this.particleScale;
      if (!FBP.fancyFlame) {
         this.isExpired = true;
      }

      if (++this.particleAge >= this.particleMaxAge) {
         if (FBP.randomFadingSpeed) {
            this.particleScale = (float)((double)this.particleScale * 0.949999988079071D * this.endMult);
         } else {
            this.particleScale *= 0.95F;
         }

         if ((double)this.particleAlpha > 0.01D && (double)this.particleScale <= this.scaleAlpha) {
            if (FBP.randomFadingSpeed) {
               this.particleAlpha = (float)((double)this.particleAlpha * 0.8999999761581421D * this.endMult);
            } else {
               this.particleAlpha *= 0.9F;
            }
         }

         if ((double)this.particleAlpha <= 0.01D) {
            this.setExpired();
         } else if ((double)this.particleAlpha <= 0.325D && this.spawnAnother && this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock() == Blocks.TORCH) {
            this.spawnAnother = false;
            this.mc.effectRenderer.addEffect(new FBPParticleFlame(this.world, this.startPos.x, this.startPos.y, this.startPos.z, 0.0D, 0.0D, 0.0D, this.spawnAnother));
         }
      }

      this.motionY -= 0.02D * (double)this.particleGravity;
      this.move(0.0D, this.motionY, 0.0D);
      this.motionY *= 0.95D;
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

   public void renderParticle(WorldRenderer buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
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
      if (this.particleAge >= this.particleMaxAge) {
         this.particleGreen = (float)((double)f4 / this.startScale);
      }

      GlStateManager.enableCull();
      this.par = new Vec2f(f, f1);
      Tessellator.getInstance().draw();
      this.mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      buf.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      buf.setTranslation((double)f5, (double)f6, (double)f7);
      this.putCube(buf, (double)(f4 / 80.0F), i >> 16 & '\uffff', i & '\uffff', this.particleRed, this.particleGreen, this.particleBlue, alpha);
      buf.setTranslation(0.0D, 0.0D, 0.0D);
      Tessellator.getInstance().draw();
      Minecraft.getMinecraft().getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
      buf.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
   }

   public void putCube(WorldRenderer worldRendererIn, double scale, int j, int k, float r, float g, float b, float a) {
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
         brightnessForRender = (float)((double)brightnessForRender * 0.95D);
         this.addVt(worldRendererIn, scale, v1, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v2, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v3, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
         this.addVt(worldRendererIn, scale, v4, (double)this.par.x, (double)this.par.y, j, k, R, G, B, a);
      }

   }

   private void addVt(WorldRenderer worldRendererIn, double scale, Vec3d pos, double u, double v, int j, int k, float r, float g, float b, float a) {
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
}
