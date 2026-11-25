package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.blocks.MimicProperty;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedModelBlockRooty implements IBakedModel {
   protected IBakedModel rootsModel;

   public BakedModelBlockRooty(IBakedModel rootsModel) {
      this.rootsModel = rootsModel;
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> quads = new ArrayList(16);
      if (state != null && state.getBlock() instanceof MimicProperty.IMimic && state instanceof IExtendedBlockState) {
         IExtendedBlockState extendedState = (IExtendedBlockState)state;
         IBlockState mimicState = (IBlockState)extendedState.getValue(MimicProperty.MIMIC);
         Minecraft mc = Minecraft.getMinecraft();
         BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
         BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
         IBakedModel mimicModel = blockModelShapes.getModelForState(mimicState);
         quads.addAll(mimicModel.getQuads(mimicState, side, rand));
         if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT_MIPPED) {
            quads.addAll(this.rootsModel.getQuads(state, side, rand));
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
      return this.rootsModel.getParticleTexture();
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
