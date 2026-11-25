package com.lycanitesmobs.client.model.template;

import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelTemplateElemental extends ModelCreatureObj {
   public ModelTemplateElemental() {
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
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
      if (partName.equals("mouth")) {
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.1F - 0.1F))), 0.0F, 0.0F);
      }

      if (partName.equals("armleft")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.1F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("armright")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.1F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("body02")) {
         posX -= MathHelper.cos(loop * 0.09F) * 0.1F;
         posY += MathHelper.sin(loop * 0.067F) * 0.05F;
      }

      if (partName.equals("body03")) {
         posX += MathHelper.cos(loop * 0.09F) * 0.1F;
         posY -= MathHelper.sin(loop * 0.067F) * 0.05F;
      }

      if (partName.equals("body")) {
         float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
         posY += bob;
      }

      if (partName.equals("effectouter")) {
         rotY += loop * 8.0F;
      }

      if (partName.equals("effectinner")) {
         rotY -= loop * 8.0F;
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
         BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)entity;
         if (baseCreatureEntity.attackPhaseMax > 0) {
            if (partName.equals("armleft") && baseCreatureEntity.getAttackPhase() == 2) {
               this.rotate(0.0F, -25.0F, 0.0F);
            }

            if (partName.equals("armright") && baseCreatureEntity.getAttackPhase() != 2) {
               this.rotate(0.0F, 25.0F, 0.0F);
            }
         } else {
            if (partName.equals("armleft")) {
               this.rotate(0.0F, -25.0F, 0.0F);
            }

            if (partName.equals("armright")) {
               this.rotate(0.0F, 25.0F, 0.0F);
            }
         }
      }

      this.angle(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
