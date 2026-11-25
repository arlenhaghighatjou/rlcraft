package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBalayang extends ModelTemplateBiped {
   public ModelBalayang() {
      this(1.0F);
   }

   public ModelBalayang(float shadowSize) {
      this.initModel("balayang", LycanitesMobs.modInfo, "entity/balayang");
      this.trophyScale = 1.2F;
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      BaseCreatureEntity creatureEntity = null;
      if (entity instanceof BaseCreatureEntity) {
         creatureEntity = (BaseCreatureEntity)entity;
      }

      if (entity != null && !entity.onGround && !entity.isInWater() && (creatureEntity == null || !creatureEntity.hasPerchTarget())) {
         if (partName.equals("wingleft01")) {
            float rotX = -40.0F;
            float rotY = 20.5F;
            float rotZ = (float)Math.toDegrees((double)(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.6F));
            this.rotate(rotX, rotY, rotZ);
            return;
         }

         if (partName.equals("wingright01")) {
            float rotX = -40.0F;
            float rotY = -20.5F;
            float rotZ = (float)Math.toDegrees((double)(MathHelper.sin(loop * 0.4F * this.wingScale + 3.1415927F) * 0.6F));
            this.rotate(rotX, rotY, rotZ);
            return;
         }

         if (partName.equals("wingleft02")) {
            float rotZ = (float)Math.toDegrees((double)(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.15F));
            this.rotate(0.0F, 0.0F, rotZ);
            return;
         }

         if (partName.equals("wingright02")) {
            float rotZ = (float)Math.toDegrees((double)(MathHelper.sin(loop * 0.4F * this.wingScale + 3.1415927F) * 0.15F));
            this.rotate(0.0F, 0.0F, rotZ);
            return;
         }
      }

      float rotX = 0.0F;
      float rotY = 0.0F;
      if (partName.toLowerCase().equals("body")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(lookX / 57.295776F)) * 0.75D);
         rotY = (float)((double)rotY + Math.toDegrees((double)(lookY / 57.295776F)) * 0.75D);
      }

      if (partName.equals("eye")) {
         rotX = (float)((double)rotX + Math.toDegrees((double)(lookX / 57.295776F)) * 0.25D);
         rotY = (float)((double)rotY + Math.toDegrees((double)(lookY / 57.295776F)) * 0.25D);
      }

      this.rotate(rotX, rotY, 0.0F);
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
   }
}
