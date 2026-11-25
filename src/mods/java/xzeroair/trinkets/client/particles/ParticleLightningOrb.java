package xzeroair.trinkets.client.particles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleLightningOrb extends Particle {
   List<Vec3d> points = new ArrayList();
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   protected double prevPosX2;
   protected double prevPosY2;
   protected double prevPosZ2;
   protected double posX2;
   protected double posY2;
   protected double posZ2;
   private boolean depth = false;

   public ParticleLightningOrb(World world, double x, double y, double z, int color, float a, boolean bool, float scale) {
      super(world, x, y, z);
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      this.particleMaxAge = 16;
      this.particleAge = 0;
      this.particleScale = scale;
   }

   public ParticleLightningOrb(World world, double x, double y, double z, double x2, double y2, double z2, int color, float a, boolean bool, float scale) {
      super(world, x, y, z);
      this.posX2 = x2;
      this.posY2 = y2;
      this.posZ2 = z2;
      this.prevPosX2 = x2;
      this.prevPosY2 = y2;
      this.prevPosZ2 = z2;
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      this.particleMaxAge = 16;
      this.particleAge = 0;
      this.particleScale = scale;
   }

   public ParticleLightningOrb(World world, double x, double y, double z, double x2, double y2, double z2, double xSpeed, double ySpeed, double zSpeed, int color, float a, boolean bool, float scale) {
      super(world, x, y, z, xSpeed, ySpeed, zSpeed);
      this.posX2 = x2;
      this.posY2 = y2;
      this.posZ2 = z2;
      this.prevPosX2 = x2;
      this.prevPosY2 = y2;
      this.prevPosZ2 = z2;
      int r = (color & 16711680) >> 16;
      int g = (color & '\uff00') >> 8;
      int b = (color & 255) >> 0;
      this.particleRed = (float)r / 255.0F;
      this.particleGreen = (float)g / 255.0F;
      this.particleBlue = (float)b / 255.0F;
      this.particleAlpha = a;
      this.depth = bool;
      this.particleMaxAge = 16;
      this.particleAge = 0;
      this.particleScale = scale;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      Vec3d origin = new Vec3d((double)x + 0.0D, (double)y + 0.2D, (double)z + 0.0D);
      int i = (int)(((float)this.particleAge + partialTicks) * 15.0F / (float)this.particleMaxAge);
      if (i <= 15) {
         int lmv = 240;
         int lmv2 = 240;

         for(int p = 0; p < this.particleMaxAge; ++p) {
            if (p < 1) {
               this.points.add(origin);
            } else {
               Vec3d lP = (Vec3d)this.points.get(p - 1);
               Vec3d next = (new LightningVertex(lP)).getVec();
               this.points.add(next);
            }
         }

         float r = this.particleRed;
         float g = this.particleGreen;
         float b = this.particleBlue;
         float a = this.particleAlpha;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         buffer.begin(3, DefaultVertexFormats.POSITION_TEX_COLOR);
         GlStateManager.glLineWidth(2.0F);

         for(int p = 0; p < this.points.size() - 1; ++p) {
            int clamped = MathHelper.clamp(p, 0, this.points.size() - 1);
            Vec3d lp = i == 0 ? (Vec3d)this.points.get(clamped) : (Vec3d)this.points.get(clamped);
            buffer.pos(lp.x, lp.y, lp.z).tex(0.0D, 0.0D).color(r, g, b, a).endVertex();
         }

         Tessellator.getInstance().draw();
         buffer.begin(3, DefaultVertexFormats.POSITION_TEX_COLOR);
         GlStateManager.glLineWidth(8.0F);

         for(int p = 0; p < this.points.size() - 1; ++p) {
            int clamped = MathHelper.clamp(p, 0, this.points.size() - 1);
            Vec3d lp = i == 0 ? (Vec3d)this.points.get(clamped) : (Vec3d)this.points.get(clamped);
            buffer.pos(lp.x, lp.y, lp.z).tex(0.0D, 0.0D).color(r, g, b, a * 0.5F).endVertex();
         }

         Tessellator.getInstance().draw();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.glLineWidth(2.0F);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("xat:textures/particle/greed.png"));
         float f = (float)(i % 16) / 16.0F;
         float f1 = f + 0.0625F;
         float f2 = (float)(i / 16) / 16.0F;
         float f3 = f2 + 0.0625F;
         float f4 = 0.5F;
         buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
         buffer.pos((double)(x - rotationX * 0.5F - rotationXY * 0.5F), (double)(y - rotationZ * 0.5F), (double)(z - rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
         buffer.pos((double)(x - rotationX * 0.5F + rotationXY * 0.5F), (double)(y + rotationZ * 0.5F), (double)(z - rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
         buffer.pos((double)(x + rotationX * 0.5F + rotationXY * 0.5F), (double)(y + rotationZ * 0.5F), (double)(z + rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
         buffer.pos((double)(x + rotationX * 0.5F - rotationXY * 0.5F), (double)(y - rotationZ * 0.5F), (double)(z + rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.glLineWidth(2.0F);
         GlStateManager.popMatrix();
         this.points.clear();
      }

   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

   }

   public int getFXLayer() {
      return 3;
   }
}
