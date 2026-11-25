package net.ilexiconn.llibrary.client.model.tools;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Model3DTexture extends ModelBox {
   private int width;
   private int height;
   private float u1;
   private float v1;
   private float u2;
   private float v2;

   public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height) {
      super(model, 0, 0, posX, posY, posZ, 0, 0, 0, 0.0F);
      this.width = width;
      this.height = height;
      this.u1 = (float)textureOffsetX / model.textureWidth;
      this.v1 = (float)textureOffsetY / model.textureHeight;
      this.u2 = (float)(textureOffsetX + width) / model.textureWidth;
      this.v2 = (float)(textureOffsetY + height) / model.textureHeight;
   }

   public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, int width, int height) {
      this(model, textureOffsetX, textureOffsetY, 0.0F, 0.0F, 0.0F, width, height);
   }

   public void render(WorldRenderer WorldRenderer, float scale) {
      Tessellator tessellator = Tessellator.getInstance();
      GlStateManager.pushMatrix();
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(this.posX1 * scale, this.posY1 * scale, this.posZ1 * scale);
      GlStateManager.scale((float)this.width / 16.0F, (float)this.height / 16.0F, 1.0F);
      float depth = 0.0625F;
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      WorldRenderer.pos(0.0D, 0.0D, 0.0D).tex((double)this.u1, (double)this.v2).normal(0.0F, 0.0F, 1.0F).endVertex();
      WorldRenderer.pos(1.0D, 0.0D, 0.0D).tex((double)this.u2, (double)this.v2).normal(0.0F, 0.0F, 1.0F).endVertex();
      WorldRenderer.pos(1.0D, 1.0D, 0.0D).tex((double)this.u2, (double)this.v1).normal(0.0F, 0.0F, 1.0F).endVertex();
      WorldRenderer.pos(0.0D, 1.0D, 0.0D).tex((double)this.u1, (double)this.v1).normal(0.0F, 0.0F, 1.0F).endVertex();
      tessellator.draw();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      WorldRenderer.pos(0.0D, 1.0D, (double)(0.0F - depth)).tex((double)this.u1, (double)this.v1).normal(0.0F, 0.0F, -1.0F).endVertex();
      WorldRenderer.pos(1.0D, 1.0D, (double)(0.0F - depth)).tex((double)this.u2, (double)this.v1).normal(0.0F, 0.0F, -1.0F).endVertex();
      WorldRenderer.pos(1.0D, 0.0D, (double)(0.0F - depth)).tex((double)this.u2, (double)this.v2).normal(0.0F, 0.0F, -1.0F).endVertex();
      WorldRenderer.pos(0.0D, 0.0D, (double)(0.0F - depth)).tex((double)this.u1, (double)this.v2).normal(0.0F, 0.0F, -1.0F).endVertex();
      tessellator.draw();
      float f5 = 0.5F * (this.u1 - this.u2) / (float)this.width;
      float f6 = 0.5F * (this.v2 - this.v1) / (float)this.height;
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int k = 0; k < this.width; ++k) {
         float f7 = (float)k / (float)this.width;
         float f8 = this.u1 + (this.u2 - this.u1) * f7 - f5;
         WorldRenderer.pos((double)f7, 0.0D, (double)(0.0F - depth)).tex((double)f8, (double)this.v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f7, 0.0D, 0.0D).tex((double)f8, (double)this.v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f7, 1.0D, 0.0D).tex((double)f8, (double)this.v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f7, 1.0D, (double)(0.0F - depth)).tex((double)f8, (double)this.v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
      }

      tessellator.draw();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var11 = 0; var11 < this.width; ++var11) {
         float f7 = (float)var11 / (float)this.width;
         float f8 = this.u1 + (this.u2 - this.u1) * f7 - f5;
         float f9 = f7 + 1.0F / (float)this.width;
         WorldRenderer.pos((double)f9, 1.0D, (double)(0.0F - depth)).tex((double)f8, (double)this.v1).normal(1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f9, 1.0D, 0.0D).tex((double)f8, (double)this.v1).normal(1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f9, 0.0D, 0.0D).tex((double)f8, (double)this.v2).normal(1.0F, 0.0F, 0.0F).endVertex();
         WorldRenderer.pos((double)f9, 0.0D, (double)(0.0F - depth)).tex((double)f8, (double)this.v2).normal(1.0F, 0.0F, 0.0F).endVertex();
      }

      tessellator.draw();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var12 = 0; var12 < this.height; ++var12) {
         float f7 = (float)var12 / (float)this.height;
         float f8 = this.v2 + (this.v1 - this.v2) * f7 - f6;
         float f9 = f7 + 1.0F / (float)this.height;
         WorldRenderer.pos(0.0D, (double)f9, 0.0D).tex((double)this.u1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         WorldRenderer.pos(1.0D, (double)f9, 0.0D).tex((double)this.u2, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         WorldRenderer.pos(1.0D, (double)f9, (double)(0.0F - depth)).tex((double)this.u2, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         WorldRenderer.pos(0.0D, (double)f9, (double)(0.0F - depth)).tex((double)this.u1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
      }

      tessellator.draw();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var13 = 0; var13 < this.height; ++var13) {
         float f7 = (float)var13 / (float)this.height;
         float f8 = this.v2 + (this.v1 - this.v2) * f7 - f6;
         WorldRenderer.pos(1.0D, (double)f7, 0.0D).tex((double)this.u2, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         WorldRenderer.pos(0.0D, (double)f7, 0.0D).tex((double)this.u1, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         WorldRenderer.pos(0.0D, (double)f7, (double)(0.0F - depth)).tex((double)this.u1, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         WorldRenderer.pos(1.0D, (double)f7, (double)(0.0F - depth)).tex((double)this.u2, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
      }

      tessellator.draw();
      GlStateManager.popMatrix();
   }
}
