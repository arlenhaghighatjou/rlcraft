package com.ferreusveritas.dynamictrees.models.loaders;

import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelSapling;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLoaderSapling implements ICustomModelLoader {
   public ModelLoaderSapling() {
   }

   public IModel loadModel(ResourceLocation modelLocation) throws Exception {
      return new IModel() {
         public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            return new BakedModelSapling(state, format, bakedTextureGetter);
         }
      };
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
   }

   public boolean accepts(ResourceLocation modelLocation) {
      return modelLocation.getResourceDomain().equals("dynamictrees") && modelLocation.getResourcePath().endsWith("sapling.smart");
   }
}
