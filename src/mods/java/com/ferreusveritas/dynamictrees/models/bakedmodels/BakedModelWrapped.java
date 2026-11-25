package com.ferreusveritas.dynamictrees.models.bakedmodels;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public abstract class BakedModelWrapped implements IBakedModel {
   private IBakedModel wrappedModel;

   public BakedModelWrapped() {
   }

   public IBakedModel getWrappedModel() {
      if (this.wrappedModel == null) {
         this.wrappedModel = this.createModel();
      }

      return this.wrappedModel;
   }

   public abstract IBakedModel createModel();

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.getWrappedModel().getQuads(state, side, rand);
   }

   public boolean isGui3d() {
      return this.getWrappedModel().isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.getWrappedModel().isBuiltInRenderer();
   }

   public boolean isAmbientOcclusion() {
      return this.getWrappedModel().isAmbientOcclusion();
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.getWrappedModel().getParticleTexture();
   }

   public ItemOverrideList getOverrides() {
      return this.getWrappedModel().getOverrides();
   }
}
