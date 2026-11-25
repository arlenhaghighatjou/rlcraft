package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLurker extends ModelObjOld {
   float maxLeg;

   public ModelLurker() {
      this(1.0F);
   }

   public ModelLurker(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("Lurker", LycanitesMobs.modInfo, "entity/lurker");
      this.setPartCenter("topmiddlemouth", 0.0F, 1.0F, 0.6F);
      this.setPartCenter("topleftmouth", 0.2F, 1.0F, 0.6F);
      this.setPartCenter("toprightmouth", -0.2F, 1.0F, 0.6F);
      this.setPartCenter("leftmouth", 0.25F, 0.75F, 0.65F);
      this.setPartCenter("rightmouth", -0.25F, 0.75F, 0.65F);
      this.setPartCenter("bottommouth", 0.0F, 0.55F, 0.65F);
      this.setPartCenter("body", 0.0F, 0.7F, 0.0F);
      this.setPartCenter("frontleftleg", 0.3F, 0.85F, 0.35F);
      this.setPartCenter("frontrightleg", -0.3F, 0.85F, 0.35F);
      this.setPartCenter("backleftleg", 0.25F, 0.5F, -0.45F);
      this.setPartCenter("backrightleg", -0.25F, 0.5F, -0.45F);
      this.trophyScale = 0.8F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -1.0F};
      this.trophyMouthOffset = new float[]{0.0F, -0.8F, 0.0F};
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
      if (partName.equals("topmiddlemouth")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.08F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("topleftmouth")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("toprightmouth")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.08F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("leftmouth")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("rightmouth")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("bottommouth")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.075F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      float walkSwing = 0.15F;
      if (partName.equals("frontleftleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.4F * distance)));
      }

      if (partName.equals("frontrightleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.4F * distance)));
      }

      if (partName.equals("backleftleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.4F * distance)));
      }

      if (partName.equals("backrightleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.4F * distance)));
      }

      float bob = MathHelper.cos(time * 0.6662F + 3.1415927F) * walkSwing * distance;
      if (bob < 0.0F) {
         bob += -bob * 2.0F;
      }

      posY = posY + bob;
      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
         if (partName.equals("topmiddlemouth") || partName.equals("topleftmouth") || partName.equals("toprightmouth")) {
            rotX -= 30.0F;
         }

         if (partName.equals("leftmouth")) {
            rotZ += 30.0F;
         }

         if (partName.equals("rightmouth")) {
            rotZ -= 30.0F;
         }

         if (partName.equals("bottommouth")) {
            rotX += 30.0F;
         }
      }

      if (entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).isSitting()) {
         if (partName.equals("topmiddlemouth") || partName.equals("topleftmouth") || partName.equals("toprightmouth")) {
            rotX += 30.0F;
         }

         if (partName.equals("leftmouth")) {
            rotZ -= 30.0F;
         }

         if (partName.equals("rightmouth")) {
            rotZ += 30.0F;
         }

         if (partName.equals("bottommouth")) {
            rotX -= 30.0F;
         }
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
