package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCacodemon extends ModelTemplateBiped {
   public ModelCacodemon() {
      this(1.0F);
   }

   public ModelCacodemon(float shadowSize) {
      this.initModel("cacodemon", LycanitesMobs.modInfo, "entity/cacodemon");
      this.flightBobScale = 0.1F;
      this.trophyScale = 0.5F;
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "glow", true, LayerCreatureEffect.BLEND.ADD.id, true));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
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
   }
}
