package slimeknights.mantle.client.model;

import java.util.List;
import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.client.ModelHelper;

public class BlockItemModelWrapper implements IBakedModel {
   private final IBakedModel parent;

   public BlockItemModelWrapper(IBakedModel parent) {
      this.parent = parent;
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Matrix4f matrix = null;
      if (cameraTransformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
         matrix = ModelHelper.BLOCK_THIRD_PERSON_RIGHT.getMatrix();
      } else if (cameraTransformType == TransformType.THIRD_PERSON_LEFT_HAND) {
         matrix = ModelHelper.BLOCK_THIRD_PERSON_LEFT.getMatrix();
      }

      return Pair.of(this, matrix);
   }

   @Nonnull
   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.parent.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return this.parent.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.parent.isBuiltInRenderer();
   }

   @Nonnull
   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.parent.getParticleTexture();
   }

   @Nonnull
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.parent.getItemCameraTransforms();
   }

   @Nonnull
   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
