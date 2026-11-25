package com.ferreusveritas.dynamictrees.models;

import com.ferreusveritas.dynamictrees.blocks.BlockRootyWater;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.Loader;

public class ModelRootyWater implements IBakedModel {
   protected IBakedModel rootsModel;
   protected EaglerTextureAtlasSprite stillWaterTexture;
   protected EaglerTextureAtlasSprite flowWaterTexture;

   public ModelRootyWater(IBakedModel rootsModel) {
      this.rootsModel = rootsModel;
      String textureProvider = Loader.isModLoaded("aquaacrobatics") ? "aquaacrobatics" : "minecraft";
      this.stillWaterTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureProvider + ":blocks/water_still");
      this.flowWaterTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureProvider + ":blocks/water_flow");
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> quads = new ArrayList();
      if (side != null) {
         return quads;
      } else {
         if (state instanceof IExtendedBlockState) {
            BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
            if (renderLayer == BlockRenderLayer.CUTOUT_MIPPED) {
               quads.addAll(this.rootsModel.getQuads(state, side, rand));
            }

            if (renderLayer == BlockRenderLayer.TRANSLUCENT) {
               IExtendedBlockState extState = (IExtendedBlockState)state;
               float yOffset = 0.001F;
               float y0 = ((Float)extState.getValue(BlockRootyWater.CORNER_HEIGHTS[0])).floatValue() - yOffset;
               float y1 = ((Float)extState.getValue(BlockRootyWater.CORNER_HEIGHTS[1])).floatValue() - yOffset;
               float y2 = ((Float)extState.getValue(BlockRootyWater.CORNER_HEIGHTS[2])).floatValue() - yOffset;
               float y3 = ((Float)extState.getValue(BlockRootyWater.CORNER_HEIGHTS[3])).floatValue() - yOffset;
               EaglerTextureAtlasSprite textureatlassprite = this.stillWaterTexture;
               int[] colors = new int[6];

               for(EnumFacing facing : EnumFacing.values()) {
                  int v = (int)(255.0F * LightUtil.diffuseLight(facing));
                  colors[facing.ordinal()] = -16777216 | v << 16 | v << 8 | v;
               }

               float min = 0.0F;
               float max = 16.0F;
               float p0 = 1.0E-4F;
               float p1 = 0.9999F;
               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[0])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(0.0F, 0.0F, 1.0F, colors[0], textureatlassprite, min, max), this.vertexToInts(0.0F, 0.0F, 0.0F, colors[0], textureatlassprite, min, min), this.vertexToInts(1.0F, 0.0F, 0.0F, colors[0], textureatlassprite, max, min), this.vertexToInts(1.0F, 0.0F, 1.0F, colors[0], textureatlassprite, max, max)}), 0, EnumFacing.DOWN, textureatlassprite, false, DefaultVertexFormats.BLOCK));
               }

               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[1])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(0.0F, y0, 0.0F, colors[1], textureatlassprite, min, min), this.vertexToInts(0.0F, y1, 1.0F, colors[1], textureatlassprite, min, max), this.vertexToInts(1.0F, y2, 1.0F, colors[1], textureatlassprite, max, max), this.vertexToInts(1.0F, y3, 0.0F, colors[1], textureatlassprite, max, min)}), 0, EnumFacing.UP, textureatlassprite, false, DefaultVertexFormats.BLOCK));
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(0.0F, y0, 0.0F, colors[1], textureatlassprite, min, min), this.vertexToInts(1.0F, y3, 0.0F, colors[1], textureatlassprite, max, min), this.vertexToInts(1.0F, y2, 1.0F, colors[1], textureatlassprite, max, max), this.vertexToInts(0.0F, y1, 1.0F, colors[1], textureatlassprite, min, max)}), 0, EnumFacing.DOWN, textureatlassprite, false, DefaultVertexFormats.BLOCK));
               }

               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[2])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p1, y3, p0, colors[2], this.flowWaterTexture, 8.0F, (1.0F - y3) * 8.0F), this.vertexToInts(p1, 0.0F, p0, colors[2], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p0, 0.0F, p0, colors[2], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p0, y0, p0, colors[2], this.flowWaterTexture, 0.0F, (1.0F - y0) * 8.0F)}), 0, EnumFacing.NORTH, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p0, y3, p0, colors[2], this.flowWaterTexture, 8.0F, (1.0F - y3) * 8.0F), this.vertexToInts(p0, 0.0F, p0, colors[2], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p1, 0.0F, p0, colors[2], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p1, y0, p0, colors[2], this.flowWaterTexture, 0.0F, (1.0F - y0) * 8.0F)}), 0, EnumFacing.SOUTH, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
               }

               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[3])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p0, y1, p1, colors[3], this.flowWaterTexture, 8.0F, (1.0F - y1) * 8.0F), this.vertexToInts(p0, 0.0F, p1, colors[3], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p1, 0.0F, p1, colors[3], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p1, y2, p1, colors[3], this.flowWaterTexture, 0.0F, (1.0F - y2) * 8.0F)}), 0, EnumFacing.SOUTH, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p1, y1, p1, colors[3], this.flowWaterTexture, 8.0F, (1.0F - y1) * 8.0F), this.vertexToInts(p1, 0.0F, p1, colors[3], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p0, 0.0F, p1, colors[3], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p0, y2, p1, colors[3], this.flowWaterTexture, 0.0F, (1.0F - y2) * 8.0F)}), 0, EnumFacing.NORTH, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
               }

               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[4])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p0, y0, p0, colors[4], this.flowWaterTexture, 8.0F, (1.0F - y0) * 8.0F), this.vertexToInts(p0, 0.0F, p0, colors[4], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p0, 0.0F, p1, colors[4], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p0, y1, p1, colors[4], this.flowWaterTexture, 0.0F, (1.0F - y1) * 8.0F)}), 0, EnumFacing.WEST, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p0, y0, p1, colors[4], this.flowWaterTexture, 8.0F, (1.0F - y0) * 8.0F), this.vertexToInts(p0, 0.0F, p1, colors[4], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p0, 0.0F, p0, colors[4], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p0, y1, p0, colors[4], this.flowWaterTexture, 0.0F, (1.0F - y1) * 8.0F)}), 0, EnumFacing.EAST, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
               }

               if (((Boolean)extState.getValue(BlockRootyWater.RENDER_SIDES[5])).booleanValue()) {
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p1, y2, p1, colors[5], this.flowWaterTexture, 8.0F, (1.0F - y2) * 8.0F), this.vertexToInts(p1, 0.0F, p1, colors[5], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p1, 0.0F, p0, colors[5], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p1, y3, p0, colors[5], this.flowWaterTexture, 0.0F, (1.0F - y3) * 8.0F)}), 0, EnumFacing.EAST, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
                  quads.add(new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(p1, y2, p0, colors[5], this.flowWaterTexture, 8.0F, (1.0F - y2) * 8.0F), this.vertexToInts(p1, 0.0F, p0, colors[5], this.flowWaterTexture, 8.0F, 8.0F), this.vertexToInts(p1, 0.0F, p1, colors[5], this.flowWaterTexture, 0.0F, 8.0F), this.vertexToInts(p1, y3, p1, colors[5], this.flowWaterTexture, 0.0F, (1.0F - y3) * 8.0F)}), 0, EnumFacing.WEST, this.flowWaterTexture, false, DefaultVertexFormats.BLOCK));
               }
            }
         }

         return quads;
      }
   }

   protected int[] vertexToInts(float x, float y, float z, int color, EaglerTextureAtlasSprite texture, float u, float v) {
      return new int[]{Float.floatToRawIntBits(x), Float.floatToRawIntBits(y), Float.floatToRawIntBits(z), color, Float.floatToRawIntBits(texture.getInterpolatedU((double)u)), Float.floatToRawIntBits(texture.getInterpolatedV((double)v)), 0};
   }

   public boolean isAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.stillWaterTexture;
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
