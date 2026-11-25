package com.fantasticsource.mctools;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PathedParticle extends Particle {
   protected boolean readyToRender;
   public double u1;
   public double v1;
   public double u2;
   public double v2;
   public boolean useBlockLight;
   public double xScale3D;
   public double yScale3D;
   public double zScale3D;
   protected CPath.CPathData basePath;
   protected CPath.CPathData rgbPath;
   protected CPath.CPathData hsvPath;
   protected CPath.CPathData alphaPath;
   protected CPath.CPathData scale3DPath;
   protected ArrayList<CPath.CPathData> morePaths;

   public PathedParticle(CPath basePath, CPath... morePaths) {
      this(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, basePath, morePaths);
   }

   public PathedParticle(SourceFactor sourceBlend, DestFactor destBlend, CPath basePath, CPath... morePaths) {
      super(Minecraft.getMinecraft().world, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
      this.readyToRender = false;
      this.u1 = 0.25D;
      this.v1 = 0.125D;
      this.u2 = 0.5D;
      this.v2 = 0.375D;
      this.useBlockLight = false;
      this.xScale3D = 1.0D;
      this.yScale3D = 1.0D;
      this.zScale3D = 1.0D;
      this.rgbPath = null;
      this.hsvPath = null;
      this.alphaPath = null;
      this.scale3DPath = null;
      this.morePaths = new ArrayList();
      this.basePath = new CPath.CPathData(basePath);

      for(CPath path : morePaths) {
         this.applyPath(path);
      }

      VectorN pos = this.currentPos();
      this.setPosition(pos.values[0], pos.values[1], pos.values[2]);
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.particleMaxAge = 60;
      this.particleScale = 1.0F;
      this.canCollide = false;
      PathedParticleManager.add(this, sourceBlend, destBlend);
   }

   public PathedParticle applyPath(CPath path) {
      this.morePaths.add(new CPath.CPathData(path));
      return this;
   }

   public PathedParticle useBlockLight(boolean useBlockLight) {
      this.useBlockLight = useBlockLight;
      return this;
   }

   public PathedParticle rgbPath(CPath path) {
      this.rgbPath = new CPath.CPathData(path);
      return this;
   }

   public PathedParticle hsvPath(CPath path) {
      this.hsvPath = new CPath.CPathData(path);
      return this;
   }

   public PathedParticle alphaPath(CPath path) {
      this.alphaPath = new CPath.CPathData(path);
      return this;
   }

   public PathedParticle scale3DPath(CPath path) {
      this.scale3DPath = new CPath.CPathData(path);
      return this;
   }

   public void onUpdate() {
      if (this.particleAge++ > this.particleMaxAge) {
         this.setExpired();
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      VectorN pos = this.currentPos();
      if (pos == null) {
         this.setExpired();
      } else {
         this.setPosition(pos.values[0], pos.values[1], pos.values[2]);
         if (this.rgbPath != null) {
            VectorN rgb = this.rgbPath.getRelativePosition();
            this.setRBGColorF((float)rgb.values[0], (float)rgb.values[1], (float)rgb.values[2]);
         } else if (this.hsvPath != null) {
            VectorN hsv = this.hsvPath.getRelativePosition();
            Color c = (new Color(0)).setColorHSV((float)hsv.values[0], (float)hsv.values[1], (float)hsv.values[2]);
            this.setRBGColorF(c.rf(), c.gf(), c.bf());
         }

         if (this.alphaPath != null) {
            this.setAlphaF((float)this.alphaPath.getRelativePosition().values[0]);
         }

         if (this.scale3DPath != null) {
            VectorN scalar = this.scale3DPath.getRelativePosition();
            this.xScale3D = scalar.values[0];
            this.yScale3D = scalar.values[1];
            this.zScale3D = scalar.values[2];
         }

         this.readyToRender = true;
      }
   }

   protected VectorN currentPos() {
      VectorN pos = this.basePath.getRelativePosition();

      for(CPath.CPathData data : this.morePaths) {
         VectorN pathPos = data.getRelativePosition();
         if (pathPos == null) {
            return null;
         }

         pos.add(pathPos);
      }

      return pos;
   }

   public int getBrightnessForRender(float p_189214_1_) {
      return this.useBlockLight ? super.getBrightnessForRender(p_189214_1_) : 15728880;
   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.readyToRender) {
         double x = this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX;
         double y = this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY;
         double z = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ;
         double scale = (double)(this.particleScale / 2.0F);
         Vec3d[] vecs = new Vec3d[]{(new Vec3d((double)(-rotationX - rotationXY) * this.xScale3D, (double)(-rotationZ) * this.yScale3D, (double)(-rotationYZ - rotationXZ) * this.zScale3D)).scale(scale), (new Vec3d((double)(-rotationX + rotationXY) * this.xScale3D, (double)rotationZ * this.yScale3D, (double)(-rotationYZ + rotationXZ) * this.zScale3D)).scale(scale), (new Vec3d((double)(rotationX + rotationXY) * this.xScale3D, (double)rotationZ * this.yScale3D, (double)(rotationYZ + rotationXZ) * this.zScale3D)).scale(scale), (new Vec3d((double)(rotationX - rotationXY) * this.xScale3D, (double)(-rotationZ) * this.yScale3D, (double)(rotationYZ - rotationXZ) * this.zScale3D)).scale(scale)};
         if (this.particleAngle != 0.0F) {
            float theta = (this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks) * 0.5F;
            float cosTheta = MathHelper.cos(theta);
            double xx = (double)MathHelper.sin(theta) * cameraViewDir.x;
            double yy = (double)MathHelper.sin(theta) * cameraViewDir.y;
            double zz = (double)MathHelper.sin(theta) * cameraViewDir.z;
            Vec3d vec3d = new Vec3d(xx, yy, zz);

            for(int l = 0; l < 4; ++l) {
               vecs[l] = vec3d.scale(2.0D * vecs[l].dotProduct(vec3d)).add(vecs[l].scale((double)(cosTheta * cosTheta) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vecs[l]).scale((double)(2.0F * cosTheta)));
            }
         }

         int lightmapIndex = this.getBrightnessForRender(partialTicks);
         int lightmapX = lightmapIndex >> 16 & '\uffff';
         int lightmapY = lightmapIndex & '\uffff';
         buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(this.u2, this.v2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
         buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(this.u2, this.v1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
         buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(this.u1, this.v1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
         buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(this.u1, this.v2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
      }
   }
}
