package rustic.client.models;

import java.util.Collections;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import rustic.common.blocks.ModBlocks;

public class LiquidBarrelItemModel implements IBakedModel {
   private IBakedModel baseLiquidBarrelModel;
   private LiquidBarrelItemOverrideList liquidBarrelItemOverrideList;
   public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ModBlocks.LIQUID_BARREL.getRegistryName(), "inventory");

   public LiquidBarrelItemModel(IBakedModel baseModel) {
      this.baseLiquidBarrelModel = baseModel;
      this.liquidBarrelItemOverrideList = new LiquidBarrelItemOverrideList(Collections.EMPTY_LIST);
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.baseLiquidBarrelModel.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.baseLiquidBarrelModel.isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.baseLiquidBarrelModel.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.baseLiquidBarrelModel.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.baseLiquidBarrelModel.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseLiquidBarrelModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.liquidBarrelItemOverrideList;
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      if (this.baseLiquidBarrelModel instanceof IBakedModel) {
         Matrix4f matrix4f = (Matrix4f)this.baseLiquidBarrelModel.handlePerspective(cameraTransformType).getRight();
         return Pair.of(this, matrix4f);
      } else {
         ItemCameraTransforms itemCameraTransforms = this.baseLiquidBarrelModel.getItemCameraTransforms();
         ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
         TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
         Matrix4f mat = null;
         if (tr != null) {
            mat = tr.getMatrix();
         }

         return Pair.of(this, mat);
      }
   }
}
