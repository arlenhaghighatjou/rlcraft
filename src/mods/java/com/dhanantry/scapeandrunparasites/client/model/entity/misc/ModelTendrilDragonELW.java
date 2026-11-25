package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTendrilDragonELW extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer jointLW1;
   public ModelRenderer jd;
   public ModelRenderer jointLW1_1;
   public ModelRenderer w;
   public ModelRenderer skin;
   public ModelRenderer jointLW2;
   public ModelRenderer dec;
   public ModelRenderer dec_1;
   public ModelRenderer dec_2;
   public ModelRenderer w_1;
   public ModelRenderer skin_1;
   public ModelRenderer dec_3;

   public ModelTendrilDragonELW() {
      this.textureWidth = 200;
      this.textureHeight = 150;
      this.jointLW1 = new ModelRenderer(this, 4, 0);
      this.jointLW1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jointLW1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.w_1 = new ModelRenderer(this, 160, 6);
      this.w_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.w_1.addBox(-15.0F, -2.0F, -2.0F, 15, 4, 4, 0.0F);
      this.setRotateAngle(this.w_1, 0.0F, 0.0F, -0.9536479F);
      this.skin = new ModelRenderer(this, -49, 24);
      this.skin.setRotationPoint(0.0F, 0.0F, 2.0F);
      this.skin.addBox(-60.0F, 0.0F, 0.0F, 60, 0, 58, 0.0F);
      this.w = new ModelRenderer(this, 8, 0);
      this.w.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.w.addBox(-38.0F, -4.0F, -4.0F, 38, 8, 8, 0.0F);
      this.setRotateAngle(this.w, -0.29670596F, 0.0F, -0.08726646F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(-23.0F, 22.0F, -22.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.mainbody, -0.25132743F, 0.0F, 0.0F);
      this.jointLW2 = new ModelRenderer(this, 92, 0);
      this.jointLW2.setRotationPoint(-60.0F, 0.0F, 0.0F);
      this.jointLW2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.jd = new ModelRenderer(this, 8, 0);
      this.jd.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jd.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.jd, 0.0F, 0.24993114F, 3.1415927F);
      this.skin_1 = new ModelRenderer(this, 16, 85);
      this.skin_1.setRotationPoint(-56.0F, 0.0F, 0.0F);
      this.skin_1.addBox(-56.0F, 0.0F, 2.0F, 60, 0, 60, 0.0F);
      this.setRotateAngle(this.skin_1, 0.0F, 0.0F, -3.1415927F);
      this.dec_1 = new ModelRenderer(this, 154, 0);
      this.dec_1.setRotationPoint(-6.0F, -4.6F, -0.5F);
      this.dec_1.addBox(-1.5F, -1.0F, -1.0F, 3, 5, 2, 0.0F);
      this.setRotateAngle(this.dec_1, 1.5707964F, -1.8221238F, 0.0F);
      this.dec_2 = new ModelRenderer(this, 164, 0);
      this.dec_2.setRotationPoint(-4.0F, -0.6F, -4.5F);
      this.dec_2.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 2, 0.0F);
      this.setRotateAngle(this.dec_2, 0.0F, 0.0F, 0.50265485F);
      this.jointLW1_1 = new ModelRenderer(this, 12, 0);
      this.jointLW1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jointLW1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.dec_3 = new ModelRenderer(this, 97, 14);
      this.dec_3.setRotationPoint(-16.0F, 0.0F, 0.0F);
      this.dec_3.addBox(-1.0F, -1.0F, -2.0F, 47, 2, 3, 0.0F);
      this.setRotateAngle(this.dec_3, 0.0F, 3.1415927F, 0.0F);
      this.dec = new ModelRenderer(this, 100, 0);
      this.dec.setRotationPoint(-39.0F, 0.0F, 0.0F);
      this.dec.addBox(-1.0F, -1.0F, -2.5F, 22, 4, 5, 0.0F);
      this.setRotateAngle(this.dec, 0.0F, 3.1415927F, 0.0F);
      this.mainbody.addChild(this.jointLW1);
      this.jointLW2.addChild(this.w_1);
      this.w.addChild(this.skin);
      this.jointLW1_1.addChild(this.w);
      this.w.addChild(this.jointLW2);
      this.jointLW1.addChild(this.jd);
      this.w_1.addChild(this.skin_1);
      this.w.addChild(this.dec_1);
      this.w.addChild(this.dec_2);
      this.jd.addChild(this.jointLW1_1);
      this.w_1.addChild(this.dec_3);
      this.w.addChild(this.dec);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.mainbody.render(scale);
   }
}
