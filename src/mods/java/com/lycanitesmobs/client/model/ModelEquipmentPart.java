package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import javax.vecmath.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModelEquipmentPart extends ModelItemBase {
   public ModelEquipmentPart(String name, ModInfo groupInfo) {
      this.initModel(name, groupInfo, "equipment/" + name);
   }

   public void addCustomLayers(IItemModelRenderer renderer) {
      super.addCustomLayers(renderer);
   }

   public ResourceLocation getTexture(ItemStack itemStack, LayerItem layer) {
      if (!(itemStack.getItem() instanceof ItemEquipmentPart)) {
         return null;
      } else {
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
         String suffix = "";
         if (layer != null && layer.textureSuffix != null && !layer.textureSuffix.isEmpty()) {
            suffix = "_" + layer.textureSuffix;
         }

         return itemEquipmentPart.getTexture(itemStack, suffix);
      }
   }

   public Vector4f getPartColor(String partName, ItemStack itemStack, LayerItem layer, float loop) {
      if (!(itemStack.getItem() instanceof ItemEquipmentPart)) {
         return super.getPartColor(partName, itemStack, layer, loop);
      } else {
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
         return layer != null ? layer.getPartColor(partName, itemStack, loop) : itemEquipmentPart.getColor(itemStack);
      }
   }
}
