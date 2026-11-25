package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureSaddle extends LayerCreatureBase {
   public LayerCreatureSaddle(RenderCreature renderer) {
      super(renderer);
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      return super.canRenderLayer(entity, scale) && entity instanceof RideableCreatureEntity ? ((RideableCreatureEntity)entity).hasSaddle() : false;
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return entity.getEquipmentTexture("saddle");
   }
}
