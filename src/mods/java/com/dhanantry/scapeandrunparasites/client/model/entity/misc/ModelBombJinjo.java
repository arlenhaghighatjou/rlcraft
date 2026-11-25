package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBombJinjo extends ModelEffect {
   public ModelRenderer mainbody;
   public ModelRenderer b;
   public ModelRenderer b_1;
   public ModelRenderer b_2;
   public ModelRenderer b_3;
   public ModelRenderer b_4;
   public ModelRenderer b_5;
   public ModelRenderer b_6;
   public ModelRenderer b_7;
   public ModelRenderer b_8;
   public ModelRenderer b_9;

   public ModelBombJinjo() {
      this.textureWidth = 135;
      this.textureHeight = 150;
      this.b = new ModelRenderer(this, 0, 0);
      this.b.setRotationPoint(0.0F, 18.5F, 0.0F);
      this.b.addBox(-4.0F, -8.0F, -7.0F, 14, 14, 12, 0.0F);
      this.b_5 = new ModelRenderer(this, 0, 53);
      this.b_5.setRotationPoint(-1.1F, 6.5F, -6.0F);
      this.b_5.addBox(-4.0F, -8.0F, -7.0F, 14, 14, 12, 0.0F);
      this.setRotateAngle(this.b_5, 0.50265485F, 0.0F, 0.0F);
      this.b_4 = new ModelRenderer(this, 40, 30);
      this.b_4.setRotationPoint(1.0F, 2.0F, 0.0F);
      this.b_4.addBox(-4.0F, -12.0F, -3.5F, 15, 22, 6, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(-3.0F, 0.0F, 0.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.b_1 = new ModelRenderer(this, 52, 0);
      this.b_1.setRotationPoint(-8.0F, 4.0F, -5.0F);
      this.b_1.addBox(-4.0F, 0.0F, -4.0F, 10, 15, 15, 0.0F);
      this.setRotateAngle(this.b_1, 0.0F, 0.0F, -0.43982297F);
      this.b_7 = new ModelRenderer(this, 0, 79);
      this.b_7.setRotationPoint(-1.1F, 1.5F, 9.0F);
      this.b_7.addBox(-3.0F, -16.0F, -7.0F, 14, 19, 12, 0.0F);
      this.setRotateAngle(this.b_7, 0.12566371F, 0.0F, 0.0F);
      this.b_3 = new ModelRenderer(this, 0, 26);
      this.b_3.setRotationPoint(-5.0F, -6.0F, 1.0F);
      this.b_3.addBox(-4.0F, -4.5F, -6.0F, 10, 17, 10, 0.0F);
      this.setRotateAngle(this.b_3, 0.0F, 0.0F, 0.12566371F);
      this.b_9 = new ModelRenderer(this, 90, 109);
      this.b_9.setRotationPoint(-1.4F, -8.5F, 2.0F);
      this.b_9.addBox(-1.0F, -16.0F, -7.0F, 8, 14, 10, 0.0F);
      this.setRotateAngle(this.b_9, 0.06283186F, 0.0F, 0.0F);
      this.b_8 = new ModelRenderer(this, 52, 88);
      this.b_8.setRotationPoint(-1.1F, -2.5F, -7.0F);
      this.b_8.addBox(-2.5F, -10.0F, -7.0F, 10, 17, 14, 0.0F);
      this.setRotateAngle(this.b_8, -0.12566371F, 0.0F, 0.0F);
      this.b_2 = new ModelRenderer(this, 92, 20);
      this.b_2.setRotationPoint(8.0F, 5.0F, -1.0F);
      this.b_2.addBox(-4.0F, -12.0F, -5.0F, 11, 27, 10, 0.0F);
      this.setRotateAngle(this.b_2, 0.0F, 0.0F, 0.18849556F);
      this.b_6 = new ModelRenderer(this, 70, 57);
      this.b_6.setRotationPoint(-1.1F, 16.5F, 5.0F);
      this.b_6.addBox(-1.0F, -16.0F, -7.0F, 11, 19, 12, 0.0F);
      this.setRotateAngle(this.b_6, -0.37699112F, 0.0F, 0.0F);
      this.mainbody.addChild(this.b);
      this.mainbody.addChild(this.b_5);
      this.mainbody.addChild(this.b_4);
      this.mainbody.addChild(this.b_1);
      this.mainbody.addChild(this.b_7);
      this.mainbody.addChild(this.b_3);
      this.mainbody.addChild(this.b_9);
      this.mainbody.addChild(this.b_8);
      this.mainbody.addChild(this.b_2);
      this.mainbody.addChild(this.b_6);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
      this.renderTwo(this.mainbody, ageInTicks, f5, 1.0F, 1.0F, 0.1D, 0.8F, 0.05D, 0.1D);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
   }
}
