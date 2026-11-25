package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTroll extends ModelObjOld {
   float maxLeg;

   public ModelTroll() {
      this(1.0F);
   }

   public ModelTroll(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("troll", LycanitesMobs.modInfo, "entity/troll");
      this.setPartCenter("head", 0.0F, 3.0F, 0.3F);
      this.setPartCenter("body", 0.0F, 3.0F, 0.3F);
      this.setPartCenter("leftarm", 0.5F, 2.7F, 0.0F);
      this.setPartCenter("rightarm", -0.5F, 2.7F, 0.0F);
      this.setPartCenter("leftleg", 0.4F, 1.2F, -0.2F);
      this.setPartCenter("rightleg", -0.4F, 1.2F, -0.2F);
      this.trophyScale = 0.8F;
      this.trophyOffset = new float[]{0.0F, -0.3F, 0.0F};
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
      if (partName.equals("mouth")) {
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F))), 0.0F, 0.0F);
      }

      if (partName.equals("leftarm")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("rightarm")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (entity == null || entity.onGround || entity.isInWater()) {
         float walkSwing = 0.6F;
         if (partName.equals("leftarm")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F)));
            rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F)));
         }

         if (partName.equals("rightarm")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.0F * distance * 0.5F)));
            rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 0.5F * distance * 0.5F)));
         }

         if (partName.equals("leftleg")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.4F * distance)));
         }

         if (partName.equals("rightleg")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.4F * distance)));
         }
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
         if (partName.equals("leftarm") || partName.equals("rightarm")) {
            rotX -= 65.0F;
         }

         if (partName.equals("leftarm")) {
            rotY -= 20.0F;
         }

         if (partName.equals("rightarm")) {
            rotY += 20.0F;
         }
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
