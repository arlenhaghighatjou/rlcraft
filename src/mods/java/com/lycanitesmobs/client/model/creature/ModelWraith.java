package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureScrolling;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWraith extends ModelTemplateElemental {
   public ModelWraith() {
      this(1.0F);
   }

   public ModelWraith(float shadowSize) {
      this.initModel("wraith", LycanitesMobs.modInfo, "entity/wraith");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "overlay", true, LayerCreatureEffect.BLEND.NORMAL.id, true));
      renderer.addLayer(new LayerCreatureEffect(renderer, "skull", false, LayerCreatureEffect.BLEND.NORMAL.id, true));
      renderer.addLayer(new LayerCreatureScrolling(renderer, "", true, LayerCreatureEffect.BLEND.ADD.id, true, new Vector2f(-8.0F, 0.0F)));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      if (entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
         if (entityCreature.hasAttackTarget() && partName.equals("mouth")) {
            this.rotate(20.0F + (float)(-Math.toDegrees((double)(MathHelper.cos(loop) * 0.1F))), 0.0F, 0.0F);
         }
      }

      float vibration = loop * 2.0F;
      if ("head".equals(partName)) {
         this.translate(MathHelper.cos(vibration) * 0.01F, MathHelper.cos(vibration) * 0.01F, MathHelper.cos(vibration) * 0.01F);
      } else if ("mouth".equals(partName)) {
         this.rotate((float)(-Math.toDegrees((double)(MathHelper.cos(vibration) * 0.025F))), 0.0F, 0.0F);
      }

   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if ("head".equals(partName)) {
         return layer == null || "overlay".equals(layer.name);
      } else if ("fire".equals(partName)) {
         return layer != null && layer instanceof LayerCreatureScrolling;
      } else {
         return layer != null && "skull".equals(layer.name);
      }
   }

   public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
      return new Vector2f(-loop * 8.0F, 0.0F);
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      if (layer != null && !(layer instanceof LayerCreatureScrolling)) {
         return super.getPartColor(partName, entity, layer, trophy, loop);
      } else {
         float glowSpeed = 80.0F;
         float glow = loop * glowSpeed % 360.0F;
         float color = (float)Math.cos(Math.toRadians((double)glow)) * 0.1F + 0.9F;
         float alpha = 1.0F;
         if ("fire".equals(partName)) {
            alpha = 1.0F;
         }

         return new Vector4f(color, color, color, alpha);
      }
   }

   public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderStart(layer, entity, renderAsTrophy);
      if (layer == null) {
         int i = 15728880;
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      }
   }

   public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderFinish(layer, entity, renderAsTrophy);
      if (layer == null) {
         int i = entity.getBrightnessForRender();
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      }
   }
}
