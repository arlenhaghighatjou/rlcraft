package me.ichun.mods.ichunutil.client.model.item;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelEmpty implements IBakedModel {
   public static final ModelEmpty INSTANCE = new ModelEmpty();
   private static final List<BakedQuad> DUMMY_LIST = Collections.emptyList();
   private TextureAtlasSprite particleTexture;

   private ModelEmpty() {
      this.particleTexture = null;
   }

   public ModelEmpty(TextureAtlasSprite particleTexture) {
      this.particleTexture = particleTexture;
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      return DUMMY_LIST;
   }

   public boolean isAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particleTexture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return ItemCameraTransforms.DEFAULT;
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
