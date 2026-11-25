package com.oblivioussp.spartanweaponry.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelArrowQuiverMedium extends ModelQuiverBase {
   public ModelRenderer quiver;
   public ModelRenderer strap_back;
   public ModelRenderer strap_front;
   public ModelRenderer strap_top;
   public ModelRenderer strap_bottom;
   public ModelRenderer arrow_1_1;
   public ModelRenderer arrow_1_2;
   public ModelRenderer arrow_2_1;
   public ModelRenderer arrow_2_2;
   public ModelRenderer arrow_3_1;
   public ModelRenderer arrow_3_2;

   public ModelArrowQuiverMedium() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.strap_top = new ModelRenderer(this, 0, 18);
      this.strap_top.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.strap_top.addBox(-3.5F, -1.0F, 6.0F, 7, 1, 1, 0.0F);
      this.setRotateAngle(this.strap_top, 0.0F, 1.5707964F, -0.87266463F);
      this.strap_back = new ModelRenderer(this, 0, 14);
      this.strap_back.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.strap_back.addBox(-6.0F, -1.0F, 2.5F, 12, 1, 1, 0.0F);
      this.setRotateAngle(this.strap_back, 0.0F, 0.0F, -0.87266463F);
      this.strap_bottom = new ModelRenderer(this, 0, 20);
      this.strap_bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.strap_bottom.addBox(-3.5F, -1.0F, -7.0F, 7, 1, 1, 0.0F);
      this.setRotateAngle(this.strap_bottom, 0.0F, 1.5707964F, -0.87266463F);
      this.quiver = new ModelRenderer(this, 0, 0);
      this.quiver.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.quiver.addBox(-2.5F, -4.0F, 3.0F, 5, 8, 4, 0.0F);
      this.setRotateAngle(this.quiver, 0.0F, 0.0F, -0.5235988F);
      this.strap_front = new ModelRenderer(this, 0, 16);
      this.strap_front.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.strap_front.addBox(-6.0F, -1.0F, -3.5F, 12, 1, 1, 0.0F);
      this.setRotateAngle(this.strap_front, 0.0F, 0.0F, -0.87266463F);
      this.arrow_1_1 = new ModelRenderer(this, 26, 0);
      this.arrow_1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_1_1.addBox(-12.25F, -11.0F, 9.5F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_1_1, 0.0F, 0.7853982F, -0.5235988F);
      this.arrow_1_2 = new ModelRenderer(this, 26, 0);
      this.arrow_1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_1_2.addBox(8.0F, -11.0F, 10.8F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_1_2, 0.0F, -0.7853982F, -0.5235988F);
      this.arrow_2_1 = new ModelRenderer(this, 26, 0);
      this.arrow_2_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_2_1.addBox(-11.0F, -11.0F, 11.0F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_2_1, 0.0F, 0.7853982F, -0.5235988F);
      this.arrow_2_2 = new ModelRenderer(this, 26, 0);
      this.arrow_2_2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_2_2.addBox(9.5F, -11.0F, 9.5F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_2_2, 0.0F, -0.7853982F, -0.5235988F);
      this.arrow_3_1 = new ModelRenderer(this, 26, 0);
      this.arrow_3_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_3_1.addBox(-9.5F, -11.0F, 8.0F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_3_1, 0.0F, 0.7853982F, -0.5235988F);
      this.arrow_3_2 = new ModelRenderer(this, 26, 0);
      this.arrow_3_2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.arrow_3_2.addBox(6.5F, -11.0F, 8.0F, 3, 6, 0, 0.0F);
      this.setRotateAngle(this.arrow_3_2, 0.0F, -0.7853982F, -0.5235988F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
      this.render(scale);
   }

   public void render(float scale) {
      this.strap_back.render(scale);
      this.quiver.render(scale);
      this.strap_front.render(scale);
      this.strap_top.render(scale);
      this.strap_bottom.render(scale);
      this.renderArrows(scale);
   }

   protected void renderArrows(float scale) {
      GlStateManager.disableLighting();
      if (this.arrowsToRender >= 1) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_1_1.offsetX, this.arrow_1_1.offsetY, this.arrow_1_1.offsetZ);
         GlStateManager.translate(this.arrow_1_1.rotationPointX * scale, this.arrow_1_1.rotationPointY * scale, this.arrow_1_1.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_1_1.offsetX, -this.arrow_1_1.offsetY, -this.arrow_1_1.offsetZ);
         GlStateManager.translate(-this.arrow_1_1.rotationPointX * scale, -this.arrow_1_1.rotationPointY * scale, -this.arrow_1_1.rotationPointZ * scale);
         this.arrow_1_1.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_1_2.offsetX, this.arrow_1_2.offsetY, this.arrow_1_2.offsetZ);
         GlStateManager.translate(this.arrow_1_2.rotationPointX * scale, this.arrow_1_2.rotationPointY * scale, this.arrow_1_2.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_1_2.offsetX, -this.arrow_1_2.offsetY, -this.arrow_1_2.offsetZ);
         GlStateManager.translate(-this.arrow_1_2.rotationPointX * scale, -this.arrow_1_2.rotationPointY * scale, -this.arrow_1_2.rotationPointZ * scale);
         this.arrow_1_2.render(scale);
         GlStateManager.popMatrix();
      }

      if (this.arrowsToRender >= 2) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_2_1.offsetX, this.arrow_2_1.offsetY, this.arrow_2_1.offsetZ);
         GlStateManager.translate(this.arrow_2_1.rotationPointX * scale, this.arrow_2_1.rotationPointY * scale, this.arrow_2_1.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_2_1.offsetX, -this.arrow_2_1.offsetY, -this.arrow_2_1.offsetZ);
         GlStateManager.translate(-this.arrow_2_1.rotationPointX * scale, -this.arrow_2_1.rotationPointY * scale, -this.arrow_2_1.rotationPointZ * scale);
         this.arrow_2_1.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_2_2.offsetX, this.arrow_2_2.offsetY, this.arrow_2_2.offsetZ);
         GlStateManager.translate(this.arrow_2_2.rotationPointX * scale, this.arrow_2_2.rotationPointY * scale, this.arrow_2_2.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_2_2.offsetX, -this.arrow_2_2.offsetY, -this.arrow_2_2.offsetZ);
         GlStateManager.translate(-this.arrow_2_2.rotationPointX * scale, -this.arrow_2_2.rotationPointY * scale, -this.arrow_2_2.rotationPointZ * scale);
         this.arrow_2_2.render(scale);
         GlStateManager.popMatrix();
      }

      if (this.arrowsToRender >= 3) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_3_1.offsetX, this.arrow_3_1.offsetY, this.arrow_3_1.offsetZ);
         GlStateManager.translate(this.arrow_3_1.rotationPointX * scale, this.arrow_3_1.rotationPointY * scale, this.arrow_3_1.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_3_1.offsetX, -this.arrow_3_1.offsetY, -this.arrow_3_1.offsetZ);
         GlStateManager.translate(-this.arrow_3_1.rotationPointX * scale, -this.arrow_3_1.rotationPointY * scale, -this.arrow_3_1.rotationPointZ * scale);
         this.arrow_3_1.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.arrow_3_2.offsetX, this.arrow_3_2.offsetY, this.arrow_3_2.offsetZ);
         GlStateManager.translate(this.arrow_3_2.rotationPointX * scale, this.arrow_3_2.rotationPointY * scale, this.arrow_3_2.rotationPointZ * scale);
         GlStateManager.scale(0.75D, 0.75D, 0.4D);
         GlStateManager.translate(-this.arrow_3_2.offsetX, -this.arrow_3_2.offsetY, -this.arrow_3_2.offsetZ);
         GlStateManager.translate(-this.arrow_3_2.rotationPointX * scale, -this.arrow_3_2.rotationPointY * scale, -this.arrow_3_2.rotationPointZ * scale);
         this.arrow_3_2.render(scale);
         GlStateManager.popMatrix();
      }

      GlStateManager.enableLighting();
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
