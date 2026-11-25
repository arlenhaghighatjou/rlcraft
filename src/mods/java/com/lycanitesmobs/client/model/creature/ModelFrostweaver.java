package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFrostweaver extends ModelObjOld {
   float maxLeg;

   public ModelFrostweaver() {
      this(1.0F);
   }

   public ModelFrostweaver(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("Frostweaver", LycanitesMobs.modInfo, "entity/frostweaver");
      this.setPartCenter("head", 0.0F, 0.4F, 0.55F);
      this.setPartCenter("leftmouth", 0.1F, 0.3F, 0.9F);
      this.setPartCenter("rightmouth", -0.1F, 0.3F, 0.9F);
      this.setPartCenter("body", 0.0F, 0.4F, 0.55F);
      this.setPartCenter("frontleftleg", 0.4F, 0.5F, 0.2F);
      this.setPartCenter("middlefrontleftleg", 0.4F, 0.5F, 0.0F);
      this.setPartCenter("middlebackleftleg", 0.4F, 0.5F, -0.2F);
      this.setPartCenter("backleftleg", 0.4F, 0.5F, -0.4F);
      this.setPartCenter("frontrightleg", -0.4F, 0.5F, 0.2F);
      this.setPartCenter("middlefrontrightleg", -0.4F, 0.5F, 0.0F);
      this.setPartCenter("middlebackrightleg", -0.4F, 0.5F, -0.2F);
      this.setPartCenter("backrightleg", -0.4F, 0.5F, -0.4F);
      this.lockHeadX = true;
      this.lockHeadY = true;
      this.trophyScale = 1.5F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.2F};
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
      if (partName.equals("leftmouth")) {
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F + 3.1415927F) * 0.05F - 0.05F))), 0.0F, 0.0F);
      }

      if (partName.equals("rightmouth")) {
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F))), 0.0F, 0.0F);
      }

      float walkSwing = 0.6F;
      if (partName.equals("frontrightleg") || partName.equals("middlebackrightleg") || partName.equals("middlefrontleftleg") || partName.equals("backleftleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * 0.3331F + 3.1415927F) * walkSwing * distance)));
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F + 3.1415927F) * walkSwing * distance)));
      }

      if (partName.equals("frontleftleg") || partName.equals("middlebackleftleg") || partName.equals("middlefrontrightleg") || partName.equals("backrightleg")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * 0.3331F) * walkSwing * distance)));
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F) * walkSwing * distance)));
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown() && (partName.equals("leftmouth") || partName.equals("rightmouth"))) {
         rotX += 20.0F;
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }
}
