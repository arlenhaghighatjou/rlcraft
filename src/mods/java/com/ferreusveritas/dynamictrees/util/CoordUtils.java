package com.ferreusveritas.dynamictrees.util;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class CoordUtils {
   public static int coordXor = 0;
   private static final int[][] coordHashMap = new int[][]{{4111, 271, 3067}, {7933711, 6144389, 9538033}, {9973, 8287, 9721}, {7211, 5437, 9613}};

   public CoordUtils() {
   }

   public static boolean isSurroundedByLoadedChunks(World world, BlockPos pos) {
      for(CoordUtils.Surround surr : CoordUtils.Surround.values()) {
         Vec3i dir = surr.getOffset();
         if (world.getChunkProvider().getLoadedChunk((pos.getX() >> 4) + dir.getX(), (pos.getZ() >> 4) + dir.getZ()) == null) {
            return false;
         }
      }

      return true;
   }

   public static EnumFacing getRandomDir(Random rand) {
      return EnumFacing.getFront(2 + rand.nextInt(4));
   }

   public static BlockPos getRayTraceFruitPos(World world, Species species, BlockPos treePos, BlockPos branchPos, SafeChunkBounds safeBounds) {
      RayTraceResult result = branchRayTrace(world, species, treePos, branchPos, 45.0F, 60.0F, (float)(4 + world.rand.nextInt(3)), safeBounds);
      if (result != null) {
         BlockPos hitPos = result.getBlockPos();
         if (hitPos != BlockPos.ORIGIN) {
            while(true) {
               hitPos = hitPos.down();
               if (!species.getFamily().isCompatibleGenericLeaves(world.getBlockState(hitPos), world, hitPos)) {
                  break;
               }
            }

            if (world.isAirBlock(hitPos)) {
               return hitPos;
            }
         }
      }

      return BlockPos.ORIGIN;
   }

   public static RayTraceResult branchRayTrace(World world, Species species, BlockPos treePos, BlockPos branchPos, float spreadHor, float spreadVer, float distance, SafeChunkBounds safeBounds) {
      treePos = new BlockPos(treePos.getX(), branchPos.getY(), treePos.getZ());
      Vec3d vOut = new Vec3d((double)(branchPos.getX() - treePos.getX()), 0.0D, (double)(branchPos.getZ() - treePos.getZ()));
      if (vOut.equals(Vec3d.ZERO)) {
         vOut = new Vec3d(1.0D, 0.0D, 0.0D);
         spreadHor = 180.0F;
      }

      float deltaYaw = world.rand.nextFloat() * spreadHor * 2.0F - spreadHor;
      float deltaPitch = world.rand.nextFloat() * -spreadVer;
      vOut = vOut.normalize().addVector(0.0D, Math.tan(Math.toRadians((double)deltaPitch)), 0.0D).normalize().rotateYaw((float)Math.toRadians((double)deltaYaw)).scale((double)distance);
      Vec3d branchVec = (new Vec3d(branchPos)).addVector(0.5D, 0.5D, 0.5D);
      Vec3d vantageVec = branchVec.add(vOut);
      BlockPos vantagePos = new BlockPos(vantageVec);
      if (!safeBounds.inBounds(vantagePos, false) || world.isAirBlock(vantagePos)) {
         RayTraceResult result = rayTraceBlocks(world, vantageVec, branchVec, false, true, false, safeBounds);
         if (result != null) {
            BlockPos hitPos = result.getBlockPos();
            if (result.typeOfHit == Type.BLOCK && hitPos != BlockPos.ORIGIN && species.getFamily().isCompatibleGenericLeaves(world.getBlockState(hitPos), world, hitPos)) {
               return result;
            }
         }
      }

      return null;
   }

   public static RayTraceResult rayTraceBlocks(World world, Vec3d vantage, Vec3d lookingAt, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, SafeChunkBounds safeBounds) {
      if (!Double.isNaN(vantage.x) && !Double.isNaN(vantage.y) && !Double.isNaN(vantage.z)) {
         if (!Double.isNaN(lookingAt.x) && !Double.isNaN(lookingAt.y) && !Double.isNaN(lookingAt.z)) {
            int vantX = net.minecraft.util.math.MathHelper.floor(lookingAt.x);
            int vantY = net.minecraft.util.math.MathHelper.floor(lookingAt.y);
            int vantZ = net.minecraft.util.math.MathHelper.floor(lookingAt.z);
            int lookX = net.minecraft.util.math.MathHelper.floor(vantage.x);
            int lookY = net.minecraft.util.math.MathHelper.floor(vantage.y);
            int lookZ = net.minecraft.util.math.MathHelper.floor(vantage.z);
            BlockPos lookPos = new BlockPos(lookX, lookY, lookZ);
            IBlockState lookState = safeBounds.inBounds(lookPos, false) ? world.getBlockState(lookPos) : Blocks.AIR.getDefaultState();
            Block lookBlock = lookState.getBlock();
            AxisAlignedBB colBB1 = safeBounds.inBounds(lookPos, false) ? lookState.getCollisionBoundingBox(world, lookPos) : Block.NULL_AABB;
            boolean specialRayTraceCollision = lookBlock instanceof IRayTraceCollision && ((IRayTraceCollision)lookBlock).isRayTraceCollidable();
            if (specialRayTraceCollision || (!ignoreBlockWithoutBoundingBox || colBB1 != Block.NULL_AABB) && lookBlock.canCollideCheck(lookState, stopOnLiquid)) {
               RayTraceResult raytraceresult = lookState.collisionRayTrace(world, lookPos, vantage, lookingAt);
               if (raytraceresult != null) {
                  return raytraceresult;
               }
            }

            RayTraceResult raytraceresult2 = null;
            int ropeLen = 200;

            while(ropeLen-- >= 0) {
               if (Double.isNaN(vantage.x) || Double.isNaN(vantage.y) || Double.isNaN(vantage.z)) {
                  return null;
               }

               if (lookX == vantX && lookY == vantY && lookZ == vantZ) {
                  return returnLastUncollidableBlock ? raytraceresult2 : null;
               }

               boolean flagX = true;
               boolean flagY = true;
               boolean flagZ = true;
               double modX = 999.0D;
               double modY = 999.0D;
               double modZ = 999.0D;
               if (vantX > lookX) {
                  modX = (double)lookX + 1.0D;
               } else if (vantX < lookX) {
                  modX = (double)lookX + 0.0D;
               } else {
                  flagX = false;
               }

               if (vantY > lookY) {
                  modY = (double)lookY + 1.0D;
               } else if (vantY < lookY) {
                  modY = (double)lookY + 0.0D;
               } else {
                  flagY = false;
               }

               if (vantZ > lookZ) {
                  modZ = (double)lookZ + 1.0D;
               } else if (vantZ < lookZ) {
                  modZ = (double)lookZ + 0.0D;
               } else {
                  flagZ = false;
               }

               double unkX = 999.0D;
               double unkY = 999.0D;
               double unkZ = 999.0D;
               double deltaX = lookingAt.x - vantage.x;
               double deltaY = lookingAt.y - vantage.y;
               double deltaZ = lookingAt.z - vantage.z;
               if (flagX) {
                  unkX = (modX - vantage.x) / deltaX;
               }

               if (flagY) {
                  unkY = (modY - vantage.y) / deltaY;
               }

               if (flagZ) {
                  unkZ = (modZ - vantage.z) / deltaZ;
               }

               if (unkX == -0.0D) {
                  unkX = -1.0E-4D;
               }

               if (unkY == -0.0D) {
                  unkY = -1.0E-4D;
               }

               if (unkZ == -0.0D) {
                  unkZ = -1.0E-4D;
               }

               EnumFacing enumfacing;
               if (unkX < unkY && unkX < unkZ) {
                  enumfacing = vantX > lookX ? EnumFacing.WEST : EnumFacing.EAST;
                  vantage = new Vec3d(modX, vantage.y + deltaY * unkX, vantage.z + deltaZ * unkX);
               } else if (unkY < unkZ) {
                  enumfacing = vantY > lookY ? EnumFacing.DOWN : EnumFacing.UP;
                  vantage = new Vec3d(vantage.x + deltaX * unkY, modY, vantage.z + deltaZ * unkY);
               } else {
                  enumfacing = vantZ > lookZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  vantage = new Vec3d(vantage.x + deltaX * unkZ, vantage.y + deltaY * unkZ, modZ);
               }

               lookX = net.minecraft.util.math.MathHelper.floor(vantage.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
               lookY = net.minecraft.util.math.MathHelper.floor(vantage.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
               lookZ = net.minecraft.util.math.MathHelper.floor(vantage.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
               lookPos = new BlockPos(lookX, lookY, lookZ);
               IBlockState lookState2 = safeBounds.inBounds(lookPos, false) ? world.getBlockState(lookPos) : Blocks.AIR.getDefaultState();
               Block lookBlock2 = lookState2.getBlock();
               AxisAlignedBB colBB2 = safeBounds.inBounds(lookPos, false) ? lookState2.getCollisionBoundingBox(world, lookPos) : Block.NULL_AABB;
               specialRayTraceCollision = lookBlock2 instanceof IRayTraceCollision && ((IRayTraceCollision)lookBlock2).isRayTraceCollidable();
               if (specialRayTraceCollision || !ignoreBlockWithoutBoundingBox || lookState2.getMaterial() == Material.PORTAL || colBB2 != Block.NULL_AABB) {
                  if (!specialRayTraceCollision && !lookBlock2.canCollideCheck(lookState2, stopOnLiquid)) {
                     raytraceresult2 = new RayTraceResult(Type.MISS, vantage, enumfacing, lookPos);
                  } else {
                     RayTraceResult raytraceresult1 = lookState2.collisionRayTrace(world, lookPos, vantage, lookingAt);
                     if (raytraceresult1 != null) {
                        return raytraceresult1;
                     }
                  }
               }
            }

            return returnLastUncollidableBlock ? raytraceresult2 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static BlockPos findGround(World world, BlockPos startPos) {
      MutableBlockPos pos = new MutableBlockPos(startPos);

      while(world.getBlockState(pos).isFullCube()) {
         pos.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
      }

      while(!world.getBlockState(pos).isFullCube() && pos.getY() > 50) {
         pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
      }

      return pos;
   }

   public static int coordHashCode(BlockPos pos, int a, int b, int c) {
      int hash = (pos.getX() * a ^ pos.getY() * b ^ pos.getZ() * c) >> 1;
      return (hash ^ coordXor) & '\uffff';
   }

   public static int coordHashCode(BlockPos pos, int readyMade) {
      int[] factors = coordHashMap[readyMade & 3];
      return coordHashCode(pos, factors[0], factors[1], factors[2]);
   }

   public static Iterable<BlockPos> goHorSides(BlockPos pos) {
      return goHorSides(pos, (EnumFacing)null);
   }

   public static Iterable<BlockPos> goHorSides(final BlockPos pos, final EnumFacing ignore) {
      return new Iterable<BlockPos>() {
         public Iterator<BlockPos> iterator() {
            return new AbstractIterator<BlockPos>() {
               private int currentDir = 0;

               protected BlockPos computeNext() {
                  while(true) {
                     if (this.currentDir < EnumFacing.HORIZONTALS.length) {
                        EnumFacing face = EnumFacing.HORIZONTALS[this.currentDir++];
                        if (face == ignore) {
                           continue;
                        }

                        return pos.offset(face);
                     }

                     return (BlockPos)this.endOfData();
                  }
               }
            };
         }
      };
   }

   public static enum Surround implements IStringSerializable {
      N("n", new EnumFacing[]{EnumFacing.NORTH}),
      NW("nw", new EnumFacing[]{EnumFacing.NORTH, EnumFacing.WEST}),
      W("w", new EnumFacing[]{EnumFacing.WEST}),
      SW("sw", new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}),
      S("s", new EnumFacing[]{EnumFacing.SOUTH}),
      SE("se", new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST}),
      E("e", new EnumFacing[]{EnumFacing.EAST}),
      NE("ne", new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST});

      private final String name;
      private final Vec3i offset;

      private Surround(String name, EnumFacing[] dirs) {
         this.name = name;
         BlockPos pos = BlockPos.ORIGIN;

         for(EnumFacing d : dirs) {
            pos = pos.add(d.getDirectionVec());
         }

         this.offset = pos;
      }

      public String getName() {
         return this.name;
      }

      public Vec3i getOffset() {
         return this.offset;
      }

      public BlockPos getOffsetPos() {
         return new BlockPos(this.offset);
      }

      public CoordUtils.Surround getOpposite() {
         return values()[this.ordinal() + 4 & 7];
      }
   }
}
