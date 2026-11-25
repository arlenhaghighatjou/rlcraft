package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTendrilEsor extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer taclejointLA0;
   public ModelRenderer tentacle;
   public ModelRenderer taclejointLA1;
   public ModelRenderer tentacle_1;
   public ModelRenderer taclejointLA2;
   public ModelRenderer tentacle_2;
   public ModelRenderer taclejointLA3;
   public ModelRenderer tentacle_3;

   public ModelTendrilEsor() {
      this.textureWidth = 55;
      this.textureHeight = 32;
      this.taclejointLA0 = new ModelRenderer(this, 4, 0);
      this.taclejointLA0.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.taclejointLA0.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.taclejointLA1 = new ModelRenderer(this, 14, 0);
      this.taclejointLA1.setRotationPoint(0.0F, 0.0F, 6.0F);
      this.taclejointLA1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.tentacle_1 = new ModelRenderer(this, 14, 0);
      this.tentacle_1.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_1.addBox(-1.5F, -1.5F, -1.0F, 3, 3, 7, 0.0F);
      this.setRotateAngle(this.tentacle_1, 0.5061455F, 0.0F, 0.0F);
      this.tentacle = new ModelRenderer(this, 0, 0);
      this.tentacle.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.tentacle.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 10, 0.0F);
      this.tentacle_3 = new ModelRenderer(this, 0, 12);
      this.tentacle_3.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_3.addBox(-0.5F, -0.5F, -1.0F, 1, 1, 12, 0.0F);
      this.setRotateAngle(this.tentacle_3, 0.82030475F, 0.0F, 0.0F);
      this.tentacle_2 = new ModelRenderer(this, 22, 0);
      this.tentacle_2.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.tentacle_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 12, 0.0F);
      this.setRotateAngle(this.tentacle_2, 0.8552113F, 0.0F, 0.0F);
      this.taclejointLA2 = new ModelRenderer(this, 27, 0);
      this.taclejointLA2.setRotationPoint(0.0F, 0.0F, 4.0F);
      this.taclejointLA2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 23.0F, 6.0F);
      this.mainbody.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.mainbody, 0.0F, 3.1415927F, 0.0F);
      this.taclejointLA3 = new ModelRenderer(this, 38, 0);
      this.taclejointLA3.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.taclejointLA3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.mainbody.addChild(this.taclejointLA0);
      this.tentacle.addChild(this.taclejointLA1);
      this.taclejointLA1.addChild(this.tentacle_1);
      this.taclejointLA0.addChild(this.tentacle);
      this.taclejointLA3.addChild(this.tentacle_3);
      this.taclejointLA2.addChild(this.tentacle_2);
      this.tentacle_1.addChild(this.taclejointLA2);
      this.tentacle_2.addChild(this.taclejointLA3);
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
      this.taclejointLA1.rotateAngleX = f1;
      this.taclejointLA2.rotateAngleX = f2;
      this.taclejointLA3.rotateAngleX = f3;
   }
}
