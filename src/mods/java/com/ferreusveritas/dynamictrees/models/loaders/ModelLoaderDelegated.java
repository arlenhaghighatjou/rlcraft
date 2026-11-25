package com.ferreusveritas.dynamictrees.models.loaders;

import java.util.function.BiFunction;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;

public class ModelLoaderDelegated extends ModelLoaderGeneric {
   protected final BiFunction<ResourceLocation, ModelBlock, IModel> loaderDelegate;

   public ModelLoaderDelegated(String key, ResourceLocation resourceName, BiFunction<ResourceLocation, ModelBlock, IModel> loaderDelegate) {
      super(key, resourceName);
      this.loaderDelegate = loaderDelegate;
   }

   protected IModel loadModel(ResourceLocation resourceLocation, ModelBlock baseModelBlock) {
      return (IModel)this.loaderDelegate.apply(resourceLocation, baseModelBlock);
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }
}
