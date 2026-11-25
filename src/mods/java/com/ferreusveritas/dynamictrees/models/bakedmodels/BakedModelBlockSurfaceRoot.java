package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.client.ModelUtils;
import com.google.common.collect.Maps;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class BakedModelBlockSurfaceRoot implements IBakedModel {
   protected ModelBlock modelBlock = new ModelBlock((ResourceLocation)null, (List)null, (Map)null, false, false, ItemCameraTransforms.DEFAULT, (List)null);
   EaglerTextureAtlasSprite barkParticles;
   private final IBakedModel[][] sleeves = new IBakedModel[4][7];
   private final IBakedModel[][] cores = new IBakedModel[2][8];
   private final IBakedModel[][] verts = new IBakedModel[4][8];

   public BakedModelBlockSurfaceRoot(ResourceLocation barkRes, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      EaglerTextureAtlasSprite barkIcon = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(barkRes);
      this.barkParticles = barkIcon;

      for(int r = 0; r < 8; ++r) {
         int radius = r + 1;
         if (radius < 8) {
            for(EnumFacing dir : EnumFacing.HORIZONTALS) {
               int horIndex = dir.getHorizontalIndex();
               this.sleeves[horIndex][r] = this.bakeSleeve(radius, dir, barkIcon);
               this.verts[horIndex][r] = this.bakeVert(radius, dir, barkIcon);
            }
         }

         this.cores[0][r] = this.bakeCore(radius, Axis.Z, barkIcon);
         this.cores[1][r] = this.bakeCore(radius, Axis.X, barkIcon);
      }

   }

   public int getRadialHeight(int radius) {
      return radius * 2;
   }

   public IBakedModel bakeSleeve(int radius, EnumFacing dir, EaglerTextureAtlasSprite bark) {
      int radialHeight = this.getRadialHeight(radius);
      int dradius = radius * 2;
      int halfSize = (16 - dradius) / 2;
      int halfSizeX = dir.getFrontOffsetX() != 0 ? halfSize : dradius;
      int halfSizeZ = dir.getFrontOffsetZ() != 0 ? halfSize : dradius;
      int move = 16 - halfSize;
      int centerX = 16 + dir.getFrontOffsetX() * move;
      int centerZ = 16 + dir.getFrontOffsetZ() * move;
      Vector3f posFrom = new Vector3f((float)((centerX - halfSizeX) / 2), 0.0F, (float)((centerZ - halfSizeZ) / 2));
      Vector3f posTo = new Vector3f((float)((centerX + halfSizeX) / 2), (float)radialHeight, (float)((centerZ + halfSizeZ) / 2));
      boolean sleeveNegative = dir.getAxisDirection() == AxisDirection.NEGATIVE;
      if (dir.getAxis() == Axis.Z) {
         sleeveNegative = !sleeveNegative;
      }

      Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);

      for(EnumFacing face : EnumFacing.VALUES) {
         if (dir.getOpposite() != face) {
            BlockFaceUV uvface = null;
            if (face.getAxis().isHorizontal()) {
               boolean facePositive = face.getAxisDirection() == AxisDirection.POSITIVE;
               uvface = new BlockFaceUV(new float[]{facePositive ? (float)(16 - radialHeight) : 0.0F, (float)(sleeveNegative ? 16 - halfSize : 0), facePositive ? 16.0F : (float)radialHeight, (float)(sleeveNegative ? 16 : halfSize)}, ModelUtils.getFaceAngle(dir.getAxis(), face));
            } else {
               uvface = new BlockFaceUV(new float[]{(float)(8 - radius), sleeveNegative ? (float)(16 - halfSize) : 0.0F, (float)(8 + radius), sleeveNegative ? 16.0F : (float)halfSize}, ModelUtils.getFaceAngle(dir.getAxis(), face));
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
         builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)e.getValue(), bark, face, ModelRotation.X0_Y0, false));
      }

      return builder.makeBakedModel();
   }

   private IBakedModel bakeVert(int radius, EnumFacing dir, EaglerTextureAtlasSprite bark) {
      int radialHeight = this.getRadialHeight(radius);
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(bark);
      AxisAlignedBB partBoundary = (new AxisAlignedBB((double)(8 - radius), (double)radialHeight, (double)(8 - radius), (double)(8 + radius), (double)(16 + radialHeight), (double)(8 + radius))).offset((double)(dir.getFrontOffsetX() * 7), 0.0D, (double)(dir.getFrontOffsetZ() * 7));

      for(int i = 0; i < 2; ++i) {
         AxisAlignedBB pieceBoundary = partBoundary.intersect((new AxisAlignedBB(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)).offset(0.0D, (double)(16 * i), 0.0D));

         for(EnumFacing face : EnumFacing.VALUES) {
            Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);
            BlockFaceUV uvface = new BlockFaceUV(ModelUtils.modUV(ModelUtils.getUVs(pieceBoundary, face)), ModelUtils.getFaceAngle(Axis.Y, face));
            mapFacesIn.put(face, new BlockPartFace((EnumFacing)null, -1, (String)null, uvface));
            Vector3f[] limits = ModelUtils.AABBLimits(pieceBoundary);
            BlockPart part = new BlockPart(limits[0], limits[1], mapFacesIn, (BlockPartRotation)null, true);
            builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)part.mapFaces.get(face), bark, face, ModelRotation.X0_Y0, false));
         }
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeCore(int radius, Axis axis, EaglerTextureAtlasSprite icon) {
      int radialHeight = this.getRadialHeight(radius);
      Vector3f posFrom = new Vector3f((float)(8 - radius), 0.0F, (float)(8 - radius));
      Vector3f posTo = new Vector3f((float)(8 + radius), (float)radialHeight, (float)(8 + radius));
      Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);

      for(EnumFacing face : EnumFacing.VALUES) {
         BlockFaceUV uvface;
         if (face.getAxis().isHorizontal()) {
            boolean positive = face.getAxisDirection() == AxisDirection.POSITIVE;
            uvface = new BlockFaceUV(new float[]{positive ? (float)(16 - radialHeight) : 0.0F, (float)(8 - radius), positive ? 16.0F : (float)radialHeight, (float)(8 + radius)}, ModelUtils.getFaceAngle(axis, face));
         } else {
            uvface = new BlockFaceUV(new float[]{(float)(8 - radius), (float)(8 - radius), (float)(8 + radius), (float)(8 + radius)}, ModelUtils.getFaceAngle(axis, face));
         }

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

   public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
      List<BakedQuad> quadsList = new ArrayList(24);
      IExtendedBlockState extendedBlockState = (IExtendedBlockState)blockState;
      if (blockState instanceof IExtendedBlockState) {
         int coreRadius = this.getRadius(blockState);
         int[] connections = this.pollConnections(coreRadius, extendedBlockState);
         EnumFacing sourceDir = this.getSourceDir(coreRadius, connections);
         if (sourceDir == null) {
            sourceDir = EnumFacing.DOWN;
         }

         int coreDir = this.resolveCoreDir(sourceDir);
         boolean isGrounded = extendedBlockState.getValue(BlockSurfaceRoot.GROUNDED) == Boolean.TRUE;
         if (isGrounded) {
            quadsList.addAll(this.cores[coreDir][coreRadius - 1].getQuads(blockState, side, rand));
         }

         if (coreRadius != 8) {
            for(EnumFacing connDir : EnumFacing.HORIZONTALS) {
               int idx = connDir.getHorizontalIndex();
               int connRadius = connections[idx];
               if (connRadius > 0) {
                  if (isGrounded) {
                     quadsList.addAll(this.sleeves[idx][connRadius - 1].getQuads(extendedBlockState, side, rand));
                  }

                  if (extendedBlockState.getValue(BlockSurfaceRoot.LEVELS[idx]) == BlockSurfaceRoot.ConnectionLevel.HIGH) {
                     quadsList.addAll(this.verts[idx][connRadius - 1].getQuads(extendedBlockState, side, rand));
                  }
               }
            }
         }
      }

      return quadsList;
   }

   protected int[] pollConnections(int coreRadius, IExtendedBlockState extendedBlockState) {
      int[] connections = new int[4];

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         int horIndex = dir.getHorizontalIndex();
         int connection = this.getConnectionRadius(extendedBlockState, BlockSurfaceRoot.CONNECTIONS[horIndex]);
         connections[horIndex] = MathHelper.clamp(connection, 0, coreRadius);
      }

      return connections;
   }

   protected EnumFacing getSourceDir(int coreRadius, int[] connections) {
      int largestConnection = 0;
      EnumFacing sourceDir = null;

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         int horIndex = dir.getHorizontalIndex();
         int connRadius = connections[horIndex];
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
      return dir.getAxis() == Axis.X ? 1 : 0;
   }

   protected int getRadius(IBlockState blockState) {
      return ((BlockSurfaceRoot)blockState.getBlock()).getRadius(blockState);
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
