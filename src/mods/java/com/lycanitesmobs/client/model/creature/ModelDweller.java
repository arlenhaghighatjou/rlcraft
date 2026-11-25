package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDweller extends ModelObjOld {
   boolean attackAlt;
   boolean attacking;

   public ModelDweller() {
      this(1.0F);
   }

   public ModelDweller(float shadowSize) {
      this.attackAlt = false;
      this.attacking = false;
      this.initModel("Dweller", LycanitesMobs.modInfo, "entity/dweller");
      this.setPartCenter("head", 0.0F, 1.5F, 0.2F);
      this.setPartCenter("mouth", 0.0F, 1.5F, 0.2F);
      this.setPartCenter("body", 0.0F, 1.5F, 0.2F);
      this.setPartCenter("leftarm", 0.4F, 1.5F, 0.0F);
      this.setPartCenter("rightarm", -0.4F, 1.5F, 0.0F);
      this.setPartCenter("tail01", 0.0F, 1.0F, 0.0F);
      this.setPartCenter("tail02", 0.0F, 0.45F, -0.1F);
      this.setPartCenter("tail03", 0.0F, 0.11F, -0.3F);
      this.setPartCenter("tail04", 0.0F, 0.1F, -0.85F);
      this.setPartCenter("tail05", 0.0F, 0.09F, -1.3F);
      this.setPartCenter("tail06", 0.0F, 0.08F, -1.65F);
      this.trophyScale = 1.0F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      float pi = 3.1415927F;
      float posX = 0.0F;
      float posY = 0.0F;
      float posZ = 0.0F;
      float angleX = 0.0F;
      float angleY = 0.0F;
      float angleZ = 0.0F;
      float rotation = 0.0F;
      float rotX = 0.0F;
      float rotY = 0.0F;
      float rotZ = 0.0F;
      float scaleX = 1.0F;
      float scaleY = 1.0F;
      float scaleZ = 1.0F;
      if (partName.equals("mouth")) {
         if (!this.lockHeadX) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(lookX / 57.295776F)));
         }

         if (!this.lockHeadY) {
            rotY = (float)((double)rotY + Math.toDegrees((double)(lookY / 57.295776F)));
         }
      }

      if (partName.equals("mouth")) {
         this.translate(0.0F, -0.2F, 0.0F);
         angleX = -1.0F;
      }

      if (partName.equals("mouth")) {
         rotation = (float)((double)rotation + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F)));
         if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
            rotation = 20.0F;
         }

         this.rotate(rotation, angleX, angleY, angleZ);
         rotation = 0.0F;
      }

      if (partName.equals("mouth")) {
         this.translate(-0.0F, 0.2F, 0.0F);
      }

      if (partName.equals("leftarm")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
         if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
            if (this.attackAlt) {
               rotY = 40.0F;
            } else {
               rotY = -25.0F;
            }
         }
      }

      if (partName.equals("rightarm")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
         if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
            if (this.attackAlt) {
               rotY = 40.0F;
            } else {
               rotY = -25.0F;
            }
         }
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown() && !this.attacking) {
         this.attackAlt = !this.attackAlt;
         this.attacking = true;
      }

      if (entity instanceof BaseCreatureEntity && !((BaseCreatureEntity)entity).isAttackOnCooldown()) {
         this.attacking = false;
      }

      float walkSwing = 0.1F;
      float walkOffset = 0.1F;
      time = time * 4.0F;
      if (partName.equals("leftarm")) {
         this.translate(-0.4F, 0.0F, 0.2F);
      }

      if (partName.equals("rightarm")) {
         this.translate(0.4F, 0.0F, 0.2F);
      }

      if (partName.equals("leftarm") || partName.equals("rightarm")) {
         this.rotate(0.0F, 0.0F, (float)this.rotateToPoint(0.0D, 0.0D, -0.5D, (double)(MathHelper.sin(time * walkSwing) * walkSwing)));
      }

      if (partName.equals("leftarm")) {
         this.translate(0.4F, 0.0F, -0.2F);
      }

      if (partName.equals("rightarm")) {
         this.translate(-0.4F, 0.0F, -0.2F);
      }

      if (partName.equals("body")) {
         rotZ = (float)((double)rotZ + this.rotateToPoint(0.0D, 0.0D, -0.5D, (double)(MathHelper.sin(time * walkSwing) * walkSwing)));
      }

      if (partName.equals("tail01") || partName.equals("tail02") || partName.equals("tail03") || partName.equals("tail04") || partName.equals("tail05") || partName.equals("tail06")) {
         double[] tailAnimation = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
         if (partName.equals("tail01")) {
            tailAnimation = this.getTailRotation(partName, "body", 1, walkSwing, walkOffset, time);
         }

         if (partName.equals("tail02")) {
            tailAnimation = this.getTailRotation(partName, "tail01", 2, walkSwing, walkOffset, time);
         }

         if (partName.equals("tail03")) {
            tailAnimation = this.getTailRotation(partName, "tail02", 3, walkSwing, walkOffset, time);
         }

         if (partName.equals("tail04")) {
            tailAnimation = this.getTailRotation(partName, "tail03", 4, walkSwing, walkOffset, time);
         }

         if (partName.equals("tail05")) {
            tailAnimation = this.getTailRotation(partName, "tail04", 5, walkSwing, walkOffset, time);
         }

         if (partName.equals("tail06")) {
            tailAnimation = this.getTailRotation(partName, "tail05", 6, walkSwing, walkOffset, time);
         }

         posX = (float)((double)posX + tailAnimation[0]);
         rotY = (float)((double)rotY + tailAnimation[2]);
         if (partName.equals("tail01") || partName.equals("tail02") || partName.equals("tail03")) {
            rotZ = (float)((double)rotZ + tailAnimation[3]);
         }
      }

      this.translate(posX, posY, posZ);
      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.scale(scaleX, scaleY, scaleZ);
   }

   public double[] getTailRotation(String tailName, String targetName, int tailID, float walkSwing, float walkOffset, float time) {
      double[] animation = new double[4];
      animation[0] = (double)(MathHelper.sin((time + walkOffset * (float)tailID) * walkSwing * (float)tailID) * walkSwing);
      float parentX = MathHelper.sin((time + walkOffset * (float)(tailID - 1)) * walkSwing * (float)tailID) * walkSwing;
      float[] partDifference = this.comparePartCenters(tailName, targetName);
      if (tailID == 1) {
         partDifference[1] += 0.5F;
      }

      double[] rotations = this.rotateToPoint((double)((float)animation[0]), 0.0D, 0.0D, (double)(parentX - partDifference[0]), (double)(-partDifference[1]), (double)partDifference[2]);
      animation[1] = rotations[0];
      animation[2] = rotations[1];
      animation[3] = rotations[2];
      return animation;
   }
}
