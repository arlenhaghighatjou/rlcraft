package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchBasic;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class BakedModelBlockBranchBasic implements IBakedModel {
   protected ModelBlock modelBlock = new ModelBlock((ResourceLocation)null, (List)null, (Map)null, false, false, ItemCameraTransforms.DEFAULT, (List)null);
   TextureAtlasSprite barkParticles;
   private final IBakedModel[][] sleeves = new IBakedModel[6][7];
   private final IBakedModel[][] cores = new IBakedModel[3][8];
   private final IBakedModel[] rings = new IBakedModel[8];

   public BakedModelBlockBranchBasic(ResourceLocation barkRes, ResourceLocation ringsRes, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      TextureAtlasSprite barkIcon = (TextureAtlasSprite)bakedTextureGetter.apply(barkRes);
      TextureAtlasSprite ringIcon = (TextureAtlasSprite)bakedTextureGetter.apply(ringsRes);
      this.barkParticles = barkIcon;

      for(int i = 0; i < 8; ++i) {
         int radius = i + 1;
         if (radius < 8) {
            for(EnumFacing dir : EnumFacing.VALUES) {
               this.sleeves[dir.getIndex()][i] = this.bakeSleeve(radius, dir, barkIcon);
            }
         }

         this.cores[0][i] = this.bakeCore(radius, Axis.Y, barkIcon);
         this.cores[1][i] = this.bakeCore(radius, Axis.Z, barkIcon);
         this.cores[2][i] = this.bakeCore(radius, Axis.X, barkIcon);
         this.rings[i] = this.bakeCore(radius, Axis.Y, ringIcon);
      }

   }

   public IBakedModel bakeSleeve(int radius, EnumFacing dir, TextureAtlasSprite bark) {
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
               if (radius == 1) {
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
         builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)e.getValue(), bark, face, ModelRotation.X0_Y0, false));
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeCore(int radius, Axis axis, TextureAtlasSprite icon) {
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

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> quadsList = new ArrayList(24);
      if (!(state instanceof IExtendedBlockState)) {
         return quadsList;
      } else {
         IExtendedBlockState extendedState = (IExtendedBlockState)state;
         int coreRadius = this.getRadius(state);
         int[] connections = this.pollConnections(coreRadius, extendedState);
         int numConnections = 0;

         for(int i : connections) {
            numConnections += i != 0 ? 1 : 0;
         }

         EnumFacing sourceDir = this.getSourceDir(coreRadius, connections);
         if (sourceDir == null) {
            sourceDir = EnumFacing.DOWN;
         }

         int coreDir = this.resolveCoreDir(sourceDir);
         EnumFacing coreRingDir = numConnections == 1 ? sourceDir.getOpposite() : null;
         if (side == null || coreRadius != connections[side.getIndex()]) {
            if (coreRingDir != null && coreRingDir == side) {
               quadsList.addAll(this.rings[coreRadius - 1].getQuads(state, side, rand));
            } else {
               quadsList.addAll(this.cores[coreDir][coreRadius - 1].getQuads(state, side, rand));
            }
         }

         if (coreRadius != 8) {
            for(EnumFacing connDir : EnumFacing.VALUES) {
               int idx = connDir.getIndex();
               int connRadius = connections[idx];
               if (connRadius > 0 && (connRadius == 1 || side != connDir)) {
                  quadsList.addAll(this.sleeves[idx][connRadius - 1].getQuads(extendedState, side, rand));
               }
            }
         }

         return quadsList;
      }
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
      return ((BlockBranchBasic)blockState.getBlock()).getRadius(blockState);
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

   public TextureAtlasSprite getParticleTexture() {
      return this.barkParticles;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.sleeves[0][0].getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
