package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import com.lycanitesmobs.core.entity.creature.EntityDarkling;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDarkling extends ModelTemplateInsect {
   public ModelDarkling() {
      this(1.0F);
   }

   public ModelDarkling(float shadowSize) {
      this.initModel("darkling", LycanitesMobs.modInfo, "entity/darkling");
      this.lookHeadScaleX = 0.0F;
      this.lookHeadScaleY = 0.0F;
      this.mouthScaleX = 1.0D;
      this.mouthScaleY = 0.10000000149011612D;
      this.trophyScale = 1.0F;
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (partName.equals("body") && entity instanceof EntityDarkling) {
         if (((EntityDarkling)entity).hasLatchTarget()) {
            this.rotate(-90.0F, 0.0F, 0.0F);
         } else {
            float bob = MathHelper.cos(time * 0.6662F + 3.1415927F) * 0.3F * distance;
            if (bob < 0.0F) {
               bob += -bob * 2.0F;
            }

            this.translate(0.0F, bob, 0.0F);
         }
      }

   }
}
