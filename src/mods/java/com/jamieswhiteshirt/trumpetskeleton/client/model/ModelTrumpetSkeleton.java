package com.jamieswhiteshirt.trumpetskeleton.client.model;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.entity.Entity;

public class ModelTrumpetSkeleton extends ModelSkeleton {
   public ModelTrumpetSkeleton() {
      this(0.0F, false);
   }

   public ModelTrumpetSkeleton(float v, boolean b) {
      super(v, b);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.bipedRightArm.rotateAngleX += 0.19634955F;
      this.bipedRightArm.rotateAngleY -= 0.3926991F;
      this.bipedRightArm.rotateAngleZ += 0.3926991F;
   }
}
