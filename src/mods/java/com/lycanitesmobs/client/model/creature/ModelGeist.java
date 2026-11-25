package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGeist extends ModelObjOld {
   float maxLeg;

   public ModelGeist() {
      this(1.0F);
   }

   public ModelGeist(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("geist", LycanitesMobs.modInfo, "entity/geist");
      this.setPartCenter("head", 0.0F, 1.9F, 0.1F);
      this.setPartCenter("body", 0.0F, 1.4F, 0.0F);
      this.setPartCenter("armleft", 0.3F, 1.78F, -0.13F);
      this.setPartCenter("armright", -0.3F, 1.78F, -0.13F);
      this.setPartCenter("legleft", 0.2F, 0.9F, -0.2F);
      this.setPartCenter("legright", -0.2F, 0.9F, -0.2F);
      this.trophyScale = 1.2F;
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
      if (partName.equals("armleft")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      if (partName.equals("armright")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)));
      }

      float walkSwing = 0.6F;
      if (partName.equals("armleft")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F)));
      }

      if (partName.equals("armright")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 2.0F * distance * 0.5F)));
      }

      if (partName.equals("legleft")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.4F * distance)));
      }

      if (partName.equals("legright")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.4F * distance)));
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
