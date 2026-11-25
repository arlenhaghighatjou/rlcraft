package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAegis extends ModelTemplateElemental {
   float maxLeg;

   public ModelAegis() {
      this(1.0F);
   }

   public ModelAegis(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("aegis", LycanitesMobs.modInfo, "entity/aegis");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "", true, LayerCreatureEffect.BLEND.NORMAL.id, true));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (partName.contains("shieldupper")) {
         this.shiftOrigin(partName, "body");
         this.rotate(0.0F, loop * 8.0F, 0.0F);
         this.shiftOriginBack(partName, "body");
      }

      if (entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
         if (!entityCreature.isBlocking()) {
            if (partName.equals("core")) {
               ;
            }

            if (partName.contains("shieldupper")) {
               float orbit = loop * 16.0F;
               if (partName.contains("left")) {
                  orbit += 90.0F;
               }

               this.shiftOrigin(partName, "body");
               this.rotate(0.0F, orbit, 0.0F);
               this.shiftOriginBack(partName, "body");
               this.translate(0.0F, -0.25F, 0.0F);
               this.scale(0.5F, 1.0F, 1.0F);
               if (!"shieldupperleft01".equals(partName) && !"shieldupperright01".equals(partName)) {
                  if ("shieldupperleft02".equals(partName) || "shieldupperright02".equals(partName)) {
                     this.rotate(90.0F, 0.0F, 0.0F);
                  }
               } else {
                  this.rotate(-90.0F, 0.0F, 0.0F);
               }
            }

            if (partName.contains("shieldlower")) {
               this.scale(0.5F, 1.0F, 1.0F);
            }
         }
      }

   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      if (!this.isArmorPart(partName)) {
         float glowSpeed = 40.0F;
         float glow = loop * glowSpeed % 360.0F;
         float color = (float)Math.cos(Math.toRadians((double)glow)) * 0.1F + 0.9F;
         return new Vector4f(color, color, color, 1.0F);
      } else {
         return super.getPartColor(partName, entity, layer, trophy, loop);
      }
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (this.isArmorPart(partName)) {
         return layer == null;
      } else {
         return layer instanceof LayerCreatureEffect;
      }
   }

   protected boolean isArmorPart(String partName) {
      return "shoulders".equals(partName) || "helm".equals(partName) || partName.contains("shield");
   }
}
