package xzeroair.trinkets.client.particles;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleLightning extends Particle {
   List<Vec3d> points = new ArrayList();
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   protected double prevPosX2;
   protected double prevPosY2;
   protected double prevPosZ2;
   protected double posX2;
   protected double posY2;
   protected double posZ2;
   private boolean depth = false;

   public ParticleLightning(World world, double x, double y, double z, int color, float a, boolean bool, float scale) {
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

   public ParticleLightning(World world, double x, double y, double z, double x2, double y2, double z2, int color, float a, boolean bool, float scale) {
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

   public ParticleLightning(World world, double x, double y, double z, double x2, double y2, double z2, double xSpeed, double ySpeed, double zSpeed, int color, float a, boolean bool, float scale) {
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
      new Vec3d((double)x + 0.0D, (double)y + 0.2D, (double)z + 0.0D);
      int i = (int)(((float)this.particleAge + partialTicks) * 15.0F / (float)this.particleMaxAge);
      if (i <= 15) {
         int lmv = 240;
         int lmv2 = 240;

         for(double done = 0.0D; done <= 15.0D; ++done) {
            double alpha = done / 15.0D;
            double x1 = interpolate(this.posX, this.posX2, alpha);
            double y1 = interpolate(this.posY, this.posY2, alpha);
            double z1 = interpolate(this.posZ, this.posZ2, alpha);
            Vec3d test = new Vec3d(x1, y1, z1);
            if (done >= 1.0D) {
               if (done == 1.0D) {
                  this.points.add(test);
               } else if (done < 15.0D) {
                  this.points.add((new LightningVertex(test)).getVec());
               } else {
                  this.points.add(test);
                  this.points.add(new Vec3d(this.posX2, this.posY2, this.posZ2));
               }
            }
         }

         GlStateManager.pushMatrix();
         float r = this.particleRed;
         float g = this.particleGreen;
         float b = this.particleBlue;
         float a = this.particleAlpha;
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         buffer.begin(3, DefaultVertexFormats.POSITION_TEX_COLOR);
         GlStateManager.glLineWidth(this.particleScale);

         for(int p = 0; p < this.points.size() - 1; ++p) {
            int clamped = MathHelper.clamp(p, 0, this.points.size() - 1);
            Vec3d lp = i == 0 ? (Vec3d)this.points.get(clamped) : (Vec3d)this.points.get(clamped);
            float t1 = (float)(lp.x + (((Vec3d)this.points.get(clamped)).x - lp.x) * (double)partialTicks - interpPosX);
            float t2 = (float)(lp.y + (((Vec3d)this.points.get(clamped)).y - lp.y) * (double)partialTicks - interpPosY);
            float t3 = (float)(lp.z + (((Vec3d)this.points.get(clamped)).z - lp.z) * (double)partialTicks - interpPosZ);
            buffer.pos((double)t1, (double)t2, (double)t3).tex(0.0D, 0.0D).color(r, g, b, a).endVertex();
         }

         Tessellator.getInstance().draw();
         buffer.begin(3, DefaultVertexFormats.POSITION_TEX_COLOR);
         GlStateManager.glLineWidth(this.particleScale * 4.0F);

         for(int p = 0; p < this.points.size() - 1; ++p) {
            int clamped = MathHelper.clamp(p, 0, this.points.size() - 1);
            Vec3d lp = i == 0 ? (Vec3d)this.points.get(clamped) : (Vec3d)this.points.get(clamped);
            float t1 = (float)(lp.x + (((Vec3d)this.points.get(clamped)).x - lp.x) * (double)partialTicks - interpPosX);
            float t2 = (float)(lp.y + (((Vec3d)this.points.get(clamped)).y - lp.y) * (double)partialTicks - interpPosY);
            float t3 = (float)(lp.z + (((Vec3d)this.points.get(clamped)).z - lp.z) * (double)partialTicks - interpPosZ);
            buffer.pos((double)t1, (double)t2, (double)t3).tex(0.0D, 0.0D).color(r, g, b, a * 0.5F).endVertex();
         }

         Tessellator.getInstance().draw();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.glLineWidth(2.0F);
         GlStateManager.popMatrix();
         this.points.clear();
      }

   }

   private static double interpolate(double start, double end, double alpha) {
      return start + (end - start) * alpha;
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
