package com.creativemd.creativecore.client.rendering;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.face.FaceRenderType;
import com.creativemd.creativecore.client.rendering.face.IFaceRenderType;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.box.AlignedBox;
import com.creativemd.creativecore.common.utils.math.geo.NormalPlane;
import com.creativemd.creativecore.common.utils.math.geo.Ray2d;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import com.creativemd.creativecore.common.utils.mc.BlockUtils;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBox extends AlignedBox {
   private static final VectorFan DOWN = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 0.0F, 1.0F)});
   private static final VectorFan UP = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 0.0F)});
   private static final VectorFan NORTH = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F)});
   private static final VectorFan SOUTH = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F)});
   private static final VectorFan WEST = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(0.0F, 1.0F, 1.0F)});
   private static final VectorFan EAST = new RenderBox.VectorFanSimple(new Vector3f[]{new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F)});
   public Block block;
   public int meta = 0;
   public int color = -1;
   public boolean keepVU = false;
   public boolean allowOverlap = false;
   public boolean doesNeedQuadUpdate = true;
   public boolean needsResorting = false;
   public boolean emissive = false;
   private IFaceRenderType renderEast = FaceRenderType.INSIDE_RENDERED;
   private IFaceRenderType renderWest = FaceRenderType.INSIDE_RENDERED;
   private IFaceRenderType renderUp = FaceRenderType.INSIDE_RENDERED;
   private IFaceRenderType renderDown = FaceRenderType.INSIDE_RENDERED;
   private IFaceRenderType renderSouth = FaceRenderType.INSIDE_RENDERED;
   private IFaceRenderType renderNorth = FaceRenderType.INSIDE_RENDERED;
   private Object quadEast = null;
   private Object quadWest = null;
   private Object quadUp = null;
   private Object quadDown = null;
   private Object quadSouth = null;
   private Object quadNorth = null;
   public Object customData;

   public RenderBox(AlignedBox cube, RenderBox box) {
      super(cube);
      this.block = box.block;
      this.meta = box.meta;
      this.color = box.color;
      this.renderEast = box.renderEast;
      this.renderWest = box.renderWest;
      this.renderUp = box.renderUp;
      this.renderDown = box.renderDown;
      this.renderSouth = box.renderSouth;
      this.renderNorth = box.renderNorth;
   }

   public RenderBox(AlignedBox cube, Block block, int meta) {
      super(cube);
      this.block = block;
      this.meta = meta;
   }

   public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block) {
      super(minX, minY, minZ, maxX, maxY, maxZ);
      this.block = block;
   }

   public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block, int meta) {
      super(minX, minY, minZ, maxX, maxY, maxZ);
      this.block = block;
      this.meta = meta;
   }

   public RenderBox setColor(int color) {
      this.color = color;
      return this;
   }

   public RenderBox setKeepUV(boolean keep) {
      this.keepVU = keep;
      return this;
   }

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

   public int countQuads() {
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

   public IBlockState getModelState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.block.getExtendedState(state, world, pos);
   }

   public IBlockState getBlockState() {
      return this.meta != -1 ? BlockUtils.getState(this.block, this.meta) : this.block.getDefaultState();
   }

   public IBlockState getBlockState(Block block) {
      return this.meta != -1 ? BlockUtils.getState(block, this.meta) : block.getDefaultState();
   }

   public void setType(EnumFacing facing, IFaceRenderType renderer) {
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

   public IFaceRenderType getType(EnumFacing facing) {
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
         return FaceRenderType.INSIDE_RENDERED;
      }
   }

   public boolean renderSide(EnumFacing facing) {
      switch(facing) {
      case DOWN:
         return this.renderDown.shouldRender();
      case EAST:
         return this.renderEast.shouldRender();
      case NORTH:
         return this.renderNorth.shouldRender();
      case SOUTH:
         return this.renderSouth.shouldRender();
      case UP:
         return this.renderUp.shouldRender();
      case WEST:
         return this.renderWest.shouldRender();
      default:
         return true;
      }
   }

   public boolean intersectsWithFace(EnumFacing facing, RenderBox.RenderInformationHolder holder, BlockPos offset) {
      switch(facing.getAxis()) {
      case X:
         return holder.maxY > this.minY - (float)offset.getY() && holder.minY < this.maxY - (float)offset.getY() && holder.maxZ > this.minZ - (float)offset.getZ() && holder.minZ < this.maxZ - (float)offset.getZ();
      case Y:
         return holder.maxX > this.minX - (float)offset.getX() && holder.minX < this.maxX - (float)offset.getX() && holder.maxZ > this.minZ - (float)offset.getZ() && holder.minZ < this.maxZ - (float)offset.getZ();
      case Z:
         return holder.maxX > this.minX - (float)offset.getX() && holder.minX < this.maxX - (float)offset.getX() && holder.maxY > this.minY - (float)offset.getY() && holder.minY < this.maxY - (float)offset.getY();
      default:
         return false;
      }
   }

   protected Object getRenderQuads(EnumFacing facing) {
      if (this.getType(facing).hasCachedFans()) {
         return this.getType(facing).getCachedFans();
      } else {
         switch(facing) {
         case DOWN:
            return DOWN;
         case EAST:
            return EAST;
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case UP:
            return UP;
         case WEST:
            return WEST;
         default:
            return null;
         }
      }
   }

   protected List<BakedQuad> getBakedQuad(IBlockAccess world, IBakedModel blockModel, IBlockState state, EnumFacing facing, BlockPos pos, BlockRenderLayer layer, long rand) {
      return OptifineHelper.getRenderQuads(blockModel.getQuads(state, facing, rand), world, state, pos, facing, layer, rand);
   }

   protected float getOffsetX() {
      return this.minX;
   }

   protected float getOffsetY() {
      return this.minY;
   }

   protected float getOffsetZ() {
      return this.minZ;
   }

   protected float getOverallScale(EnumFacing facing) {
      return this.getType(facing).getScale();
   }

   protected float getScaleX() {
      return this.maxX - this.minX;
   }

   protected float getScaleY() {
      return this.maxY - this.minY;
   }

   protected float getScaleZ() {
      return this.maxZ - this.minZ;
   }

   protected boolean scaleAndOffsetQuads(EnumFacing facing) {
      return true;
   }

   protected boolean onlyScaleOnceNoOffset(EnumFacing facing) {
      return this.getType(facing).hasCachedFans();
   }

   public List<BakedQuad> getBakedQuad(IBlockAccess world, @Nullable BlockPos pos, BlockPos offset, IBlockState state, IBakedModel blockModel, EnumFacing facing, BlockRenderLayer layer, long rand, boolean overrideTint, int defaultColor) {
      List<BakedQuad> blockQuads = this.getBakedQuad(world, blockModel, state, facing, pos, layer, rand);
      if (blockQuads.isEmpty()) {
         return Collections.emptyList();
      } else {
         RenderBox.RenderInformationHolder holder = new RenderBox.RenderInformationHolder(facing, this.color != -1 ? this.color : defaultColor);
         holder.offset = offset;
         List<BakedQuad> quads = new ArrayList();

         for(int i = 0; i < blockQuads.size(); ++i) {
            holder.setQuad((BakedQuad)blockQuads.get(i), overrideTint, defaultColor);
            if (!this.needsResorting && OptifineHelper.isEmissive(holder.quad.getSprite())) {
               this.needsResorting = true;
            }

            VertexFormat format = holder.quad.getFormat();
            int[] data = holder.quad.getVertexData();
            int index = 0;
            int uvIndex = index + format.getUvOffsetById(0) / 4;
            float tempMinX = Float.intBitsToFloat(data[index]);
            float tempMinY = Float.intBitsToFloat(data[index + 1]);
            float tempMinZ = Float.intBitsToFloat(data[index + 2]);
            float tempU = Float.intBitsToFloat(data[uvIndex]);
            float tempV = Float.intBitsToFloat(data[uvIndex + 1]);
            holder.uvInverted = false;
            index = 1 * format.getIntegerSize();
            uvIndex = index + format.getUvOffsetById(0) / 4;
            if (tempMinX != Float.intBitsToFloat(data[index])) {
               if (tempU != Float.intBitsToFloat(data[uvIndex])) {
                  holder.uvInverted = Axis.X != RotationUtils.getUAxisFromFacing(facing);
               } else {
                  holder.uvInverted = Axis.X != RotationUtils.getVAxisFromFacing(facing);
               }
            } else if (tempMinY != Float.intBitsToFloat(data[index + 1])) {
               if (tempU != Float.intBitsToFloat(data[uvIndex])) {
                  holder.uvInverted = Axis.Y != RotationUtils.getUAxisFromFacing(facing);
               } else {
                  holder.uvInverted = Axis.Y != RotationUtils.getVAxisFromFacing(facing);
               }
            } else if (tempU != Float.intBitsToFloat(data[uvIndex])) {
               holder.uvInverted = Axis.Z != RotationUtils.getUAxisFromFacing(facing);
            } else {
               holder.uvInverted = Axis.Z != RotationUtils.getVAxisFromFacing(facing);
            }

            index = 2 * format.getIntegerSize();
            float tempMaxX = Float.intBitsToFloat(data[index]);
            float tempMaxY = Float.intBitsToFloat(data[index + 1]);
            float tempMaxZ = Float.intBitsToFloat(data[index + 2]);
            holder.setBounds(tempMinX, tempMinY, tempMinZ, tempMaxX, tempMaxY, tempMaxZ);
            if (this.intersectsWithFace(facing, holder, offset)) {
               uvIndex = format.getUvOffsetById(0) / 4;
               float u1 = Float.intBitsToFloat(data[uvIndex]);
               float v1 = Float.intBitsToFloat(data[uvIndex + 1]);
               uvIndex = 2 * format.getIntegerSize() + format.getUvOffsetById(0) / 4;
               float u2 = Float.intBitsToFloat(data[uvIndex]);
               float v2 = Float.intBitsToFloat(data[uvIndex + 1]);
               if (holder.uvInverted) {
                  holder.sizeU = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                  holder.sizeV = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
               } else {
                  holder.sizeU = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                  holder.sizeV = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
               }

               Object renderQuads = this.getRenderQuads(holder.facing);
               if (renderQuads instanceof List) {
                  for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                     ((VectorFan)((List)renderQuads).get(j)).generate(holder, quads);
                  }
               } else if (renderQuads instanceof VectorFan) {
                  ((VectorFan)renderQuads).generate(holder, quads);
               }
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

   protected boolean previewScalingAndOffset() {
      return true;
   }

   public float getPreviewOffX() {
      return this.minX;
   }

   public float getPreviewOffY() {
      return this.minY;
   }

   public float getPreviewOffZ() {
      return this.minZ;
   }

   public float getPreviewScaleX() {
      return this.maxX - this.minX;
   }

   public float getPreviewScaleY() {
      return this.maxY - this.minY;
   }

   public float getPreviewScaleZ() {
      return this.maxZ - this.minZ;
   }

   public void renderPreview(double x, double y, double z, int alpha) {
      int red = ColorUtils.getRed(this.color);
      int green = ColorUtils.getGreen(this.color);
      int blue = ColorUtils.getBlue(this.color);
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      if (this.previewScalingAndOffset()) {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderPreview(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderPreview(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha);
            }
         }
      } else {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderPreview(red, green, blue, alpha);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderPreview(red, green, blue, alpha);
            }
         }
      }

      GlStateManager.popMatrix();
   }

   public void renderLines(double x, double y, double z, int alpha) {
      int red = ColorUtils.getRed(this.color);
      int green = ColorUtils.getGreen(this.color);
      int blue = ColorUtils.getBlue(this.color);
      if (red == 1 && green == 1 && blue == 1) {
         blue = 0;
         green = 0;
         red = 0;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      if (this.previewScalingAndOffset()) {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderLines(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderLines(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha);
            }
         }
      } else {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderLines(red, green, blue, alpha);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderLines(red, green, blue, alpha);
            }
         }
      }

      GlStateManager.popMatrix();
   }

   public void renderLines(double x, double y, double z, int alpha, Vector3d center, double grow) {
      int red = ColorUtils.getRed(this.color);
      int green = ColorUtils.getGreen(this.color);
      int blue = ColorUtils.getBlue(this.color);
      if (red == 1 && green == 1 && blue == 1) {
         blue = 0;
         green = 0;
         red = 0;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      if (this.previewScalingAndOffset()) {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderLines(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha, center, grow);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderLines(this.getPreviewOffX(), this.getPreviewOffY(), this.getPreviewOffZ(), this.getPreviewScaleX(), this.getPreviewScaleY(), this.getPreviewScaleZ(), red, green, blue, alpha, center, grow);
            }
         }
      } else {
         for(int i = 0; i < EnumFacing.VALUES.length; ++i) {
            Object renderQuads = this.getRenderQuads(EnumFacing.VALUES[i]);
            if (renderQuads instanceof List) {
               for(int j = 0; j < ((List)renderQuads).size(); ++j) {
                  ((VectorFan)((List)renderQuads).get(j)).renderLines(red, green, blue, alpha, center, grow);
               }
            } else if (renderQuads instanceof VectorFan) {
               ((VectorFan)renderQuads).renderLines(red, green, blue, alpha, center, grow);
            }
         }
      }

      GlStateManager.popMatrix();
   }

   public boolean isTranslucent() {
      if (ColorUtils.isTransparent(this.color)) {
         return true;
      } else {
         return !this.getBlockState().getMaterial().blocksLight() || !this.getBlockState().getMaterial().isSolid() || !this.getBlockState().isOpaqueCube();
      }
   }

   public class RenderInformationHolder {
      public final EnumFacing facing;
      public final int color;
      public BlockPos offset;
      public boolean shouldOverrideColor;
      public BakedQuad quad;
      public NormalPlane normal;
      public Ray2d ray = new Ray2d(Axis.X, Axis.Y, 0.0D, 0.0D, 0.0D, 0.0D);
      public final boolean scaleAndOffset;
      public final float offsetX;
      public final float offsetY;
      public final float offsetZ;
      public final float scaleX;
      public final float scaleY;
      public final float scaleZ;
      public float minX;
      public float minY;
      public float minZ;
      public float maxX;
      public float maxY;
      public float maxZ;
      public float sizeX;
      public float sizeY;
      public float sizeZ;
      public boolean uvInverted;
      public float sizeU;
      public float sizeV;

      public RenderInformationHolder(EnumFacing facing, int color) {
         this.color = color;
         this.facing = facing;
         RenderBox box = this.getBox();
         this.scaleAndOffset = box.scaleAndOffsetQuads(facing);
         if (this.scaleAndOffset) {
            if (box.onlyScaleOnceNoOffset(facing)) {
               this.offsetX = this.offsetY = this.offsetZ = 0.0F;
               this.scaleX = this.scaleY = this.scaleZ = box.getOverallScale(facing);
            } else {
               this.offsetX = box.getOffsetX();
               this.offsetY = box.getOffsetY();
               this.offsetZ = box.getOffsetZ();
               this.scaleX = box.getScaleX();
               this.scaleY = box.getScaleY();
               this.scaleZ = box.getScaleZ();
            }
         } else {
            this.offsetX = this.offsetY = this.offsetZ = 0.0F;
            this.scaleX = this.scaleY = this.scaleZ = 0.0F;
         }

      }

      public void setQuad(BakedQuad quad, boolean overrideTint, int defaultColor) {
         this.quad = quad;
         this.shouldOverrideColor = overrideTint && (defaultColor == -1 || quad.hasTintIndex()) && this.color != -1;
      }

      public void setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
         this.minX = Math.min(minX, maxX);
         this.minY = Math.min(minY, maxY);
         this.minZ = Math.min(minZ, maxZ);
         this.maxX = Math.max(minX, maxX);
         this.maxY = Math.max(minY, maxY);
         this.maxZ = Math.max(minZ, maxZ);
         this.sizeX = this.maxX - this.minX;
         this.sizeY = this.maxY - this.minY;
         this.sizeZ = this.maxZ - this.minZ;
      }

      public RenderBox getBox() {
         return RenderBox.this;
      }

      public boolean hasBounds() {
         switch(this.facing.getAxis()) {
         case X:
            return this.minY != 0.0F || this.maxY != 1.0F || this.minZ != 0.0F || this.maxZ != 1.0F;
         case Y:
            return this.minX != 0.0F || this.maxX != 1.0F || this.minZ != 0.0F || this.maxZ != 1.0F;
         case Z:
            return this.minX != 0.0F || this.maxX != 1.0F || this.minY != 0.0F || this.maxY != 1.0F;
         default:
            return false;
         }
      }
   }

   private static class VectorFanSimple extends VectorFan {
      public VectorFanSimple(Vector3f[] coords) {
         super(coords);
      }

      @SideOnly(Side.CLIENT)
      public void generate(RenderBox.RenderInformationHolder holder, List<BakedQuad> quads) {
         int index;
         for(index = 0; index < this.coords.length - 3; index += 2) {
            this.generate(holder, this.coords[0], this.coords[index + 1], this.coords[index + 2], this.coords[index + 3], quads);
         }

         if (index < this.coords.length - 2) {
            this.generate(holder, this.coords[0], this.coords[index + 1], this.coords[index + 2], this.coords[index + 2], quads);
         }

      }

      protected boolean doMinMaxLate() {
         return true;
      }
   }
}
