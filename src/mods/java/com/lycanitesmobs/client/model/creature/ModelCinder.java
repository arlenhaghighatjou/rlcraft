package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import javax.vecmath.Vector2f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCinder extends ModelTemplateElemental {
   public ModelCinder() {
      this(1.0F);
   }

   public ModelCinder(float shadowSize) {
      this.initModel("cinder", LycanitesMobs.modInfo, "entity/cinder");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureEffect(renderer, "eyes"));
   }

   public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
      return partName.contains("effect") ? super.getBaseTextureOffset(partName, entity, trophy, loop) : new Vector2f(loop, 0.0F);
   }

   public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderStart(layer, entity, renderAsTrophy);
      GlStateManager.disableLighting();
   }

   public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderFinish(layer, entity, renderAsTrophy);
      GlStateManager.enableLighting();
   }
}
