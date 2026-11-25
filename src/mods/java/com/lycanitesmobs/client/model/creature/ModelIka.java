package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelIka extends ModelObjOld {
   float maxLeg;

   public ModelIka() {
      this(1.0F);
   }

   public ModelIka(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("ika", LycanitesMobs.modInfo, "entity/ika");
      this.setPartCenter("head", 0.0F, 0.3F, 1.1F);
      this.setPartCenter("body", 0.0F, 0.3F, 0.0F);
      this.setPartCenter("shell", 0.0F, 0.4F, 0.0F);
      this.setPartCenter("legleft", 0.4F, 0.2F, 0.8F);
      this.setPartCenter("legright", -0.4F, 0.2F, 0.8F);
      this.setPartCenter("tail", 0.0F, 0.3F, -1.0F);
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
      if (partName.equals("tail")) {
         rotX = (float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F)));
         rotY = (float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F)));
      }

      float walkSwing = 0.2F;
      if (partName.equals("legleft")) {
         rotY = (float)((double)rotY + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F + 3.1415927F) * walkSwing * distance)));
      }

      if (partName.equals("legright")) {
         rotY = (float)((double)rotY + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F) * walkSwing * distance)));
      }

      if (entity != null && partName.equals("shell") && entity.getHealth() <= entity.getMaxHealth() / 2.0F) {
         this.scale(0.0F, 0.0F, 0.0F);
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }

   public void childScale(String partName) {
      if (partName.equals("head")) {
         this.translate(-(this.getPartCenter(partName)[0] / 2.0F), -(this.getPartCenter(partName)[1] / 2.0F), -(this.getPartCenter(partName)[2] / 2.0F));
      } else {
         super.childScale(partName);
      }

   }
}
