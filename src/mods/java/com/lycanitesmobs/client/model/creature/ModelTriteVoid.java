package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTriteVoid extends ModelTemplateInsect {
   public ModelTriteVoid() {
      this(1.0F);
   }

   public ModelTriteVoid(float shadowSize) {
      this.initModel("trite_void", LycanitesMobs.modInfo, "entity/trite_void");
      this.mouthScaleX = 1.0D;
      this.mouthScaleY = 0.10000000149011612D;
      this.trophyScale = 1.0F;
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (partName.equals("body")) {
         float bob = MathHelper.cos(time * 0.6662F + 3.1415927F) * 0.3F * distance;
         if (bob < 0.0F) {
            bob += -bob * 2.0F;
         }

         this.translate(0.0F, bob, 0.0F);
      }

      if (partName.contains("tentacle")) {
         float loopOffset = 0.0F;
         if (partName.contains("front")) {
            loopOffset += 10.0F;
         } else if (partName.contains("back")) {
            loopOffset += 30.0F;
         }

         if (partName.contains("left")) {
            this.rotate((float)Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)), (float)Math.toDegrees((double)(MathHelper.sin((loop + loopOffset / 2.0F) * 0.2F) * 0.25F)) - 10.0F, (float)(-Math.toDegrees((double)(MathHelper.cos((loop + loopOffset / 2.0F) * 0.09F) * 0.1F))));
         } else {
            this.rotate((float)Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)), (float)(-Math.toDegrees((double)(MathHelper.sin((loop + loopOffset / 2.0F) * 0.2F) * 0.25F))) - 10.0F, (float)(-Math.toDegrees((double)(MathHelper.cos((loop + loopOffset / 2.0F) * 0.09F) * 0.1F))));
         }
      }

   }
}
