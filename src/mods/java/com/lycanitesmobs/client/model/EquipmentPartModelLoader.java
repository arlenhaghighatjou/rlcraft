package com.lycanitesmobs.client.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class EquipmentPartModelLoader implements ICustomModelLoader {
   public EquipmentPartModelLoader() {
   }

   public boolean accepts(ResourceLocation modelLocation) {
      return "lycanitesmobs".equals(modelLocation.getResourceDomain()) && "equipmentpart".equals(modelLocation.getResourcePath());
   }

   public IModel loadModel(ResourceLocation modelLocation) throws Exception {
      return (state, format, bakedTextureGetter) -> {
         return new EquipmentPartModel();
      };
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
   }
}
