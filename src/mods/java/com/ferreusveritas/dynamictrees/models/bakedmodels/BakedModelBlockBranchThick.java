package com.ferreusveritas.dynamictrees.models.bakedmodels;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.client.ModelUtils;
import com.ferreusveritas.dynamictrees.models.ICustomDamageModel;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class BakedModelBlockBranchThick extends BakedModelBlockBranchBasic implements ICustomDamageModel {
   private final IBakedModel[] trunksBark = new IBakedModel[16];
   private final IBakedModel[] trunksTopBark = new IBakedModel[16];
   private final IBakedModel[] trunksTopRings = new IBakedModel[16];
   private final IBakedModel[] trunksBotRings = new IBakedModel[16];

   public BakedModelBlockBranchThick(ResourceLocation barkRes, ResourceLocation ringsRes, ResourceLocation thickRingsRes, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      super(barkRes, ringsRes, bakedTextureGetter);
      TextureAtlasSprite barkIcon = (TextureAtlasSprite)bakedTextureGetter.apply(barkRes);
      TextureAtlasSprite ringIcon = (TextureAtlasSprite)bakedTextureGetter.apply(ringsRes);
      TextureAtlasSprite thickRingIcon = (TextureAtlasSprite)bakedTextureGetter.apply(thickRingsRes);
      this.barkParticles = barkIcon;

      for(int i = 0; i < 16; ++i) {
         int radius = i + 9;
         this.trunksBark[i] = this.bakeTrunkBark(radius, barkIcon, true);
         this.trunksTopBark[i] = this.bakeTrunkBark(radius, barkIcon, false);
         this.trunksTopRings[i] = this.bakeTrunkRings(radius, ModConfigs.fancyThickRings ? thickRingIcon : ringIcon, EnumFacing.UP);
         this.trunksBotRings[i] = this.bakeTrunkRings(radius, ModConfigs.fancyThickRings ? thickRingIcon : ringIcon, EnumFacing.DOWN);
      }

   }

   public IBakedModel bakeTrunkBark(int radius, TextureAtlasSprite bark, boolean side) {
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(bark);
      AxisAlignedBB wholeVolume = new AxisAlignedBB((double)(8 - radius), 0.0D, (double)(8 - radius), (double)(8 + radius), 16.0D, (double)(8 + radius));
      EnumFacing[] run = side ? EnumFacing.HORIZONTALS : new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
      ArrayList<Vec3i> offsets = new ArrayList();

      for(CoordUtils.Surround dir : CoordUtils.Surround.values()) {
         offsets.add(dir.getOffset());
      }

      offsets.add(new Vec3i(0, 0, 0));

      for(EnumFacing face : run) {
         for(Vec3i offset : offsets) {
            if (face.getAxis() == Axis.Y || (new Vec3d(face.getDirectionVec())).add(new Vec3d(offset)).lengthSquared() > 2.25D) {
               Vec3d scaledOffset = new Vec3d((double)(offset.getX() * 16), (double)(offset.getY() * 16), (double)(offset.getZ() * 16));
               AxisAlignedBB partBoundary = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)).offset(scaledOffset).intersect(wholeVolume);
               Vector3f[] limits = ModelUtils.AABBLimits(partBoundary);
               Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);
               BlockFaceUV uvface = new BlockFaceUV(ModelUtils.modUV(ModelUtils.getUVs(partBoundary, face)), this.getFaceAngle(Axis.Y, face));
               mapFacesIn.put(face, new BlockPartFace((EnumFacing)null, -1, (String)null, uvface));
               BlockPart part = new BlockPart(limits[0], limits[1], mapFacesIn, (BlockPartRotation)null, true);
               builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)part.mapFaces.get(face), bark, face, ModelRotation.X0_Y0, false));
            }
         }
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeTrunkRings(int radius, TextureAtlasSprite ring, EnumFacing face) {
      return this.bakeTrunkRings(radius, ring, face, ModConfigs.fancyThickRings);
   }

   public IBakedModel bakeTrunkRings(int radius, TextureAtlasSprite ring, EnumFacing face, boolean fancy) {
      Builder builder = (new Builder(this.modelBlock, ItemOverrideList.NONE)).setTexture(ring);
      AxisAlignedBB wholeVolume = new AxisAlignedBB((double)(8 - radius), 0.0D, (double)(8 - radius), (double)(8 + radius), 16.0D, (double)(8 + radius));
      int wholeVolumeWidth = fancy ? 48 : radius * 2;
      ArrayList<Vec3i> offsets = new ArrayList();

      for(CoordUtils.Surround dir : CoordUtils.Surround.values()) {
         offsets.add(dir.getOffset());
      }

      offsets.add(new Vec3i(0, 0, 0));

      for(Vec3i offset : offsets) {
         Vec3d scaledOffset = new Vec3d((double)(offset.getX() * 16), (double)(offset.getY() * 16), (double)(offset.getZ() * 16));
         AxisAlignedBB partBoundary = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)).offset(scaledOffset).intersect(wholeVolume);
         Vector3f posFrom = new Vector3f((float)partBoundary.minX, (float)partBoundary.minY, (float)partBoundary.minZ);
         Vector3f posTo = new Vector3f((float)partBoundary.maxX, (float)partBoundary.maxY, (float)partBoundary.maxZ);
         Map<EnumFacing, BlockPartFace> mapFacesIn = Maps.newEnumMap(EnumFacing.class);
         float textureOffsetX = fancy ? -16.0F : (float)wholeVolume.minX;
         float textureOffsetZ = fancy ? -16.0F : (float)wholeVolume.minZ;
         float minX = (float)((partBoundary.minX - (double)textureOffsetX) / (double)wholeVolumeWidth) * 16.0F;
         float maxX = (float)((partBoundary.maxX - (double)textureOffsetX) / (double)wholeVolumeWidth) * 16.0F;
         float minZ = (float)((partBoundary.minZ - (double)textureOffsetZ) / (double)wholeVolumeWidth) * 16.0F;
         float maxZ = (float)((partBoundary.maxZ - (double)textureOffsetZ) / (double)wholeVolumeWidth) * 16.0F;
         if (face == EnumFacing.DOWN) {
            minZ = (float)((partBoundary.maxZ - (double)textureOffsetZ) / (double)wholeVolumeWidth) * 16.0F;
            maxZ = (float)((partBoundary.minZ - (double)textureOffsetZ) / (double)wholeVolumeWidth) * 16.0F;
         }

         float[] uvs = new float[]{minX, minZ, maxX, maxZ};
         BlockFaceUV uvface = new BlockFaceUV(uvs, this.getFaceAngle(Axis.Y, face));
         mapFacesIn.put(face, new BlockPartFace((EnumFacing)null, -1, (String)null, uvface));
         BlockPart part = new BlockPart(posFrom, posTo, mapFacesIn, (BlockPartRotation)null, true);
         builder.addFaceQuad(face, ModelUtils.makeBakedQuad(part, (BlockPartFace)part.mapFaces.get(face), ring, face, ModelRotation.X0_Y0, false));
      }

      return builder.makeBakedModel();
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      int coreRadius = this.getRadius(state);
      if (coreRadius <= 8) {
         return super.getQuads(state, side, rand);
      } else {
         coreRadius = MathHelper.clamp(coreRadius, 9, 24);
         List<BakedQuad> quadsList = new ArrayList(30);
         quadsList.addAll(this.trunksBark[coreRadius - 9].getQuads(state, side, rand));
         if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState)state;
            int[] connections = this.pollConnections(coreRadius, extendedBlockState);
            if (connections[0] < 1) {
               quadsList.addAll(this.trunksBotRings[coreRadius - 9].getQuads(state, side, rand));
            }

            if (connections[1] < 1) {
               quadsList.addAll(this.trunksTopRings[coreRadius - 9].getQuads(state, side, rand));
            } else if (connections[1] == 1 && side == EnumFacing.UP) {
               quadsList.addAll(this.trunksTopBark[coreRadius - 9].getQuads(state, side, rand));
            }
         }

         return quadsList;
      }
   }

   public List<BakedQuad> getCustomDamageQuads(IBlockState blockState, EnumFacing side, long rand) {
      int coreRadius = this.getRadius(blockState);
      if (coreRadius <= 8) {
         return super.getQuads(blockState, side, rand);
      } else {
         coreRadius = MathHelper.clamp(coreRadius, 9, 24);
         List<BakedQuad> quadsList = new LinkedList();
         quadsList.addAll(this.trunksBark[coreRadius - 9].getQuads(blockState, side, rand));
         quadsList.addAll(this.trunksTopBark[coreRadius - 9].getQuads(blockState, side, rand));
         return quadsList;
      }
   }
}
