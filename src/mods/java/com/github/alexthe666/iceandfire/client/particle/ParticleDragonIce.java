package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFlame;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleDragonIce extends ParticleFlame {
   private static final ResourceLocation SNOWFLAKE_TEXTURE_SMALL = new ResourceLocation("iceandfire:textures/particles/snowflake_0.png");
   private static final ResourceLocation SNOWFLAKE_TEXTURE_LARGE = new ResourceLocation("iceandfire:textures/particles/snowflake_1.png");
   private static final ResourceLocation MINECRAFT_TEXTURES = new ResourceLocation("textures/particle/particles.png");
   private boolean big;

   @SideOnly(Side.CLIENT)
   public ParticleDragonIce(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.setParticleTextureIndex(0);
      this.particleMaxAge = 7;
      this.big = this.rand.nextBoolean();
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      this.particleTextureIndexX = particleTextureIndex % 16;
      this.particleTextureIndexY = particleTextureIndex / 16;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.particleAge > 7) {
         this.setExpired();
      }

      this.particleScale = 1.0F * (float)(this.particleMaxAge - this.particleAge);
      float f3 = (float)(this.posX - interpPosX);
      float f4 = (float)(this.posY - interpPosY);
      float f5 = (float)(this.posZ - interpPosZ);
      float f6 = this.world.getLightBrightness(new BlockPos(this.posX, this.posY, this.posZ));
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      float width = this.particleScale * 0.1F;
      Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * width - rotationXY * width), (double)(-rotationZ * width), (double)(-rotationYZ * width - rotationXZ * width)), new Vec3d((double)(-rotationX * width + rotationXY * width), (double)(rotationZ * width), (double)(-rotationYZ * width + rotationXZ * width)), new Vec3d((double)(rotationX * width + rotationXY * width), (double)(rotationZ * width), (double)(rotationYZ * width + rotationXZ * width)), new Vec3d((double)(rotationX * width - rotationXY * width), (double)(-rotationZ * width), (double)(rotationYZ * width - rotationXZ * width))};
      if (this.particleAngle != 0.0F) {
         float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
         float f9 = MathHelper.cos(f8 * 0.5F);
         float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
         float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
         float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
         Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

         for(int l = 0; l < 4; ++l) {
            avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
         }
      }

      if (this.big) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(SNOWFLAKE_TEXTURE_LARGE);
      } else {
         Minecraft.getMinecraft().getTextureManager().bindTexture(SNOWFLAKE_TEXTURE_SMALL);
      }

      buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos((double)f3 + avec3d[0].x, (double)f4 + avec3d[0].y, (double)f5 + avec3d[0].z).tex(0.0D, 1.0D).color(f6, f6, f6, 1.0F).endVertex();
      buffer.pos((double)f3 + avec3d[1].x, (double)f4 + avec3d[1].y, (double)f5 + avec3d[1].z).tex(1.0D, 1.0D).color(f6, f6, f6, 1.0F).endVertex();
      buffer.pos((double)f3 + avec3d[2].x, (double)f4 + avec3d[2].y, (double)f5 + avec3d[2].z).tex(1.0D, 0.0D).color(f6, f6, f6, 1.0F).endVertex();
      buffer.pos((double)f3 + avec3d[3].x, (double)f4 + avec3d[3].y, (double)f5 + avec3d[3].z).tex(0.0D, 0.0D).color(f6, f6, f6, 1.0F).endVertex();
      Tessellator.getInstance().draw();
   }

   public int getFXLayer() {
      return 3;
   }

   public void onUpdate() {
      super.onUpdate();
      this.particleAngle = MathHelper.sin((float)this.particleAge * 0.2F) / 4.0F;
   }
}
