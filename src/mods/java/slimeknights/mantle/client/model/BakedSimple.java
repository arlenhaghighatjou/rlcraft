package slimeknights.mantle.client.model;

import com.google.common.collect.ImmutableList;
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

public class BakedSimple implements IBakedModel {
   private final List<BakedQuad> quads;
   private final ImmutableMap<TransformType, TRSRTransformation> transforms;
   private final EaglerTextureAtlasSprite particle;
   private final boolean ambientOcclusion;
   private final boolean isGui3d;
   private final ItemOverrideList overrides;

   public BakedSimple(ImmutableList<BakedQuad> quads, ImmutableMap<TransformType, TRSRTransformation> transforms, IBakedModel base) {
      this(quads, transforms, base.getParticleTexture(), base.isAmbientOcclusion(), base.isGui3d(), base.getOverrides());
   }

   public BakedSimple(List<BakedQuad> quads, ImmutableMap<TransformType, TRSRTransformation> transforms, IBakedModel base) {
      this(quads, transforms, base.getParticleTexture(), base.isAmbientOcclusion(), base.isGui3d(), base.getOverrides());
   }

   public BakedSimple(List<BakedQuad> quads, ImmutableMap<TransformType, TRSRTransformation> transforms, EaglerTextureAtlasSprite particle, boolean ambientOcclusion, boolean isGui3d, ItemOverrideList overrides) {
      this.quads = quads;
      this.particle = particle;
      this.transforms = transforms;
      this.ambientOcclusion = ambientOcclusion;
      this.isGui3d = isGui3d;
      this.overrides = overrides;
   }

   @Nonnull
   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.quads;
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   @Nonnull
   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.particle;
   }

   @Nonnull
   public ItemCameraTransforms getItemCameraTransforms() {
      return ItemCameraTransforms.DEFAULT;
   }

   @Nonnull
   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      return PerspectiveMapWrapper.handlePerspective(this, this.transforms, cameraTransformType);
   }

   public static class Wrapper extends BakedSimple {
      private final IBakedModel parent;

      public Wrapper(ImmutableList<BakedQuad> quads, IBakedModel base) {
         super(quads, (ImmutableMap)null, base);
         this.parent = base;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
         Pair<? extends IBakedModel, Matrix4f> pair = this.parent.handlePerspective(cameraTransformType);
         return Pair.of(this, pair.getRight());
      }
   }
}
