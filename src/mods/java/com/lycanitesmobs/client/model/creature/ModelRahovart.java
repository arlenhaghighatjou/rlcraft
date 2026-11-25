package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRahovart extends ModelObjOld {
   float maxLeg;

   public ModelRahovart() {
      this(1.0F);
   }

   public ModelRahovart(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("rahovart", LycanitesMobs.modInfo, "entity/rahovart");
      this.setPartCenter("head", 0.0F, 23.275F, 0.0F);
      this.setPartCenter("mouth", 0.0F, 23.075F, -2.5F);
      this.setPartCenter("body", 0.0F, 11.025F, 0.0F);
      this.setPartCenter("armleft", 5.6F, 19.775F, 0.0F);
      this.setPartCenter("armright", -5.6F, 19.775F, 0.0F);
      this.setPartCenter("legleft", 2.45F, 11.2F, 0.0F);
      this.setPartCenter("legright", -2.45F, 11.2F, 0.0F);
      this.setPartCenter("tail", 0.0F, 18.9F, 4.725F);
      this.lockHeadX = false;
      this.lockHeadY = false;
      this.trophyScale = 0.1F;
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
      if (partName.equals("mouth")) {
         this.centerPartToPart("mouth", "head");
         if (!this.lockHeadX) {
            this.rotate((float)Math.toDegrees((double)(lookX / 57.295776F)), 0.0F, 0.0F);
         }

         if (!this.lockHeadY) {
            this.rotate(0.0F, (float)Math.toDegrees((double)(lookY / 57.295776F)), 0.0F);
         }

         this.uncenterPartToPart("mouth", "head");
      }

      if (partName.equals("mouth")) {
         rotX += MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
      }

      if (partName.equals("armleft")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("armright")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("tail")) {
         rotX = (float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F)));
         rotY = (float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F)));
      }

      float walkSwing = 0.05F;
      if (partName.equals("armleft")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * distance * 0.5F)));
      }

      if (partName.equals("armright")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * distance * 0.5F)));
      }

      if (partName.equals("legleft")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * distance)));
      }

      if (partName.equals("legright")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * distance)));
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown() && partName.equals("armleft")) {
         this.rotate(-40.0F, 0.0F, 0.0F);
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
