package com.ferreusveritas.dynamictrees.models.blockmodels;

import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelBlockSurfaceRoot;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockSurfaceRoot implements IModel {
   public ResourceLocation barkTexture;

   public ModelBlockSurfaceRoot(ModelBlock modelBlock) {
      this.barkTexture = new ResourceLocation(modelBlock.resolveTextureName("bark"));
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.copyOf(new ResourceLocation[0]);
   }

   public Collection<ResourceLocation> getTextures() {
      return ImmutableList.copyOf(new ResourceLocation[]{this.barkTexture});
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      try {
         return new BakedModelBlockSurfaceRoot(this.barkTexture, bakedTextureGetter);
      } catch (Exception var5) {
         System.err.println("BakedModelBlockSurfaceRoot.bake() failed due to exception:" + var5);
         return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
      }
   }
}
