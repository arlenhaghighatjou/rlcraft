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
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVapula extends ModelTemplateElemental {
   float maxLeg;

   public ModelVapula() {
      this(1.0F);
   }

   public ModelVapula(float shadowSize) {
      this.maxLeg = 0.0F;
      this.initModel("vapula", LycanitesMobs.modInfo, "entity/vapula");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "", true, LayerCreatureEffect.BLEND.NORMAL.id, true));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (partName.contains("effect")) {
         this.shiftOrigin(partName, "crystals");
         this.rotate(0.0F, 0.0F, loop * 8.0F);
         this.shiftOriginBack(partName, "crystals");
      } else if (!partName.equals("fingerleft01") && !partName.equals("fingerright01")) {
         if (!partName.equals("fingerleft02") && !partName.equals("fingerright02")) {
            if (partName.equals("fingerleft03") || partName.equals("fingerright03")) {
               this.rotate(0.0F, (float)Math.toDegrees((double)(MathHelper.cos((loop + 40.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F);
            }
         } else {
            this.rotate(0.0F, (float)Math.toDegrees((double)(MathHelper.cos((loop + 20.0F) * 0.2F) * 0.2F - 0.2F)), 0.0F);
         }
      } else {
         this.rotate(0.0F, (float)Math.toDegrees((double)(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F)), 0.0F);
      }

   }

   public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
      return true;
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (!this.isCrystal(partName)) {
         return layer == null;
      } else {
         if (layer instanceof LayerCreatureEffect) {
            if (!partName.contains("effect")) {
               return true;
            }

            if (entity instanceof BaseCreatureEntity) {
               int attackPhase = ((BaseCreatureEntity)entity).getAttackPhase();
               if (((BaseCreatureEntity)entity).isAttackOnCooldown() && attackPhase == 0) {
                  return false;
               }

               if ("effect01".equals(partName)) {
                  return attackPhase <= 7;
               }

               if ("effect02".equals(partName)) {
                  return attackPhase <= 6;
               }

               if ("effect03".equals(partName)) {
                  return attackPhase <= 5;
               }

               if ("effect04".equals(partName)) {
                  return attackPhase <= 4;
               }

               if ("effect05".equals(partName)) {
                  return attackPhase <= 3;
               }

               if ("effect06".equals(partName)) {
                  return attackPhase <= 2;
               }

               if ("effect07".equals(partName)) {
                  return attackPhase <= 1;
               }

               if ("effect08".equals(partName)) {
                  return attackPhase <= 0;
               }
            }
         }

         return false;
      }
   }

   protected boolean isCrystal(String partName) {
      if (!"eye".equals(partName) && !"crystals".equals(partName)) {
         return partName.contains("effect") || partName.contains("finger");
      } else {
         return true;
      }
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      if (this.isCrystal(partName)) {
         float glowSpeed = 40.0F;
         float glow = loop * glowSpeed % 360.0F;
         float color = (float)Math.cos(Math.toRadians((double)glow)) * 0.1F + 0.9F;
         return new Vector4f(color, color, color, 1.0F);
      } else {
         return super.getPartColor(partName, entity, layer, trophy, loop);
      }
   }
}
