package com.teamacronymcoders.base.client.models;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import com.teamacronymcoders.base.items.IHasSubItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHasModel extends IHasSubItems, IModAware {
   default List<String> getModelNames(List<String> modelNames) {
      return modelNames;
   }

   default List<ResourceLocation> getResourceLocations(List<ResourceLocation> resourceLocations) {
      IBaseMod mod = this.getMod();
      if (mod != null) {
         this.getModelNames(new ArrayList()).forEach((modelName) -> {
            resourceLocations.add(new ResourceLocation(mod.getID(), modelName.replace(mod.getID() + ".", "")));
         });
      }

      return resourceLocations;
   }

   @SideOnly(Side.CLIENT)
   default List<ModelResourceLocation> getModelResourceLocations(List<ModelResourceLocation> models) {
      for(ResourceLocation resourceLocation : this.getResourceLocations(new ArrayList())) {
         models.add(new ModelResourceLocation(resourceLocation, "inventory"));
      }

      return models;
   }
}
