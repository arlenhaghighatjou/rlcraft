package com.ferreusveritas.dynamictrees.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.model.ITransformation;
import org.lwjgl.util.vector.Vector3f;

public class ModelUtils {
   public ModelUtils() {
   }

   public static float[] getUVs(AxisAlignedBB box, EnumFacing face) {
      switch(face) {
      case DOWN:
      default:
         return new float[]{(float)box.minX, 16.0F - (float)box.minZ, (float)box.maxX, 16.0F - (float)box.maxZ};
      case UP:
         return new float[]{(float)box.minX, (float)box.minZ, (float)box.maxX, (float)box.maxZ};
      case NORTH:
         return new float[]{16.0F - (float)box.maxX, (float)box.minY, 16.0F - (float)box.minX, (float)box.maxY};
      case SOUTH:
         return new float[]{(float)box.minX, (float)box.minY, (float)box.maxX, (float)box.maxY};
      case WEST:
         return new float[]{(float)box.minZ, (float)box.minY, (float)box.maxZ, (float)box.maxY};
      case EAST:
         return new float[]{16.0F - (float)box.maxZ, (float)box.minY, 16.0F - (float)box.minZ, (float)box.maxY};
      }
   }

   public static int getFaceAngle(Axis axis, EnumFacing face) {
      if (axis == Axis.Y) {
         return 0;
      } else if (axis == Axis.Z) {
         switch(face) {
         case DOWN:
            return 180;
         case UP:
            return 0;
         case NORTH:
            return 270;
         case SOUTH:
         default:
            return 90;
         case WEST:
            return 270;
         }
      } else {
         return face == EnumFacing.NORTH ? 270 : 90;
      }
   }

   public static float[] modUV(float[] uvs) {
      uvs[0] = (float)((int)uvs[0] & 15);
      uvs[1] = (float)((int)uvs[1] & 15);
      uvs[2] = (float)(((int)uvs[2] - 1 & 15) + 1);
      uvs[3] = (float)(((int)uvs[3] - 1 & 15) + 1);
      return uvs;
   }

   public static Vector3f[] AABBLimits(AxisAlignedBB aabb) {
      return new Vector3f[]{new Vector3f((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ), new Vector3f((float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)};
   }

   public static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace partFace, TextureAtlasSprite atlasSprite, EnumFacing dir, ITransformation transform, boolean uvlocked) {
      return (new FaceBakery()).makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, partFace, atlasSprite, dir, transform, blockPart.partRotation, uvlocked, blockPart.shade);
   }
}
