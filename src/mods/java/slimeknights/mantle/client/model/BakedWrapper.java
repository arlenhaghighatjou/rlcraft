package slimeknights.mantle.client.model;

import com.google.common.collect.ImmutableMap;
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
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

public class BakedWrapper implements IBakedModel {
   protected final IBakedModel parent;

   public BakedWrapper(IBakedModel parent) {
      this.parent = parent;
   }

   @Nonnull
   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.parent.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.parent.isAmbientOcclusion();
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
      return this.parent.getOverrides();
   }

   public static class Perspective extends BakedWrapper implements IBakedModel {
      protected final ImmutableMap<TransformType, TRSRTransformation> transforms;

      public Perspective(IBakedModel parent, ImmutableMap<TransformType, TRSRTransformation> transforms) {
         super(parent);
         this.transforms = transforms;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
         return PerspectiveMapWrapper.handlePerspective(this, this.transforms, cameraTransformType);
      }
   }
}
