package rustic.client.models;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import rustic.common.blocks.BlockLattice;
import rustic.core.Rustic;

public class LatticeModel implements IModel {
   public LatticeModel() {
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      IBakedModel base = null;
      IBakedModel[] bars = new IBakedModel[6];
      IBakedModel leaves = null;
      IBakedModel[] leaf_bars = new IBakedModel[6];
      ModelRotation[] rotations = new ModelRotation[]{ModelRotation.X90_Y0, ModelRotation.X270_Y0, ModelRotation.X0_Y0, ModelRotation.X0_Y180, ModelRotation.X0_Y270, ModelRotation.X0_Y90};

      try {
         IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("rustic", "block/lattice_iron_base"));
         IModel barModel = ModelLoaderRegistry.getModel(new ResourceLocation("rustic", "block/lattice_iron_bar"));
         IModel leavesModel = ModelLoaderRegistry.getModel(new ResourceLocation("rustic", "block/lattice_leaves_base"));
         IModel leaf_barModel = ModelLoaderRegistry.getModel(new ResourceLocation("rustic", "block/lattice_leaves_bar"));

         for(int i = 0; i < 6; ++i) {
            base = baseModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
            bars[i] = barModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
            leaves = leavesModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
            leaf_bars[i] = leaf_barModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
         }
      } catch (Exception var14) {
         Rustic.logger.warn(var14.getMessage());
      }

      return (IBakedModel)(base == null ? ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter) : new LatticeModel.BakedLatticeModel(base, bars, leaves, leaf_bars));
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of(new ResourceLocation("rustic", "block/lattice_iron_base"), new ResourceLocation("rustic", "block/lattice_iron_bar"), new ResourceLocation("rustic", "block/lattice_leaves_base"), new ResourceLocation("rustic", "block/lattice_leaves_bar"));
   }

   public static class BakedLatticeModel implements IBakedModel {
      private IBakedModel base;
      private IBakedModel[] bars;
      private IBakedModel leaves;
      private IBakedModel[] leaf_bars;

      public BakedLatticeModel(IBakedModel base, IBakedModel[] bars, IBakedModel leaves, IBakedModel[] leaf_bars) {
         this.base = base;
         this.bars = bars;
         this.leaves = leaves;
         this.leaf_bars = leaf_bars;
      }

      public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
         if (!(state instanceof IExtendedBlockState)) {
            return Collections.EMPTY_LIST;
         } else {
            List<IBakedModel> subModels = this.getSubModels((IExtendedBlockState)state);
            List<BakedQuad> quads = new ArrayList();

            for(IBakedModel model : subModels) {
               quads.addAll(model.getQuads(state, side, rand));
            }

            return quads;
         }
      }

      private List<IBakedModel> getSubModels(IExtendedBlockState state) {
         List<IBakedModel> subModels = new ArrayList();
         subModels.add(this.base);
         boolean foliage = ((Boolean)state.getValue(BlockLattice.LEAVES)).booleanValue();
         if (foliage) {
            subModels.add(this.leaves);
         }

         try {
            for(int i = 0; i < 6; ++i) {
               if (((Boolean)state.getValue(BlockLattice.CONNECTIONS[i])).booleanValue()) {
                  subModels.add(this.bars[i]);
                  if (foliage) {
                     subModels.add(this.leaf_bars[i]);
                  }
               }
            }
         } catch (NullPointerException var5) {
            ;
         }

         return subModels;
      }

      public boolean isAmbientOcclusion() {
         return true;
      }

      public boolean isGui3d() {
         return true;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public EaglerTextureAtlasSprite getParticleTexture() {
         return this.base.getParticleTexture();
      }

      public ItemOverrideList getOverrides() {
         return ItemOverrideList.NONE;
      }
   }

   public static class LatticeModelLoader implements ICustomModelLoader {
      private static LatticeModel model;

      public LatticeModelLoader() {
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
         model = new LatticeModel();
      }

      public boolean accepts(ResourceLocation modelLocation) {
         return modelLocation.getResourceDomain().equals("rustic") && modelLocation.getResourcePath().equals("iron_lattice");
      }

      public IModel loadModel(ResourceLocation modelLocation) throws Exception {
         if (model == null) {
            model = new LatticeModel();
         }

         return model;
      }
   }
}
