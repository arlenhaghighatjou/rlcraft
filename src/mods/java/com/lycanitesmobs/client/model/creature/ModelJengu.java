package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJengu extends ModelTemplateElemental {
   public ModelJengu() {
      this(1.0F);
   }

   public ModelJengu(float shadowSize) {
      this.initModel("jengu", LycanitesMobs.modInfo, "entity/jengu");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.2F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "", false, LayerCreatureEffect.BLEND.NORMAL.id, true));
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      float rotX = 0.0F;
      float rotY = 0.0F;
      float rotZ = 0.0F;
      if (partName.equals("effectouter")) {
         rotX = 15.0F;
      }

      if (partName.equals("effectinner")) {
         rotX = 30.0F;
      }

      this.rotate(rotX, rotY, rotZ);
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
   }

   public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
      return partName.contains("effect") ? super.getBaseTextureOffset(partName, entity, trophy, loop) : new Vector2f(0.0F, loop);
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (partName.contains("effect")) {
         return layer != null;
      } else {
         return layer == null;
      }
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      return partName.contains("effect") ? new Vector4f(1.0F, 1.0F, 1.0F, 0.5F) : super.getPartColor(partName, entity, layer, trophy, loop);
   }
}
