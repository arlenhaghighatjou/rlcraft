package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureDjinn;
import javax.vecmath.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDjinn extends ModelTemplateElemental {
   public ModelDjinn() {
      this(1.0F);
   }

   public ModelDjinn(float shadowSize) {
      this.initModel("djinn", LycanitesMobs.modInfo, "entity/djinn");
      this.trophyScale = 1.2F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
      this.trophyMouthOffset = new float[]{0.0F, -0.25F, 0.0F};
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      renderer.addLayer(new LayerCreatureDjinn(renderer));
   }

   public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
      return !partName.contains("ribbon");
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      return partName.contains("ribbon") ? layer instanceof LayerCreatureDjinn : super.canRenderPart(partName, entity, layer, trophy);
   }

   public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
      return partName.contains("ribbon") ? new Vector2f(-loop * 25.0F, 0.0F) : super.getBaseTextureOffset(partName, entity, trophy, loop);
   }
}
