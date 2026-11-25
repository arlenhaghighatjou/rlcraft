package com.creativemd.creativecore.client.rendering;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.box.CubeObject;
import com.creativemd.creativecore.common.utils.mc.BlockUtils;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCubeObject extends CubeObject {
   public Block block;
   public int meta = 0;
   public int color = -1;
   public Object customData = null;
   public boolean keepVU = false;
   public boolean allowOverlap = false;
   private RenderCubeObject.EnumSideRender renderEast = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private RenderCubeObject.EnumSideRender renderWest = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private RenderCubeObject.EnumSideRender renderUp = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private RenderCubeObject.EnumSideRender renderDown = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private RenderCubeObject.EnumSideRender renderSouth = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private RenderCubeObject.EnumSideRender renderNorth = RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
   private Object quadEast = null;
   private Object quadWest = null;
   private Object quadUp = null;
   private Object quadDown = null;
   private Object quadSouth = null;
   private Object quadNorth = null;
   public boolean doesNeedQuadUpdate = true;
   public boolean isEmissive = false;

   public void setQuad(EnumFacing facing, List<BakedQuad> quads) {
      Object quad = quads != null && !quads.isEmpty() ? (quads.size() == 1 ? quads.get(0) : quads) : null;
      switch(facing) {
      case DOWN:
         this.quadDown = quad;
         break;
      case EAST:
         this.quadEast = quad;
         break;
      case NORTH:
         this.quadNorth = quad;
         break;
      case SOUTH:
         this.quadSouth = quad;
         break;
      case UP:
         this.quadUp = quad;
         break;
      case WEST:
         this.quadWest = quad;
      }

   }

   public Object getQuad(EnumFacing facing) {
      switch(facing) {
      case DOWN:
         return this.quadDown;
      case EAST:
         return this.quadEast;
      case NORTH:
         return this.quadNorth;
      case SOUTH:
         return this.quadSouth;
      case UP:
         return this.quadUp;
      case WEST:
         return this.quadWest;
      default:
         return null;
      }
   }

   public int getQuads() {
      int quads = 0;
      if (this.quadUp != null) {
         quads += this.quadUp instanceof List ? ((List)this.quadUp).size() : 1;
      }

      if (this.quadDown != null) {
         quads += this.quadDown instanceof List ? ((List)this.quadDown).size() : 1;
      }

      if (this.quadEast != null) {
         quads += this.quadEast instanceof List ? ((List)this.quadEast).size() : 1;
      }

      if (this.quadWest != null) {
         quads += this.quadWest instanceof List ? ((List)this.quadWest).size() : 1;
      }

      if (this.quadSouth != null) {
         quads += this.quadSouth instanceof List ? ((List)this.quadSouth).size() : 1;
      }

      if (this.quadNorth != null) {
         quads += this.quadNorth instanceof List ? ((List)this.quadNorth).size() : 1;
      }

      return quads;
   }

   public RenderCubeObject(CubeObject cube, RenderCubeObject cube2) {
      super(cube);
      this.applyExtraCubeData(cube2);
   }

   public RenderCubeObject(CubeObject cube, Block block, int meta) {
      super(cube);
      this.block = block;
      this.meta = meta;
   }

   public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block) {
      super(minX, minY, minZ, maxX, maxY, maxZ);
      this.block = block;
   }

   public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block, int meta) {
      super(minX, minY, minZ, maxX, maxY, maxZ);
      this.block = block;
      this.meta = meta;
   }

   protected void applyExtraCubeData(CubeObject cube) {
      if (cube instanceof RenderCubeObject) {
         this.block = ((RenderCubeObject)cube).block;
         this.meta = ((RenderCubeObject)cube).meta;
         this.color = ((RenderCubeObject)cube).color;
         this.renderEast = ((RenderCubeObject)cube).renderEast;
         this.renderWest = ((RenderCubeObject)cube).renderWest;
         this.renderUp = ((RenderCubeObject)cube).renderUp;
         this.renderDown = ((RenderCubeObject)cube).renderDown;
         this.renderSouth = ((RenderCubeObject)cube).renderSouth;
         this.renderNorth = ((RenderCubeObject)cube).renderNorth;
      }

   }

   public RenderCubeObject setColor(Vec3i color) {
      this.setColor(ColorUtils.RGBToInt(color));
      return this;
   }

   public RenderCubeObject setColor(int color) {
      this.color = color;
      return this;
   }

   public RenderCubeObject setKeepUV(boolean keep) {
      this.keepVU = keep;
      return this;
   }

   public IBlockState getModelState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.block.getExtendedState(state, world, pos);
   }

   public IBlockState getBlockState() {
      return this.meta != -1 ? BlockUtils.getState(this.block, this.meta) : this.block.getDefaultState();
   }

   public IBlockState getBlockState(Block block) {
      return this.meta != -1 ? BlockUtils.getState(block, this.meta) : block.getDefaultState();
   }

   public void setSideRender(EnumFacing facing, RenderCubeObject.EnumSideRender renderer) {
      switch(facing) {
      case DOWN:
         this.renderDown = renderer;
         break;
      case EAST:
         this.renderEast = renderer;
         break;
      case NORTH:
         this.renderNorth = renderer;
         break;
      case SOUTH:
         this.renderSouth = renderer;
         break;
      case UP:
         this.renderUp = renderer;
         break;
      case WEST:
         this.renderWest = renderer;
      }

   }

   public RenderCubeObject.EnumSideRender getSidedRendererType(EnumFacing facing) {
      switch(facing) {
      case DOWN:
         return this.renderDown;
      case EAST:
         return this.renderEast;
      case NORTH:
         return this.renderNorth;
      case SOUTH:
         return this.renderSouth;
      case UP:
         return this.renderUp;
      case WEST:
         return this.renderWest;
      default:
         return RenderCubeObject.EnumSideRender.INSIDE_RENDERED;
      }
   }

   public boolean shouldSideBeRendered(EnumFacing facing) {
      switch(facing) {
      case DOWN:
         return this.renderDown.shouldBeRendered;
      case EAST:
         return this.renderEast.shouldBeRendered;
      case NORTH:
         return this.renderNorth.shouldBeRendered;
      case SOUTH:
         return this.renderSouth.shouldBeRendered;
      case UP:
         return this.renderUp.shouldBeRendered;
      case WEST:
         return this.renderWest.shouldBeRendered;
      default:
         return true;
      }
   }

   public boolean intersectsWithFace(EnumFacing facing, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, BlockPos offset) {
      switch(facing.getAxis()) {
      case X:
         return maxY > this.minY - (float)offset.getY() && minY < this.maxY - (float)offset.getY() && maxZ > this.minZ - (float)offset.getZ() && minZ < this.maxZ - (float)offset.getZ();
      case Y:
         return maxX > this.minX - (float)offset.getX() && minX < this.maxX - (float)offset.getX() && maxZ > this.minZ - (float)offset.getZ() && minZ < this.maxZ - (float)offset.getZ();
      case Z:
         return maxX > this.minX - (float)offset.getX() && minX < this.maxX - (float)offset.getX() && maxY > this.minY - (float)offset.getY() && minY < this.maxY - (float)offset.getY();
      default:
         return false;
      }
   }

   protected List<BakedQuad> getBakedQuad(IBlockAccess world, IBakedModel blockModel, IBlockState state, EnumFacing facing, BlockPos pos, BlockRenderLayer layer, long rand) {
      return OptifineHelper.getRenderQuads(blockModel.getQuads(state, facing, rand), world, state, pos, facing, layer, rand);
   }

   public List<BakedQuad> getBakedQuad(IBlockAccess world, @Nullable BlockPos pos, BlockPos offset, IBlockState state, IBakedModel blockModel, EnumFacing facing, BlockRenderLayer layer, long rand, boolean overrideTint, int defaultColor) {
      List<BakedQuad> blockQuads = this.getBakedQuad(world, blockModel, state, facing, pos, layer, rand);
      if (blockQuads.isEmpty()) {
         return Collections.emptyList();
      } else {
         int color = this.color != -1 ? this.color : defaultColor;
         List<BakedQuad> quads = new ArrayList();

         for(int i = 0; i < blockQuads.size(); ++i) {
            BakedQuad oldQuad = (BakedQuad)blockQuads.get(i);
            if (!this.isEmissive && OptifineHelper.isEmissive(oldQuad.getSprite())) {
               this.isEmissive = true;
            }

            int index = 0;
            int uvIndex = index + oldQuad.getFormat().getUvOffsetById(0) / 4;
            float tempMinX = Float.intBitsToFloat(oldQuad.getVertexData()[index]);
            float tempMinY = Float.intBitsToFloat(oldQuad.getVertexData()[index + 1]);
            float tempMinZ = Float.intBitsToFloat(oldQuad.getVertexData()[index + 2]);
            float tempU = Float.intBitsToFloat(oldQuad.getVertexData()[uvIndex]);
            Float.intBitsToFloat(oldQuad.getVertexData()[uvIndex + 1]);
            boolean uvInverted = false;
            index = 1 * oldQuad.getFormat().getIntegerSize();
            uvIndex = index + oldQuad.getFormat().getUvOffsetById(0) / 4;
            if (tempMinX != Float.intBitsToFloat(oldQuad.getVertexData()[index])) {
               if (tempU != Float.intBitsToFloat(oldQuad.getVertexData()[uvIndex])) {
                  uvInverted = Axis.X != RotationUtils.getUAxisFromFacing(facing);
               } else {
                  uvInverted = Axis.X != RotationUtils.getVAxisFromFacing(facing);
               }
            } else if (tempMinY != Float.intBitsToFloat(oldQuad.getVertexData()[index + 1])) {
               if (tempU != Float.intBitsToFloat(oldQuad.getVertexData()[uvIndex])) {
                  uvInverted = Axis.Y != RotationUtils.getUAxisFromFacing(facing);
               } else {
                  uvInverted = Axis.Y != RotationUtils.getVAxisFromFacing(facing);
               }
            } else if (tempU != Float.intBitsToFloat(oldQuad.getVertexData()[uvIndex])) {
               uvInverted = Axis.Z != RotationUtils.getUAxisFromFacing(facing);
            } else {
               uvInverted = Axis.Z != RotationUtils.getVAxisFromFacing(facing);
            }

            index = 2 * oldQuad.getFormat().getIntegerSize();
            float tempMaxX = Float.intBitsToFloat(oldQuad.getVertexData()[index]);
            float tempMaxY = Float.intBitsToFloat(oldQuad.getVertexData()[index + 1]);
            float tempMaxZ = Float.intBitsToFloat(oldQuad.getVertexData()[index + 2]);
            float minX = Math.min(tempMinX, tempMaxX);
            float minY = Math.min(tempMinY, tempMaxY);
            float minZ = Math.min(tempMinZ, tempMaxZ);
            float maxX = Math.max(tempMinX, tempMaxX);
            float maxY = Math.max(tempMinY, tempMaxY);
            float maxZ = Math.max(tempMinZ, tempMaxZ);
            if (this.intersectsWithFace(facing, minX, minY, minZ, maxX, maxY, maxZ, offset)) {
               float sizeX = maxX - minX;
               float sizeY = maxY - minY;
               float sizeZ = maxZ - minZ;
               BakedQuad quad = new CreativeBakedQuad((BakedQuad)blockQuads.get(i), this, color, overrideTint && (defaultColor == -1 || ((BakedQuad)blockQuads.get(i)).hasTintIndex()) && color != -1, facing);
               uvIndex = quad.getFormat().getUvOffsetById(0) / 4;
               float u1 = Float.intBitsToFloat(quad.getVertexData()[uvIndex]);
               float v1 = Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]);
               uvIndex = 2 * quad.getFormat().getIntegerSize() + quad.getFormat().getUvOffsetById(0) / 4;
               float u2 = Float.intBitsToFloat(quad.getVertexData()[uvIndex]);
               float v2 = Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]);
               float sizeU;
               float sizeV;
               if (uvInverted) {
                  sizeU = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                  sizeV = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
               } else {
                  sizeU = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                  sizeV = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
               }

               EnumFaceDirection direction = EnumFaceDirection.getFacing(facing);

               for(int k = 0; k < 4; ++k) {
                  VertexInformation vertex = direction.getVertexInformation(k);
                  index = k * quad.getFormat().getIntegerSize();
                  float x = facing.getAxis() != Axis.X && !this.allowOverlap ? MathHelper.clamp(this.getVertexInformationPosition(vertex.xIndex) - (float)offset.getX(), minX, maxX) : this.getVertexInformationPosition(vertex.xIndex) - (float)offset.getX();
                  float y = facing.getAxis() != Axis.Y && !this.allowOverlap ? MathHelper.clamp(this.getVertexInformationPosition(vertex.yIndex) - (float)offset.getY(), minY, maxY) : this.getVertexInformationPosition(vertex.yIndex) - (float)offset.getY();
                  float z = facing.getAxis() != Axis.Z && !this.allowOverlap ? MathHelper.clamp(this.getVertexInformationPosition(vertex.zIndex) - (float)offset.getZ(), minZ, maxZ) : this.getVertexInformationPosition(vertex.zIndex) - (float)offset.getZ();
                  float oldX = Float.intBitsToFloat(quad.getVertexData()[index]);
                  float oldY = Float.intBitsToFloat(quad.getVertexData()[index + 1]);
                  float oldZ = Float.intBitsToFloat(quad.getVertexData()[index + 2]);
                  quad.getVertexData()[index] = Float.floatToIntBits(x + (float)offset.getX());
                  quad.getVertexData()[index + 1] = Float.floatToIntBits(y + (float)offset.getY());
                  quad.getVertexData()[index + 2] = Float.floatToIntBits(z + (float)offset.getZ());
                  if (!this.keepVU) {
                     uvIndex = index + quad.getFormat().getUvOffsetById(0) / 4;
                     float uOffset;
                     float vOffset;
                     if (uvInverted) {
                        uOffset = (RotationUtils.getVFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(facing, x, y, z)) / RotationUtils.getVFromFacing(facing, sizeX, sizeY, sizeZ) * sizeU;
                        vOffset = (RotationUtils.getUFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(facing, x, y, z)) / RotationUtils.getUFromFacing(facing, sizeX, sizeY, sizeZ) * sizeV;
                     } else {
                        uOffset = (RotationUtils.getUFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(facing, x, y, z)) / RotationUtils.getUFromFacing(facing, sizeX, sizeY, sizeZ) * sizeU;
                        vOffset = (RotationUtils.getVFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(facing, x, y, z)) / RotationUtils.getVFromFacing(facing, sizeX, sizeY, sizeZ) * sizeV;
                     }

                     quad.getVertexData()[uvIndex] = Float.floatToRawIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex]) - uOffset);
                     quad.getVertexData()[uvIndex + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]) - vOffset);
                  }
               }

               quads.add(quad);
            }
         }

         return quads;
      }
   }

   public void deleteQuadCache() {
      this.doesNeedQuadUpdate = true;
      this.quadEast = null;
      this.quadWest = null;
      this.quadUp = null;
      this.quadDown = null;
      this.quadSouth = null;
      this.quadNorth = null;
   }

   public static enum EnumSideRender {
      INSIDE_RENDERED(true, false),
      INSIDE_NOT_RENDERED(false, false),
      OUTSIDE_RENDERED(true, true),
      OUTSIDE_NOT_RENDERD(false, true);

      public final boolean shouldBeRendered;
      public final boolean outside;

      private EnumSideRender(boolean shouldBeRendered, boolean outside) {
         this.shouldBeRendered = shouldBeRendered;
         this.outside = outside;
      }
   }
}
