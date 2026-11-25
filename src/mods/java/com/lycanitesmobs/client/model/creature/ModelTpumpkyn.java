package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureScrolling;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTpumpkyn extends ModelTemplateQuadruped {
   public ModelTpumpkyn() {
      this(1.0F);
   }

   public ModelTpumpkyn(float shadowSize) {
      this.initModel("tpumpkyn", LycanitesMobs.modInfo, "entity/tpumpkyn");
      this.trophyScale = 0.8F;
      this.trophyOffset = new float[]{0.0F, -0.2F, 0.0F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureScrolling(renderer, "effect", true, LayerCreatureEffect.BLEND.NORMAL.id, false, new Vector2f(0.0F, 2.0F)));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      if (distance < 0.1F && !((BaseCreatureEntity)entity).hasAttackTarget()) {
         if (partName.contains("body")) {
            this.translate(0.0F, -0.25F, 0.0F);
         }

         if (partName.contains("leg")) {
            this.scale(distance, distance, distance);
         }

         if (partName.equals("eyes") || partName.equals("effect")) {
            this.scale(0.0F, 0.0F, 0.0F);
         }

         if (partName.equals("mouth")) {
            this.translate(0.0F, 0.1F, 0.0F);
         }

      } else {
         super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
         if (partName.contains("hair")) {
            float rotZ = (float)Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F));
            float rotX = (float)Math.toDegrees((double)(MathHelper.sin(loop * 0.067F) * 0.05F));
            this.rotate(rotX, 0.0F, rotZ);
         }

      }
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (!partName.contains("effect") && !partName.contains("eye")) {
         return layer == null;
      } else {
         return layer != null && "effect".equals(layer.name);
      }
   }
}
