package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTendrilNogla extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer taclejointL;
   public ModelRenderer tentacle;
   public ModelRenderer taclejointL1;
   public ModelRenderer tentacle_1;
   public ModelRenderer taclejointL2;
   public ModelRenderer tentacle_2;
   public ModelRenderer taclejointL3;
   public ModelRenderer tentacle_3;

   public ModelTendrilNogla() {
      this.textureWidth = 80;
      this.textureHeight = 32;
      this.taclejointL2 = new ModelRenderer(this, 32, 0);
      this.taclejointL2.setRotationPoint(0.0F, -0.2F, 9.0F);
      this.taclejointL2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
      this.tentacle_1 = new ModelRenderer(this, 38, 0);
      this.tentacle_1.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_1.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 11, 0.0F);
      this.setRotateAngle(this.tentacle_1, -0.61086524F, 0.0F, 0.0F);
      this.tentacle_3 = new ModelRenderer(this, 42, 16);
      this.tentacle_3.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_3.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 13, 0.0F);
      this.setRotateAngle(this.tentacle_3, -0.7853982F, 0.0F, 0.0F);
      this.taclejointL1 = new ModelRenderer(this, 24, 0);
      this.taclejointL1.setRotationPoint(0.0F, -0.2F, 11.0F);
      this.taclejointL1.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 1, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 21.5F, 6.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.tentacle_2 = new ModelRenderer(this, 27, 15);
      this.tentacle_2.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_2.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 11, 0.0F);
      this.setRotateAngle(this.tentacle_2, -0.8552113F, 0.0F, 0.0F);
      this.taclejointL3 = new ModelRenderer(this, 38, 0);
      this.taclejointL3.setRotationPoint(0.0F, -0.2F, 9.0F);
      this.taclejointL3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F);
      this.taclejointL = new ModelRenderer(this, 4, 0);
      this.taclejointL.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.taclejointL.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 1, 0.0F);
      this.setRotateAngle(this.taclejointL, 0.0F, 3.1415927F, 3.1415927F);
      this.tentacle = new ModelRenderer(this, 0, 0);
      this.tentacle.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.tentacle.addBox(-2.5F, -2.5F, -1.0F, 5, 5, 14, 0.0F);
      this.tentacle_1.addChild(this.taclejointL2);
      this.taclejointL1.addChild(this.tentacle_1);
      this.taclejointL3.addChild(this.tentacle_3);
      this.tentacle.addChild(this.taclejointL1);
      this.taclejointL2.addChild(this.tentacle_2);
      this.tentacle_2.addChild(this.taclejointL3);
      this.mainbody.addChild(this.taclejointL);
      this.taclejointL.addChild(this.tentacle);
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
      this.taclejointL1.rotateAngleX = f1;
      this.taclejointL2.rotateAngleX = f2;
      this.taclejointL3.rotateAngleX = f3;
   }
}
