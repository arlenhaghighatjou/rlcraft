package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBombHost extends ModelEffect {
   public ModelRenderer mainbody;
   public ModelRenderer dec;
   public ModelRenderer dec_1;
   public ModelRenderer dec_2;
   public ModelRenderer dec_3;
   public ModelRenderer mainbody_1;
   public ModelRenderer dec_4;
   public ModelRenderer dec_5;
   public ModelRenderer dec_6;
   public ModelRenderer dec_7;

   public ModelBombHost() {
      this.textureWidth = 64;
      this.textureHeight = 16;
      this.dec = new ModelRenderer(this, 16, 0);
      this.dec.setRotationPoint(-2.0F, -2.0F, -2.0F);
      this.dec.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec, -0.36651915F, 0.7853982F, 0.0F);
      this.dec_4 = new ModelRenderer(this, 29, 6);
      this.dec_4.setRotationPoint(-2.0F, -2.0F, -2.0F);
      this.dec_4.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_4, -0.36651915F, 0.7853982F, 0.0F);
      this.dec_6 = new ModelRenderer(this, 38, 9);
      this.dec_6.setRotationPoint(2.0F, 2.0F, -2.0F);
      this.dec_6.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_6, 0.36651915F, -0.7853982F, 0.0F);
      this.mainbody_1 = new ModelRenderer(this, 13, 6);
      this.mainbody_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.mainbody_1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 3, 0.0F);
      this.setRotateAngle(this.mainbody_1, 0.0F, 3.1415927F, 0.0F);
      this.dec_7 = new ModelRenderer(this, 50, 9);
      this.dec_7.setRotationPoint(-2.0F, 2.0F, -2.0F);
      this.dec_7.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_7, 0.36651915F, 0.7853982F, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.mainbody.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 3, 0.0F);
      this.dec_5 = new ModelRenderer(this, 0, 8);
      this.dec_5.setRotationPoint(2.0F, -2.0F, -2.0F);
      this.dec_5.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_5, -0.36651915F, -0.7853982F, 0.0F);
      this.dec_1 = new ModelRenderer(this, 28, 0);
      this.dec_1.setRotationPoint(2.0F, -2.0F, -2.0F);
      this.dec_1.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_1, -0.36651915F, -0.7853982F, 0.0F);
      this.dec_2 = new ModelRenderer(this, 40, 0);
      this.dec_2.setRotationPoint(2.0F, 2.0F, -2.0F);
      this.dec_2.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_2, 0.36651915F, -0.7853982F, 0.0F);
      this.dec_3 = new ModelRenderer(this, 49, 3);
      this.dec_3.setRotationPoint(-2.0F, 2.0F, -2.0F);
      this.dec_3.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 3, 0.0F);
      this.setRotateAngle(this.dec_3, 0.36651915F, 0.7853982F, 0.0F);
      this.mainbody.addChild(this.dec);
      this.mainbody_1.addChild(this.dec_4);
      this.mainbody_1.addChild(this.dec_6);
      this.mainbody.addChild(this.mainbody_1);
      this.mainbody_1.addChild(this.dec_7);
      this.mainbody_1.addChild(this.dec_5);
      this.mainbody.addChild(this.dec_1);
      this.mainbody.addChild(this.dec_2);
      this.mainbody.addChild(this.dec_3);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
      this.renderTwo(this.mainbody, ageInTicks, f5, 1.0F, 1.0F, 0.1D, 0.8F, 0.05D, 0.1D);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
   }
}
