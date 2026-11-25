package vazkii.quark.oddities.client.bakery;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

public class WrapperWithParticleTexture implements IBakedModel {
   private final EaglerTextureAtlasSprite sprite;
   private final IBakedModel parent;

   public WrapperWithParticleTexture(EaglerTextureAtlasSprite sprite, IBakedModel parent) {
      this.sprite = sprite;
      this.parent = parent;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      return this.parent.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.parent.isAmbientOcclusion();
   }

   public boolean isAmbientOcclusion(IBlockState state) {
      return this.parent.isAmbientOcclusion(state);
   }

   @Nonnull
   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull TransformType cameraTransformType) {
      return this.parent.handlePerspective(cameraTransformType);
   }

   public boolean isGui3d() {
      return this.parent.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.parent.isBuiltInRenderer();
   }

   @Nonnull
   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.sprite;
   }

   @Nonnull
   public ItemOverrideList getOverrides() {
      return this.parent.getOverrides();
   }
}
