package rustic.client.models;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

public class TEISRModel implements IBakedModel {
   protected final List<BakedQuad> quads = new ArrayList();
   protected final IBakedModel baseModel;

   public TEISRModel(IBakedModel baseModel) {
      this.baseModel = baseModel;
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.quads;
   }

   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   public ItemOverrideList getOverrides() {
      return this.baseModel.getOverrides();
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Matrix4f matrix4f = (Matrix4f)this.baseModel.handlePerspective(cameraTransformType).getRight();
      return Pair.of(this, matrix4f);
   }
}
