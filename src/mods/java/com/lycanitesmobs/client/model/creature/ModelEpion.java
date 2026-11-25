package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEpion extends ModelCreatureObj {
   float maxLeg;

   public ModelEpion() {
      this(1.0F);
   }

   public ModelEpion(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("epion", LycanitesMobs.modInfo, "entity/epion");
      this.trophyScale = 1.0F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.2F};
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
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F))), 0.0F, 0.0F);
      }

      if (partName.equals("earleft")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.18F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("earright")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.18F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (entity.isBeingRidden() && (partName.equals("earleft") || partName.equals("earright"))) {
         rotX -= 30.0F;
      }

      if (entity != null && (entity.onGround || entity.isInWater())) {
         float walkSwing = 0.6F;
         if (partName.equals("wingleft01")) {
            rotZ -= 29.0F;
            rotY = (float)((double)rotY + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 0.1F * distance)));
         }

         if (partName.equals("wingright01")) {
            rotZ += 29.0F;
            rotY = (float)((double)rotY + (Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 0.1F)) - (double)(10.0F * distance)));
         }

         if (partName.equals("wingleft02")) {
            rotZ += 120.0F;
         }

         if (partName.equals("wingright02")) {
            rotZ -= 120.0F;
         }
      } else {
         if (partName.equals("body")) {
            float bob = -MathHelper.sin(loop * 0.2F) * (entity.isBeingRidden() ? 0.1F : 0.6F);
            if (bob < 0.0F) {
               bob = -bob;
            }

            posY += bob;
         }

         if (partName.contains("wingleft")) {
            rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.sin(loop * 0.4F) * 0.6F)));
         }

         if (partName.contains("wingright")) {
            rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.sin(loop * 0.4F + 3.1415927F) * 0.6F)));
         }

         if (partName.equals("wingleft01")) {
            rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.4F) * 0.1F)));
         }

         if (partName.equals("wingright01")) {
            rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.4F) * 0.1F)));
         }
      }

      this.translate(posX, posY, posZ);
      this.angle(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
   }
}
