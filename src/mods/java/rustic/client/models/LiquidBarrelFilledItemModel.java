package rustic.client.models;

import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import rustic.client.util.FluidClientUtil;

public class LiquidBarrelFilledItemModel implements IBakedModel {
   private int amount;
   private int capacity;
   private Fluid fluid;
   private IBakedModel parentModel;

   public LiquidBarrelFilledItemModel(IBakedModel parentModel, int amount, int capacity, Fluid fluid) {
      this.parentModel = parentModel;
      this.amount = amount;
      this.capacity = capacity;
      this.fluid = fluid;
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> combinedQuadsList = new ArrayList(this.parentModel.getQuads(state, side, rand));
      combinedQuadsList.addAll(this.getLiquidQuads(this.amount, this.capacity, this.fluid));
      return combinedQuadsList;
   }

   public boolean isAmbientOcclusion() {
      return this.parentModel.isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.parentModel.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.parentModel.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.parentModel.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.parentModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      throw new UnsupportedOperationException("The finalized model does not have an override list.");
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      if (this.parentModel instanceof IBakedModel) {
         Matrix4f matrix4f = (Matrix4f)this.parentModel.handlePerspective(cameraTransformType).getRight();
         return Pair.of(this, matrix4f);
      } else {
         ItemCameraTransforms itemCameraTransforms = this.parentModel.getItemCameraTransforms();
         ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
         TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
         Matrix4f mat = null;
         if (tr != null) {
            mat = tr.getMatrix();
         }

         return Pair.of(this, mat);
      }
   }

   private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v) {
      return new int[]{Float.floatToRawIntBits(x), Float.floatToRawIntBits(y), Float.floatToRawIntBits(z), color, Float.floatToRawIntBits(texture.getInterpolatedU((double)u)), Float.floatToRawIntBits(texture.getInterpolatedV((double)v)), 0};
   }

   private List<BakedQuad> getLiquidQuads(int amount, int capacity, Fluid fluid) {
      List<BakedQuad> returnList = new ArrayList();
      if (fluid != null) {
         TextureAtlasSprite fluidTexture = (TextureAtlasSprite)FluidClientUtil.stillTextures.get(fluid);
         if (fluidTexture == null) {
            return returnList;
         }

         float x2 = 0.8125F;
         float x1 = 0.8125F;
         float x4 = 0.1875F;
         float x3 = 0.1875F;
         float z4 = 0.8125F;
         float z1 = 0.8125F;
         float z3 = 0.1875F;
         float z2 = 0.1875F;
         float y2;
         float y3;
         float y4;
         float y1 = y2 = y3 = y4 = 0.125F + 0.8125F * ((float)amount / (float)capacity);
         int c = -16777216;
         if (fluid != null) {
            c = fluid.getColor();
         }

         int blue = c & 255;
         int green = c >> 8 & 255;
         int red = c >> 16 & 255;
         int alpha = c >> 24 & 255;
         int color = (red & 255) << 24 | (green & 255) << 16 | (blue & 255) << 8 | alpha & 255;
         int[] vertexData = Ints.concat(new int[][]{this.vertexToInts(x1, y1, z1, color, fluidTexture, 16.0F, 16.0F), this.vertexToInts(x2, y2, z2, color, fluidTexture, 16.0F, 0.0F), this.vertexToInts(x3, y3, z3, color, fluidTexture, 0.0F, 0.0F), this.vertexToInts(x4, y4, z4, color, fluidTexture, 0.0F, 16.0F)});
         returnList.add(new BakedQuad(vertexData, 0, EnumFacing.UP, fluidTexture, true, DefaultVertexFormats.BLOCK));
      }

      return returnList;
   }
}
