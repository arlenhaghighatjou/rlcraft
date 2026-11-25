package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.blocks.SpeciesProperty;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedModelSapling implements IBakedModel {
   private static IBakedModel[] modelMap;
   private static IBakedModel errorSaplingModel;
   private static EaglerTextureAtlasSprite particleTexture;

   public static IBakedModel getModelForSapling(Species species) {
      int modelId = species.saplingModelId;
      IBakedModel bakedModel = modelMap[modelId];
      return bakedModel != null ? bakedModel : errorSaplingModel;
   }

   public BakedModelSapling(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      try {
         IModel model = ModelLoaderRegistry.getModel(new ResourceLocation("dynamictrees", "block/saplings/error"));
         if (model != null) {
            errorSaplingModel = model.bake(state, format, bakedTextureGetter);
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      particleTexture = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(new ResourceLocation("blocks/dirt"));
      modelMap = new IBakedModel[Species.REGISTRY.getEntries().size()];
      int modelId = 0;

      for(Entry<ResourceLocation, Species> entry : Species.REGISTRY.getEntries()) {
         Species species = (Species)entry.getValue();
         ResourceLocation resLoc = species.getSaplingName();
         species.saplingModelId = modelId;
         if (species != Species.NULLSPECIES) {
            try {
               IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(resLoc.getResourceDomain(), "block/saplings/" + resLoc.getResourcePath()));
               if (model != null) {
                  modelMap[modelId] = model.bake(state, format, bakedTextureGetter);
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }

         ++modelId;
      }

   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> quads = new ArrayList(12);
      if (state != null && state.getBlock() instanceof BlockDynamicSapling && state instanceof IExtendedBlockState) {
         Species species = (Species)((IExtendedBlockState)state).getValue(SpeciesProperty.SPECIES);
         if (species != null) {
            quads.addAll(getModelForSapling(species).getQuads(ModBlocks.blockDynamicSapling.getDefaultState(), side, rand));
         }
      }

      return quads;
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return particleTexture;
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
