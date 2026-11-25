package com.ferreusveritas.dynamictrees.models.blockmodels;

import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelBlockBranchBasic;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockBranchBasic implements IModel {
   public ResourceLocation barkTexture;
   public ResourceLocation ringsTexture;

   public ModelBlockBranchBasic(ModelBlock modelBlock) {
      this.barkTexture = new ResourceLocation(modelBlock.resolveTextureName("bark"));
      this.ringsTexture = new ResourceLocation(modelBlock.resolveTextureName("rings"));
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.copyOf(new ResourceLocation[0]);
   }

   public Collection<ResourceLocation> getTextures() {
      return ImmutableList.copyOf(new ResourceLocation[]{this.barkTexture, this.ringsTexture});
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      try {
         return new BakedModelBlockBranchBasic(this.barkTexture, this.ringsTexture, bakedTextureGetter);
      } catch (Exception var5) {
         System.err.println("BranchModel.bake() failed due to exception:" + var5);
         return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
      }
   }
}
