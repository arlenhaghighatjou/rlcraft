package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureEquipment extends LayerCreatureBase {
   public String equipmentSlot;

   public LayerCreatureEquipment(RenderCreature renderer, String equipmentSlot) {
      super(renderer);
      this.equipmentSlot = equipmentSlot;
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      if (super.canRenderLayer(entity, scale) && this.equipmentSlot != null) {
         return entity.getEquipmentName(this.equipmentSlot) != null;
      } else {
         return false;
      }
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return entity.getEquipmentTexture(entity.getEquipmentName(this.equipmentSlot));
   }
}
