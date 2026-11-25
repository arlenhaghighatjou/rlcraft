package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTendrilDragonERW extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer jointRW1;
   public ModelRenderer jd;
   public ModelRenderer jointRW1_1;
   public ModelRenderer w;
   public ModelRenderer skin;
   public ModelRenderer jointRW2;
   public ModelRenderer dec;
   public ModelRenderer dec_1;
   public ModelRenderer dec_2;
   public ModelRenderer dec_3;
   public ModelRenderer w_1;
   public ModelRenderer skin_1;
   public ModelRenderer dec_4;

   public ModelTendrilDragonERW() {
      this.textureWidth = 200;
      this.textureHeight = 150;
      this.dec_4 = new ModelRenderer(this, 0, 16);
      this.dec_4.setRotationPoint(-51.0F, 0.0F, 0.0F);
      this.dec_4.addBox(-1.0F, -1.0F, -2.0F, 15, 2, 3, 0.0F);
      this.setRotateAngle(this.dec_4, 0.0F, 3.1415927F, 0.0F);
      this.jointRW2 = new ModelRenderer(this, 66, 0);
      this.jointRW2.setRotationPoint(-60.0F, 0.0F, 0.0F);
      this.jointRW2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.dec_2 = new ModelRenderer(this, 68, 0);
      this.dec_2.setRotationPoint(-21.0F, -4.6F, -1.5F);
      this.dec_2.addBox(-1.5F, -1.0F, -1.0F, 3, 3, 2, 0.0F);
      this.setRotateAngle(this.dec_2, 1.5707964F, -0.43982297F, 0.0F);
      this.jd = new ModelRenderer(this, 8, 0);
      this.jd.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jd.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.jd, 0.0F, 0.24993114F, 0.0F);
      this.skin_1 = new ModelRenderer(this, 16, 84);
      this.skin_1.setRotationPoint(-56.0F, 0.0F, 0.0F);
      this.skin_1.addBox(-56.0F, 0.0F, 2.0F, 60, 0, 60, 0.0F);
      this.setRotateAngle(this.skin_1, 0.0F, 0.0F, -3.1415927F);
      this.jointRW1 = new ModelRenderer(this, 4, 0);
      this.jointRW1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jointRW1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.dec_3 = new ModelRenderer(this, 164, 0);
      this.dec_3.setRotationPoint(-13.0F, -4.6F, -1.5F);
      this.dec_3.addBox(-1.5F, -1.0F, -1.0F, 3, 5, 2, 0.0F);
      this.setRotateAngle(this.dec_3, 1.5707964F, -0.87964594F, 0.0F);
      this.jointRW1_1 = new ModelRenderer(this, 12, 0);
      this.jointRW1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.jointRW1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(28.0F, 23.0F, -20.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.mainbody, -0.25132743F, 0.0F, 0.0F);
      this.dec = new ModelRenderer(this, 74, 0);
      this.dec.setRotationPoint(-26.0F, 0.0F, 0.0F);
      this.dec.addBox(-1.0F, -1.0F, -2.5F, 35, 4, 5, 0.0F);
      this.setRotateAngle(this.dec, 0.0F, 3.1415927F, 0.0F);
      this.w = new ModelRenderer(this, 8, 0);
      this.w.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.w.addBox(-25.0F, -4.0F, -4.0F, 25, 8, 8, 0.0F);
      this.setRotateAngle(this.w, 0.29670596F, 0.0F, 0.08726646F);
      this.skin = new ModelRenderer(this, -52, 23);
      this.skin.setRotationPoint(0.0F, 0.0F, 2.0F);
      this.skin.addBox(-60.0F, 0.0F, 0.0F, 60, 0, 58, 0.0F);
      this.dec_1 = new ModelRenderer(this, 154, 0);
      this.dec_1.setRotationPoint(-19.0F, -1.6F, -4.5F);
      this.dec_1.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 2, 0.0F);
      this.setRotateAngle(this.dec_1, 0.0F, 0.0F, -0.43982297F);
      this.w_1 = new ModelRenderer(this, 74, 9);
      this.w_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.w_1.addBox(-50.0F, -2.0F, -2.0F, 50, 4, 4, 0.0F);
      this.setRotateAngle(this.w_1, 0.0F, 0.0F, 1.0164797F);
      this.w_1.addChild(this.dec_4);
      this.w.addChild(this.jointRW2);
      this.w.addChild(this.dec_2);
      this.jointRW1.addChild(this.jd);
      this.w_1.addChild(this.skin_1);
      this.mainbody.addChild(this.jointRW1);
      this.w.addChild(this.dec_3);
      this.jd.addChild(this.jointRW1_1);
      this.w.addChild(this.dec);
      this.jointRW1_1.addChild(this.w);
      this.w.addChild(this.skin);
      this.w.addChild(this.dec_1);
      this.jointRW2.addChild(this.w_1);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.mainbody.render(scale);
   }
}
