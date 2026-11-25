package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.EntityParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderParticle extends Render {
   private float scale;
   private int renderTime = 0;
   public EntityParticle entityParticle;

   public RenderParticle(RenderManager renderManager, EntityParticle entityParticle) {
      super(renderManager);
      this.entityParticle = entityParticle;
   }

   public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
      if (this.renderTime++ > 2147483646) {
         this.renderTime = 0;
      }

      this.renderParticle(entity, par2, par4, par6, par8, par9);
   }

   public void renderParticle(Entity entity, double x, double y, double z, float par8, float par9) {
      float scale = 1.0F;

      try {
         scale = ((BaseProjectileEntity)entity).getProjectileScale();
      } catch (Exception var12) {
         ;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F * scale, 0.5F * scale, 0.5F * scale);
      this.bindTexture(this.getEntityTexture(this.entityParticle));
      this.renderTexture(Tessellator.getInstance(), entity);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return entity instanceof EntityParticle ? ((EntityParticle)entity).getTexture() : null;
   }

   private void renderTexture(Tessellator tessellator, Entity entity) {
      double minU = 0.0D;
      double maxU = 1.0D;
      double minV = 0.0D;
      double maxV = 1.0D;
      float textureWidth = 0.00390625F;
      float textureHeight = 0.00390625F;
      if (entity instanceof BaseProjectileEntity) {
         BaseProjectileEntity entityProjectile = (BaseProjectileEntity)entity;
         if (entityProjectile.animationFrameMax > 0) {
            minV = (double)((float)entityProjectile.animationFrame / (float)entityProjectile.animationFrameMax);
            maxV = minV + (double)(1.0F / (float)entityProjectile.animationFrameMax);
         }
      }

      double xCoord = 0.0D;
      double yCoord = 0.0D;
      double zLevel = 0.0D;
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-this.scale / 2.0F, -this.scale / 2.0F, -this.scale / 2.0F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos(xCoord + 0.0D, yCoord + (double)((float)maxV), zLevel).tex((minU + 0.0D) * (double)textureWidth, (minV + maxV) * (double)textureHeight).endVertex();
      vertexbuffer.pos(xCoord + (double)((float)maxU), yCoord + (double)((float)maxV), zLevel).tex((minU + maxU) * (double)textureWidth, (minV + maxV) * (double)textureHeight).endVertex();
      vertexbuffer.pos(xCoord + (double)((float)maxU), yCoord + 0.0D, zLevel).tex((minU + maxU) * (double)textureWidth, (minV + 0.0D) * (double)textureHeight).endVertex();
      vertexbuffer.pos(xCoord + 0.0D, yCoord + 0.0D, zLevel).tex((minU + 0.0D) * (double)textureWidth, (minV + 0.0D) * (double)textureHeight).endVertex();
      tessellator.draw();
   }
}
