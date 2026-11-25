package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJoust extends ModelTemplateQuadruped {
   float maxLeg;

   public ModelJoust() {
      this(1.0F);
   }

   public ModelJoust(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("joust", LycanitesMobs.modInfo, "entity/joust");
      this.trophyScale = 1.0F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
      this.lookHeadScaleX = 0.25F;
      this.lookHeadScaleY = 0.25F;
      this.lookNeckScaleX = 0.5F;
      this.lookNeckScaleY = 0.5F;
      this.legAnimationScale = 0.0F;
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      float posY = 0.0F;
      float rotX = 0.0F;
      float rotY = 0.0F;
      float rotZ = 0.0F;
      float rotation = 0.0F;
      float angleY = 0.0F;
      float angleZ = 0.0F;
      this.lookHeadScaleX = 0.25F;
      this.lookHeadScaleY = 0.25F;
      this.lookNeckScaleX = 0.5F;
      this.lookNeckScaleY = 0.5F;
      if (partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightfront") || partName.equals("legrightback")) {
         angleZ = 1.0F;
      }

      if (partName.equals("legleftfront")) {
         angleY = 0.097222224F;
      }

      if (partName.equals("legleftback")) {
         angleY = -0.097222224F;
      }

      if (partName.equals("legrightfront")) {
         angleY = -0.097222224F;
      }

      if (partName.equals("legrightback")) {
         angleY = 0.097222224F;
      }

      float walkSwing = 0.3F;
      if (!partName.equals("legrightfront") && !partName.equals("legleftback")) {
         if (!partName.equals("legleftfront") && !partName.equals("legrightback")) {
            if (partName.equals("body")) {
               float bob = MathHelper.cos(time * 0.6662F + 3.1415927F) * walkSwing * distance;
               if (bob < 0.0F) {
                  bob += -bob * 2.0F;
               }

               posY += bob;
            }
         } else {
            rotation = (float)((double)rotation + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F) * walkSwing * distance)));
         }
      } else {
         rotation = (float)((double)rotation + Math.toDegrees((double)(MathHelper.cos(time * 0.6662F + 3.1415927F) * walkSwing * distance)));
      }

      if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown() && partName.equals("mouth")) {
         this.rotate(30.0F, 0.0F, 0.0F);
      }

      this.angle(rotation, 0.0F, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(0.0F, posY, 0.0F);
   }
}
