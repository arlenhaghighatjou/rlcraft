package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.client.renderer.LayerCreatureDye;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelYale extends ModelCreatureObj {
   float maxLeg;

   public ModelYale() {
      this(1.0F);
   }

   public ModelYale(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("yale", LycanitesMobs.modInfo, "entity/yale");
      this.bigChildHead = true;
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureDye(renderer, "fur", false));
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
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.1F) * 0.1F + 0.1F))), 0.0F, 0.0F);
      }

      if (partName.equals("armleft")) {
         rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX - Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.2F)));
      }

      if (partName.equals("armright")) {
         rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F)));
         rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.2F)));
      }

      if (entity == null || entity.onGround || entity.isInWater()) {
         float walkSwing = 0.6F;
         if (partName.equals("armleft")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F)));
            rotZ = (float)((double)rotZ - Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F)));
         }

         if (partName.equals("armright")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.0F * distance * 0.5F)));
            rotZ = (float)((double)rotZ + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 0.5F * distance * 0.5F)));
         }

         if (partName.equals("legleftfront") || partName.equals("legrightback")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing + 3.1415927F) * 1.4F * distance)));
         }

         if (partName.equals("legrightfront") || partName.equals("legleftback")) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(MathHelper.cos(time * walkSwing) * 1.4F * distance)));
         }
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown() && (partName.equals("armleft") || partName.equals("armright"))) {
         rotX += 20.0F;
      }

      if (entity != null && !entity.onGround && !entity.isInWater()) {
         if (partName.equals("armleft")) {
            rotZ -= 10.0F;
            rotX -= 50.0F;
         }

         if (partName.equals("armright")) {
            rotZ += 10.0F;
            rotX -= 50.0F;
         }

         if (partName.equals("legleftfront") || partName.equals("legrightfront")) {
            rotX += 50.0F;
         }

         if (partName.equals("legleftback") || partName.equals("legrightback")) {
            rotX -= 50.0F;
         }
      }

      this.angle(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }

   public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
      return "fur".equals(partName) ? false : super.canBaseRenderPart(partName, entity, trophy);
   }
}
