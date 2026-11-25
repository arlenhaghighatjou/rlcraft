package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTendrilShyco extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer taclejointL1;
   public ModelRenderer tentacle;
   public ModelRenderer taclejointL2;
   public ModelRenderer dec;
   public ModelRenderer tentacle_1;
   public ModelRenderer taclejointL3;
   public ModelRenderer tentacle_2;
   public ModelRenderer taclejointL4;
   public ModelRenderer tentacle_3;

   public ModelTendrilShyco() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.tentacle = new ModelRenderer(this, 4, 0);
      this.tentacle.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.tentacle.addBox(-1.0F, -2.0F, -2.0F, 8, 4, 4, 0.0F);
      this.tentacle_2 = new ModelRenderer(this, 35, 0);
      this.tentacle_2.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 11, 0.0F);
      this.setRotateAngle(this.tentacle_2, 0.0F, 1.012291F, 0.0F);
      this.tentacle_3 = new ModelRenderer(this, 17, 10);
      this.tentacle_3.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_3.addBox(-0.5F, -0.5F, -1.0F, 1, 1, 13, 0.0F);
      this.setRotateAngle(this.tentacle_3, 0.0F, 0.9250245F, 0.0F);
      this.taclejointL1 = new ModelRenderer(this, 4, 0);
      this.taclejointL1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.taclejointL1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.tentacle_1 = new ModelRenderer(this, 0, 8);
      this.tentacle_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.tentacle_1.addBox(-1.5F, -1.5F, -1.0F, 3, 3, 12, 0.0F);
      this.setRotateAngle(this.tentacle_1, 0.0F, 2.0420353F, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 22.2F, 6.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.mainbody, 0.0F, 1.5707964F, 1.5707964F);
      this.taclejointL3 = new ModelRenderer(this, 42, 0);
      this.taclejointL3.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.taclejointL3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F);
      this.dec = new ModelRenderer(this, 28, 0);
      this.dec.setRotationPoint(-0.3F, 0.0F, 0.0F);
      this.dec.addBox(-2.5F, -2.5F, -1.0F, 5, 5, 4, 0.0F);
      this.taclejointL4 = new ModelRenderer(this, 50, 0);
      this.taclejointL4.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.taclejointL4.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F);
      this.taclejointL2 = new ModelRenderer(this, 24, 0);
      this.taclejointL2.setRotationPoint(6.0F, 0.0F, 0.0F);
      this.taclejointL2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
      this.taclejointL1.addChild(this.tentacle);
      this.taclejointL3.addChild(this.tentacle_2);
      this.taclejointL4.addChild(this.tentacle_3);
      this.mainbody.addChild(this.taclejointL1);
      this.taclejointL2.addChild(this.tentacle_1);
      this.tentacle_1.addChild(this.taclejointL3);
      this.tentacle.addChild(this.dec);
      this.tentacle_2.addChild(this.taclejointL4);
      this.tentacle.addChild(this.taclejointL2);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.mainbody.render(scale);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      float f1 = MathHelper.sin(ageInTicks * 0.06F + 0.0F) * 0.2F;
      float f2 = MathHelper.sin(ageInTicks * 0.07F + 8.0F) * 0.3F;
      float f3 = MathHelper.sin(ageInTicks * 0.06F + 4.0F) * 0.4F;
      this.taclejointL2.rotateAngleY = f1;
      this.taclejointL3.rotateAngleY = f2;
      this.taclejointL4.rotateAngleY = f3;
   }
}
