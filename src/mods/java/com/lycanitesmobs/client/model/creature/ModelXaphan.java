package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
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
public class ModelXaphan extends ModelTemplateElemental {
   float maxLeg;

   public ModelXaphan() {
      this(1.0F);
   }

   public ModelXaphan(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("xaphan", LycanitesMobs.modInfo, "entity/xaphan");
      this.trophyScale = 0.8F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureScrolling(renderer, "ball", true, LayerCreatureEffect.BLEND.NORMAL.id, true, new Vector2f(0.0F, 4.0F)));
      renderer.addLayer(new LayerCreatureEffect(renderer, "ring", true, LayerCreatureEffect.BLEND.ADD.id, true));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (!partName.equals("fingerleft01") && !partName.equals("fingerright01")) {
         if (!partName.equals("fingerleft02") && !partName.equals("fingerright02")) {
            if (!partName.equals("fingerleft03") && !partName.equals("fingerright03")) {
               if (partName.equals("shoulderleft")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)), 0.0F, (float)Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.1F)));
               } else if (partName.equals("shoulderright")) {
                  this.rotate((float)Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F)), 0.0F, (float)(-Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.1F))));
               } else if (partName.equals("effect01")) {
                  this.rotate(0.0F, loop * 16.0F, 0.0F);
               } else if (partName.contains("ball")) {
                  this.shiftOrigin(partName, "body");
                  this.rotate(0.0F, loop * -8.0F, 0.0F);
                  this.shiftOriginBack(partName, "body");
                  float ballScale = (float)(1.0D + 0.2D * (double)MathHelper.cos(loop));
                  if (partName.equals("ball02")) {
                     ballScale = (float)(1.0D + 0.2D * (double)MathHelper.cos(loop + 20.0F));
                  } else if (partName.equals("ball03")) {
                     ballScale = (float)(1.0D + 0.2D * (double)MathHelper.cos(loop + 40.0F));
                  } else if (partName.equals("ball04")) {
                     ballScale = (float)(1.0D + 0.2D * (double)MathHelper.cos(loop + 60.0F));
                  }

                  this.scale(ballScale, ballScale, ballScale);
               } else if (partName.contains("spine")) {
                  float coil = 8.0F;
                  this.rotate(MathHelper.cos(loop * 0.1F) * coil - coil * 90.0F, 0.0F, 0.0F);
               }
            } else {
               this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 40.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
            }
         } else {
            this.rotate((float)Math.toDegrees((double)(MathHelper.cos((loop + 20.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
         }
      } else {
         this.rotate((float)Math.toDegrees((double)(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F)), 0.0F, 0.0F);
      }

   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if ("effect01".equals(partName)) {
         return layer != null && "ring".equals(layer.name);
      } else if (!partName.contains("ball")) {
         return layer == null;
      } else {
         return layer != null && "ball".equals(layer.name);
      }
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      return super.getPartColor(partName, entity, layer, trophy, loop);
   }
}
