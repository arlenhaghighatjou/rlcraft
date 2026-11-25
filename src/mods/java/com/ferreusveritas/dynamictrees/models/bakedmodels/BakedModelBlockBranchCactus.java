package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchCactus;
import com.ferreusveritas.dynamictrees.client.ModelUtils;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class BakedModelBlockBranchCactus implements IBakedModel {
   protected ModelBlock modelBlock = new ModelBlock((ResourceLocation)null, (List)null, (Map)null, false, false, ItemCameraTransforms.DEFAULT, (List)null);
   EaglerTextureAtlasSprite barkParticles;
   private final IBakedModel[][] sleeves = new IBakedModel[6][2];
   private final IBakedModel[][] cores = new IBakedModel[3][2];
   private final IBakedModel[] rings = new IBakedModel[2];
   private final IBakedModel[] coreSpikes = new IBakedModel[2];
   private IBakedModel sleeveTopSpikes;

   public BakedModelBlockBranchCactus(ResourceLocation barkRes, ResourceLocation ringsRes, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      EaglerTextureAtlasSprite barkIcon = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(barkRes);
      EaglerTextureAtlasSprite ringIcon = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(ringsRes);
      this.barkParticles = barkIcon;

      for(int i = 0; i < 2; ++i) {
         int radius = i + 4;

         for(EnumFacing dir : EnumFacing.VALUES) {
            this.sleeves[dir.getIndex()][i] = this.bakeSleeve(radius, dir, barkIcon, ringIcon);
         }

         this.cores[0][i] = this.bakeCore(radius, Axis.Y, barkIcon);
         this.cores[1][i] = this.bakeCore(radius, Axis.Z, barkIcon);
         this.cores[2][i] = this.bakeCore(radius, Axis.X, barkIcon);
         this.rings[i] = this.bakeCore(radius, Axis.Y, ringIcon);
         this.coreSpikes[i] = this.bakeCoreSpikes(radius, barkIcon);
         this.sleeveTopSpikes = this.bakeTopSleeveSpikes(barkIcon);
      }

   }

   public IBakedModel bakeSleeve(int radius, EnumFacing dir, EaglerTextureAtlasSprite bark, EaglerTextureAtlasSprite top) {
      int dradius = radius * 2;
      int halfSize = (16 - dradius) / 2;
      int halfSizeX = dir.getFrontOffsetX() != 0 ? halfSize : dradius;
      int halfSizeY = dir.getFrontOffsetY() != 0 ? halfSize : dradius;
      int halfSizeZ = dir.getFrontOffsetZ() != 0 ? halfSize : dradius;
      int move = 16 - halfSize;
      int centerX = 16 + dir.getFrontOffsetX() * move;
      int centerY = 16 + dir.getFrontOffsetY() * move;
      int centerZ = 16 + dir.getFrontOffsetZ() * move;
      Vector3f posFrom = new Vector3f((float)((centerX - halfSizeX) / 2), (float)((centerY - halfSizeY) / 2), (float)((centerZ - halfSizeZ) / 2));
      Vector3f posTo = new Vector3f((float)((centerX + halfSizeX) / 2), (float)((centerY + halfSizeY) / 2), (float)((centerZ + halfSizeZ) / 2));
      boolean negative = dir.getAxisDirection() == AxisDirection.NEGATIVE;
      if (dir.getAxis() == Axis.Z) {
         negative = !negative;
      }

      Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);

      for(EnumFacing face : EnumFacing.VALUES) {
         if (dir.getOpposite() != face) {
            BlockFaceUV uvface = null;
            if (dir == face) {
               if (radius == 4 || radius == 5 && dir == EnumFacing.DOWN) {
                  uvface = new BlockFaceUV(new float[]{(float)(8 - radius), (float)(8 - radius), (float)(8 + radius), (float)(8 + radius)}, 0);
               }
            } else {
               uvface = new BlockFaceUV(new float[]{(float)(8 - radius), negative ? (float)(16 - halfSize) : 0.0F, (float)(8 + radius), negative ? 16.0F : (float)halfSize}, this.getFaceAngle(dir.getAxis(), face));
            }

            if (uvface != null) {
               mapFacesIn.put(face, new BlockPartFace((EnumFacing)null, -1, (String)null, uvface));
            }
         }
      }

      BlockPart part = new BlockPart(posFrom, posTo, mapFacesIn, (BlockPartRotation)null, true);
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(bark);

      for(Entry<EnumFacing, BlockPartFace> e : part.mapFaces.entrySet()) {
         EnumFacing face = (EnumFacing)e.getKey();
         builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)e.getValue(), dir == face ? top : bark, face, ModelRotation.X0_Y0, false));
      }

      float minV = negative ? (float)(16 - halfSize) : 0.0F;
      float maxV = negative ? 16.0F : (float)halfSize;
      switch(dir.getAxis()) {
      case X:
         builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 2.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, minV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 2.0F, minV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
         break;
      case Y:
         builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 16.0F, minV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 2.0F, minV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
         break;
      case Z:
         builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 2.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 16.0F, minV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 2.0F, minV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
         builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeCore(int radius, Axis axis, EaglerTextureAtlasSprite icon) {
      Vector3f posFrom = new Vector3f((float)(8 - radius), (float)(8 - radius), (float)(8 - radius));
      Vector3f posTo = new Vector3f((float)(8 + radius), (float)(8 + radius), (float)(8 + radius));
      Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);

      for(EnumFacing face : EnumFacing.VALUES) {
         BlockFaceUV uvface = new BlockFaceUV(new float[]{(float)(8 - radius), (float)(8 - radius), (float)(8 + radius), (float)(8 + radius)}, this.getFaceAngle(axis, face));
         mapFacesIn.put(face, new BlockPartFace((EnumFacing)null, -1, (String)null, uvface));
      }

      BlockPart part = new BlockPart(posFrom, posTo, mapFacesIn, (BlockPartRotation)null, true);
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(icon);

      for(Entry<EnumFacing, BlockPartFace> e : part.mapFaces.entrySet()) {
         EnumFacing face = (EnumFacing)e.getKey();
         builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)e.getValue(), icon, face, ModelRotation.X0_Y0, false));
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeCoreSpikes(int radius, EaglerTextureAtlasSprite bark) {
      float minV = (float)(8 - radius);
      float maxV = (float)(8 + radius);
      Vector3f posFrom = new Vector3f((float)(8 - radius), (float)(8 - radius), (float)(8 - radius));
      Vector3f posTo = new Vector3f((float)(8 + radius), (float)(8 + radius), (float)(8 + radius));
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(bark);
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 2.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, minV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 2.0F, minV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posFrom.y / 16.0F - 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.SOUTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.NORTH, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.001F, posTo.y / 16.0F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 16.0F, minV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F, posFrom.z / 16.0F - 0.001F, -1, bark, 2.0F, minV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F, posTo.z / 16.0F + 0.001F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.DOWN, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posFrom.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 2.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 16.0F, minV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 2.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posFrom.y / 16.0F - 0.001F, posFrom.z / 16.0F, -1, bark, 2.0F, minV)}), 0, EnumFacing.DOWN, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.EAST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.WEST, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      return builder.makeBakedModel();
   }

   public IBakedModel bakeTopSleeveSpikes(EaglerTextureAtlasSprite bark) {
      float minV = 4.0F;
      float maxV = 12.0F;
      Vector3f posFrom = new Vector3f(4.0F, 16.0F, 4.0F);
      Vector3f posTo = new Vector3f(12.0F, 16.0F, 12.0F);
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(bark);
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F - 0.002F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.NORTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F + 0.002F, -1, bark, 16.0F, minV)}), 0, EnumFacing.SOUTH, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F + 0.0625F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posTo.z / 16.0F - 0.0625F, -1, bark, 14.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, maxV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F + 0.0625F, -1, bark, 2.0F, minV), this.vertexToInts(posTo.x / 16.0F, posTo.y / 16.0F + 0.002F, posFrom.z / 16.0F - 0.0625F, -1, bark, 0.0F, minV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posFrom.x / 16.0F - 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.WEST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posTo.z / 16.0F, -1, bark, 16.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F - 0.0625F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F + 0.002F, posTo.y / 16.0F + 0.0625F, posFrom.z / 16.0F, -1, bark, 16.0F, minV)}), 0, EnumFacing.EAST, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 16.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 14.0F, minV), this.vertexToInts(posTo.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 14.0F, maxV), this.vertexToInts(posTo.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 16.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      builder.addFaceQuad(EnumFacing.UP, new BakedQuad(Ints.concat(new int[][]{this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 2.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posFrom.z / 16.0F, -1, bark, 0.0F, minV), this.vertexToInts(posFrom.x / 16.0F - 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 0.0F, maxV), this.vertexToInts(posFrom.x / 16.0F + 0.0625F, posTo.y / 16.0F + 0.001F, posTo.z / 16.0F, -1, bark, 2.0F, maxV)}), 0, EnumFacing.UP, bark, true, DefaultVertexFormats.BLOCK));
      return builder.makeBakedModel();
   }

   public int getFaceAngle(Axis axis, EnumFacing face) {
      if (axis == Axis.Y) {
         return 0;
      } else if (axis == Axis.Z) {
         switch(face) {
         case UP:
            return 0;
         case WEST:
            return 270;
         case DOWN:
            return 180;
         default:
            return 90;
         }
      } else {
         return face == EnumFacing.NORTH ? 270 : 90;
      }
   }

   protected int[] vertexToInts(float x, float y, float z, int color, EaglerTextureAtlasSprite texture, float u, float v) {
      return new int[]{Float.floatToRawIntBits(x), Float.floatToRawIntBits(y), Float.floatToRawIntBits(z), color, Float.floatToRawIntBits(texture.getInterpolatedU((double)u)), Float.floatToRawIntBits(texture.getInterpolatedV((double)v)), 0};
   }

   public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
      List<BakedQuad> quadsList = new ArrayList(12);
      IExtendedBlockState extendedBlockState = (IExtendedBlockState)blockState;
      if (blockState instanceof IExtendedBlockState) {
         int coreRadius = this.getRadius(blockState);
         int[] connections = this.pollConnections(coreRadius, extendedBlockState);
         int numConnections = 0;

         for(int i : connections) {
            numConnections += i != 0 ? 1 : 0;
         }

         boolean extraUpSleeve = false;
         if (coreRadius == 4 && numConnections == 1 && ((EnumFacing)blockState.getValue(BlockBranchCactus.ORIGIN)).getAxis().isHorizontal()) {
            connections[1] = 4;
            extraUpSleeve = true;
         }

         EnumFacing sourceDir = this.getSourceDir(coreRadius, connections);
         if (sourceDir == null) {
            sourceDir = EnumFacing.DOWN;
         }

         int coreDir = this.resolveCoreDir(sourceDir);
         EnumFacing coreRingDir = numConnections == 1 ? sourceDir.getOpposite() : null;
         if (side == null || coreRadius != connections[side.getIndex()]) {
            if (coreRingDir != null && coreRingDir == side) {
               quadsList.addAll(this.rings[coreRadius - 4].getQuads(blockState, side, rand));
            } else {
               quadsList.addAll(this.cores[coreDir][coreRadius - 4].getQuads(blockState, side, rand));
            }
         }

         for(EnumFacing dir : EnumFacing.values()) {
            if (coreRadius > connections[dir.getIndex()]) {
               for(BakedQuad quad : this.coreSpikes[coreRadius - 4].getQuads(blockState, dir, rand)) {
                  if (coreRadius > connections[quad.getFace().getIndex()]) {
                     quadsList.add(quad);
                  }
               }
            }
         }

         for(EnumFacing connDir : EnumFacing.VALUES) {
            int idx = connDir.getIndex();
            int connRadius = connections[idx];
            if (connRadius >= 4 && (connDir == EnumFacing.UP && connRadius == 4 && extraUpSleeve || side != connDir || connDir == EnumFacing.DOWN)) {
               quadsList.addAll(this.sleeves[idx][connRadius - 4].getQuads(extendedBlockState, side, rand));
            }
         }

         if (extraUpSleeve) {
            quadsList.addAll(this.sleeveTopSpikes.getQuads(extendedBlockState, EnumFacing.UP, rand));
         }
      }

      return quadsList;
   }

   protected int[] pollConnections(int coreRadius, IExtendedBlockState extendedBlockState) {
      int[] connections = new int[6];

      for(EnumFacing dir : EnumFacing.VALUES) {
         int connection = this.getConnectionRadius(extendedBlockState, BlockBranch.CONNECTIONS[dir.getIndex()]);
         connections[dir.getIndex()] = MathHelper.clamp(connection, 0, coreRadius);
      }

      return connections;
   }

   protected EnumFacing getSourceDir(int coreRadius, int[] connections) {
      int largestConnection = 0;
      EnumFacing sourceDir = null;

      for(EnumFacing dir : EnumFacing.VALUES) {
         int connRadius = connections[dir.getIndex()];
         if (connRadius > largestConnection) {
            largestConnection = connRadius;
            sourceDir = dir;
         }
      }

      if (largestConnection < coreRadius) {
         sourceDir = null;
      }

      return sourceDir;
   }

   protected int resolveCoreDir(EnumFacing dir) {
      return dir.getIndex() >> 1;
   }

   protected int getRadius(IBlockState blockState) {
      return ((BlockBranchCactus)blockState.getBlock()).getRadius(blockState);
   }

   protected int getConnectionRadius(IExtendedBlockState iExtendedBlockState, IUnlistedProperty<Integer> whichConnection) {
      Integer connection = (Integer)iExtendedBlockState.getValue(whichConnection);
      return connection != null ? connection.intValue() : 0;
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return this.barkParticles;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.sleeves[0][0].getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
