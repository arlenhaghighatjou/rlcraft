package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureScrolling;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelReaper extends ModelTemplateBiped {
   public ModelReaper() {
      this(1.0F);
   }

   public ModelReaper(float shadowSize) {
      this.initModel("reaper", LycanitesMobs.modInfo, "entity/reaper");
      this.flightBobScale = 0.15F;
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, 0.0F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureScrolling(renderer, "cape", false, LayerCreatureEffect.BLEND.SUB.id, true, new Vector2f(0.0F, -1.0F)));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (partName.equals("cloak")) {
         this.rotate((float)Math.toDegrees((double)(MathHelper.cos(loop * 0.1F) * 0.05F)), 0.0F, 0.0F);
      } else if (partName.equals("cape")) {
         this.rotate((float)Math.toDegrees((double)(MathHelper.cos(loop * 0.2F) * 0.05F)), 0.0F, 0.0F);
      } else if (!partName.equals("clawleft01") && !partName.equals("clawright01")) {
         if (!partName.equals("clawleft02") && !partName.equals("clawright02")) {
            if (!partName.equals("clawleft03") && !partName.equals("clawright03")) {
               if (partName.equals("skull01")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
               } else if (partName.equals("skull02")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 20.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
               } else if (partName.equals("skull03")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 40.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
               } else if (partName.equals("skull04")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 60.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
               } else if (partName.equals("skull05")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 80.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
               }
            } else {
               this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 40.0F) * 0.2F) * 0.2F)), 0.0F, 0.0F);
            }
         } else {
            this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 20.0F) * 0.2F) * 0.2F)), 0.0F, 0.0F);
         }
      } else {
         this.rotate((float)Math.toDegrees((double)(MathHelper.cos(loop * 0.2F) * 0.2F)), 0.0F, 0.0F);
      }

   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (!"cape".equals(partName)) {
         return layer == null;
      } else {
         return layer != null && "cape".equals(layer.name);
      }
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      return super.getPartColor(partName, entity, layer, trophy, loop);
   }
}
