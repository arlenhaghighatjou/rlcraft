package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.EntityParticle;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileSprite extends Render {
   private float scale;
   private int renderTime = 0;
   Class projectileClass;
   protected ModelBase laserModel = new ModelBase() {
   };
   private ModelRenderer laserBox;

   public RenderProjectileSprite(RenderManager renderManager, Class projectileClass) {
      super(renderManager);
      this.projectileClass = projectileClass;
   }

   public void doRender(Entity entity, double x, double y, double z, float par8, float par9) {
      if (this.renderTime++ > 2147483646) {
         this.renderTime = 0;
      }

      if (entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).getLaserEnd() != null) {
         this.renderLaser((CustomProjectileEntity)entity, x, y, z, par8, par9, ((CustomProjectileEntity)entity).laserWidth);
      } else if (entity instanceof EntityProjectileLaser) {
         this.renderOldLaser((BaseProjectileEntity)entity, ((EntityProjectileLaser)entity).getLaserEnd(), x, y, z, par8, par9, ((EntityProjectileLaser)entity).getLaserWidth(), ((EntityProjectileLaser)entity).getLength());
      } else {
         this.renderProjectile(entity, x, y, z, par8, par9);
      }
   }

   public void renderProjectile(Entity entity, double x, double y, double z, float par8, float par9) {
      double scale = 1.0D;
      if (!(entity instanceof CustomProjectileEntity) || ((CustomProjectileEntity)entity).projectileInfo != null) {
         if (entity instanceof BaseProjectileEntity) {
            BaseProjectileEntity baseProjectileEntity = (BaseProjectileEntity)entity;
            scale *= (double)baseProjectileEntity.getProjectileScale();
            y += (double)baseProjectileEntity.getTextureOffsetY();
         }

         ResourceLocation texture = this.getEntityTexture(entity);
         if (texture != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(scale, scale, scale);
            this.bindTexture(texture);
            this.renderTexture(Tessellator.getInstance(), entity);
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
         }
      }
   }

   private void renderTexture(Tessellator tessellator, Entity entity) {
      double minU = 0.0D;
      double maxU = 1.0D;
      double minV = 0.0D;
      double maxV = 1.0D;
      double textureWidth = 0.5D;
      double textureHeight = 0.5D;
      double offsetY = 0.0D;
      if (entity instanceof BaseProjectileEntity) {
         BaseProjectileEntity entityProjectile = (BaseProjectileEntity)entity;
         if (entityProjectile.animationFrameMax > 0) {
            minV = (double)((float)entityProjectile.animationFrame / (float)entityProjectile.animationFrameMax);
            maxV = minV + (double)(1.0F / (float)entityProjectile.animationFrameMax);
            textureWidth *= (double)entityProjectile.textureScale;
            textureHeight *= (double)entityProjectile.textureScale;
            offsetY = (double)entityProjectile.textureOffsetY;
         }
      }

      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-this.scale / 2.0F, -this.scale / 2.0F, -this.scale / 2.0F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      WorldRenderer vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      vertexbuffer.pos(-textureWidth, -textureHeight + textureHeight / 2.0D + offsetY, 0.0D).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
      vertexbuffer.pos(textureWidth, -textureHeight + textureHeight / 2.0D + offsetY, 0.0D).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
      vertexbuffer.pos(textureWidth, textureHeight + textureHeight / 2.0D + offsetY, 0.0D).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
      vertexbuffer.pos(-textureWidth, textureHeight + textureHeight / 2.0D + offsetY, 0.0D).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
      tessellator.draw();
   }

   public void renderLaser(CustomProjectileEntity entity, double x, double y, double z, float par8, float par9, float scale) {
      if (this.laserBox == null) {
         this.laserBox = new ModelRenderer(this.laserModel, 0, 0);
         this.laserBox.addBox(-(scale / 2.0F), -(scale / 2.0F), 0.0F, (int)scale, (int)scale, 16);
         this.laserBox.rotationPointX = 0.0F;
         this.laserBox.rotationPointY = 0.0F;
         this.laserBox.rotationPointZ = 0.0F;
      }

      float factor = 0.0625F;
      float lastSegment = 0.0F;
      double length = entity.getPositionVector().distanceTo(entity.getLaserEnd());
      if (length > 0.0D) {
         GlStateManager.pushMatrix();
         GlStateManager.enableAlpha();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.translate(x, y, z);
         this.bindTexture(this.getLaserTexture(entity));
         float[] angles = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
         float dx = (float)(entity.getLaserEnd().x - entity.posX);
         float dy = (float)(entity.getLaserEnd().y - entity.posY);
         float dz = (float)(entity.getLaserEnd().z - entity.posZ);
         angles[0] = (float)Math.toDegrees(Math.atan2((double)dz, (double)dy)) - 90.0F;
         angles[1] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dz));
         angles[2] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dy)) - 90.0F;
         float dr = (float)Math.sqrt((double)(dx * dx + dz * dz));
         angles[3] = (float)Math.toDegrees(Math.atan2((double)dr, (double)dy)) - 90.0F;
         GlStateManager.rotate(angles[1], 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(angles[3], 1.0F, 0.0F, 0.0F);

         for(float segment = 0.0F; (double)segment <= length - 1.0D; lastSegment = segment++) {
            this.laserBox.render(factor);
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
         }

         ++lastSegment;
         GlStateManager.scale(length - (double)lastSegment, 1.0D, 1.0D);
         this.laserBox.render(factor);
         GlStateManager.popMatrix();
      }
   }

   public void renderOldLaser(BaseProjectileEntity entity, LaserEndProjectileEntity laserEnd, double x, double y, double z, float par8, float par9, float scale, float length) {
      if (this.laserBox == null) {
         this.laserBox = new ModelRenderer(this.laserModel, 0, 0);
         this.laserBox.addBox(-(scale / 2.0F), -(scale / 2.0F), 0.0F, (int)scale, (int)scale, 16);
         this.laserBox.rotationPointX = 0.0F;
         this.laserBox.rotationPointY = 0.0F;
         this.laserBox.rotationPointZ = 0.0F;
      }

      float factor = 0.0625F;
      float lastSegment = 0.0F;
      if (length > 0.0F) {
         GlStateManager.pushMatrix();
         GlStateManager.enableAlpha();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.translate(x, y, z);
         this.bindTexture(this.getLaserTexture(entity));
         float[] angles = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
         if (laserEnd != null) {
            float dx = (float)(laserEnd.posX - entity.posX);
            float dy = (float)(laserEnd.posY - entity.posY);
            float dz = (float)(laserEnd.posZ - entity.posZ);
            angles[0] = (float)Math.toDegrees(Math.atan2((double)dz, (double)dy)) - 90.0F;
            angles[1] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dz));
            angles[2] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dy)) - 90.0F;
            float dr = (float)Math.sqrt((double)(dx * dx + dz * dz));
            angles[3] = (float)Math.toDegrees(Math.atan2((double)dr, (double)dy)) - 90.0F;
         }

         GlStateManager.rotate(angles[1], 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(angles[3], 1.0F, 0.0F, 0.0F);

         for(float segment = 0.0F; segment <= length - 1.0F; lastSegment = segment++) {
            this.laserBox.render(factor);
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
         }

         ++lastSegment;
         GlStateManager.scale(length - lastSegment, 1.0F, 1.0F);
         this.laserBox.render(factor);
         GlStateManager.popMatrix();
      }
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      if (entity instanceof BaseProjectileEntity) {
         return ((BaseProjectileEntity)entity).getTexture();
      } else {
         return entity instanceof EntityParticle ? ((EntityParticle)entity).getTexture() : null;
      }
   }

   protected ResourceLocation getLaserTexture(BaseProjectileEntity entity) {
      if (entity instanceof CustomProjectileEntity) {
         return ((CustomProjectileEntity)entity).getBeamTexture();
      } else {
         return entity instanceof EntityProjectileLaser ? ((EntityProjectileLaser)entity).getBeamTexture() : this.getEntityTexture(entity);
      }
   }
}
