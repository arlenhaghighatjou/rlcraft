package lykrast.defiledlands.client.model;

import lykrast.defiledlands.common.entity.boss.EntityMourner;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

public class ModelMourner extends ModelPlayer {
   public ModelMourner(float modelSize) {
      super(modelSize, true);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      EntityMourner mourner = (EntityMourner)entityIn;
      if (mourner.isFiring()) {
         switch(mourner.getCurrentAttack()) {
         case 0:
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            break;
         case 1:
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            break;
         case 2:
            this.bipedRightArm.rotateAngleX = 3.1415927F;
            this.bipedLeftArm.rotateAngleX = 3.1415927F;
         }
      }

   }
}
