package com.TominoCZ.FBP.model;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class FBPSimpleBakedModel implements IBakedModel {
   private final List<BakedQuad>[] quads;
   private final IBakedModel parent;
   private TextureAtlasSprite particle;

   public FBPSimpleBakedModel() {
      this((IBakedModel)null);
   }

   public FBPSimpleBakedModel(IBakedModel parent) {
      this.quads = new List[7];
      this.parent = parent;

      for(int i = 0; i < this.quads.length; ++i) {
         this.quads[i] = new ArrayList();
      }

   }

   public void setParticle(TextureAtlasSprite particle) {
      this.particle = particle;
   }

   public void addQuad(EnumFacing side, BakedQuad quad) {
      this.quads[side == null ? 6 : side.ordinal()].add(quad);
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.quads[side == null ? 6 : side.ordinal()];
   }

   public boolean isAmbientOcclusion() {
      return this.parent != null ? this.parent.isAmbientOcclusion() : true;
   }

   public boolean isGui3d() {
      return this.parent != null ? this.parent.isGui3d() : true;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      if (this.particle != null) {
         return this.particle;
      } else {
         return this.parent != null ? this.parent.getParticleTexture() : null;
      }
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Pair<? extends IBakedModel, Matrix4f> pair = this.parent.handlePerspective(cameraTransformType);
      return (Pair<? extends IBakedModel, Matrix4f>)(pair.getLeft() != this.parent ? pair : ImmutablePair.of(this, pair.getRight()));
   }
}
