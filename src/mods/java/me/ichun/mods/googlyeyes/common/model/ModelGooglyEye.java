package me.ichun.mods.googlyeyes.common.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGooglyEye extends ModelBase {
   public ModelRenderer iris12;
   public ModelRenderer iris11;
   public ModelRenderer iris10;
   public ModelRenderer iris9;
   public ModelRenderer iris8;
   public ModelRenderer iris7;
   public ModelRenderer iris6;
   public ModelRenderer iris5;
   public ModelRenderer iris4;
   public ModelRenderer iris3;
   public ModelRenderer iris2;
   public ModelRenderer iris1;
   public ModelRenderer[] pupils = new ModelRenderer[6];

   public ModelGooglyEye() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.iris6 = new ModelRenderer(this, 0, 0);
      this.iris6.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris6.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris6, 0.0F, -0.0F, -3.1415927F);
      this.iris12 = new ModelRenderer(this, 0, 0);
      this.iris12.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris12.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.iris2 = new ModelRenderer(this, 0, 0);
      this.iris2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris2.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris2, 0.0F, -0.0F, -1.0471976F);
      this.iris1 = new ModelRenderer(this, 0, 0);
      this.iris1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris1.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris1, 0.0F, -0.0F, -0.5235988F);
      this.iris11 = new ModelRenderer(this, 0, 0);
      this.iris11.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris11.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris11, 0.0F, -0.0F, 0.5235988F);
      this.pupils[0] = new ModelRenderer(this, 0, 0);
      this.pupils[0].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[0].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.pupils[0], 0.0F, -0.0F, -1.0471976F);
      this.iris4 = new ModelRenderer(this, 0, 0);
      this.iris4.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris4.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris4, 0.0F, -0.0F, -2.0943952F);
      this.pupils[3] = new ModelRenderer(this, 0, 0);
      this.pupils[3].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[3].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.pupils[3], 0.0F, -0.0F, 2.0943952F);
      this.iris5 = new ModelRenderer(this, 0, 0);
      this.iris5.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris5.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris5, 0.0F, -0.0F, -2.6179938F);
      this.iris7 = new ModelRenderer(this, 0, 0);
      this.iris7.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris7.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris7, 0.0F, -0.0F, 2.6179938F);
      this.pupils[5] = new ModelRenderer(this, 0, 0);
      this.pupils[5].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[5].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.pupils[4] = new ModelRenderer(this, 0, 0);
      this.pupils[4].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[4].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.pupils[4], 0.0F, -0.0F, 1.0471976F);
      this.iris3 = new ModelRenderer(this, 0, 0);
      this.iris3.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris3.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris3, 0.0F, -0.0F, -1.5707964F);
      this.pupils[2] = new ModelRenderer(this, 0, 0);
      this.pupils[2].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[2].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.pupils[2], 0.0F, -0.0F, -3.1415927F);
      this.iris9 = new ModelRenderer(this, 0, 0);
      this.iris9.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris9.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris9, 0.0F, -0.0F, 1.5707964F);
      this.pupils[1] = new ModelRenderer(this, 0, 0);
      this.pupils[1].setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pupils[1].addBox(-0.5F, -0.88F, -1.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.pupils[1], 0.0F, -0.0F, -2.0943952F);
      this.iris10 = new ModelRenderer(this, 0, 0);
      this.iris10.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris10.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris10, 0.0F, 0.0F, 1.0471976F);
      this.iris8 = new ModelRenderer(this, 0, 0);
      this.iris8.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.iris8.addBox(-0.5F, -1.88F, -1.0F, 1, 3, 1, 0.0F);
      this.setRotateAngle(this.iris8, 0.0F, -0.0F, 2.0943952F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
   }

   public void renderIris(float f5) {
      this.iris1.render(f5);
      this.iris2.render(f5);
      this.iris3.render(f5);
      this.iris4.render(f5);
      this.iris5.render(f5);
      this.iris6.render(f5);
      this.iris7.render(f5);
      this.iris8.render(f5);
      this.iris9.render(f5);
      this.iris10.render(f5);
      this.iris11.render(f5);
      this.iris12.render(f5);
   }

   public void renderPupil(float f5) {
      for(int i = 0; i < this.pupils.length; ++i) {
         this.pupils[i].render(f5);
      }

   }

   public void movePupil(float x, float y, float pupilSize) {
      float shiftFactor = (1.45F - pupilSize * 0.525F) / pupilSize;

      for(int i = 0; i < this.pupils.length; ++i) {
         this.pupils[i].rotationPointX = -x * shiftFactor;
         this.pupils[i].rotationPointY = -y * shiftFactor * (float)Math.cos(Math.toRadians((double)(x / 1.0F * 90.0F)));
      }

   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
