package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

public class IvMultiBlockHelper implements Iterable<BlockPos> {
   private Iterator<BlockPos> iterator;
   private List<BlockPos> childLocations;
   private IvTileEntityMultiBlock parentTileEntity = null;
   private World world;
   private IBlockState blockState;
   private EnumFacing direction;
   private double[] center;
   private double[] size;

   public IvMultiBlockHelper() {
   }

   public boolean beginPlacing(List<BlockPos> positions, World world, BlockPos pos, EnumFacing blockSide, ItemStack itemStack, EntityPlayer player, IBlockState state, EnumFacing direction) {
      List<BlockPos> validLocations = getBestPlacement(positions, world, pos, blockSide, itemStack, player, state);
      return validLocations == null ? false : this.beginPlacing(validLocations, world, state, direction);
   }

   public boolean beginPlacing(List<BlockPos> validLocations, World world, IBlockState state, EnumFacing direction) {
      this.world = world;
      this.parentTileEntity = null;
      this.direction = direction;
      this.blockState = state;
      this.center = getTileEntityCenter(validLocations);
      this.size = getTileEntitySize(validLocations);
      this.childLocations = validLocations;
      this.iterator = validLocations.iterator();
      return true;
   }

   public Iterator<BlockPos> iterator() {
      return this.iterator;
   }

   public IvTileEntityMultiBlock placeBlock(BlockPos pos) {
      return this.placeBlock(pos, this.parentTileEntity == null);
   }

   private IvTileEntityMultiBlock placeBlock(BlockPos pos, boolean parent) {
      this.world.setBlockState(pos, this.blockState, 3);
      TileEntity tileEntity = this.world.getTileEntity(pos);
      if (tileEntity instanceof IvTileEntityMultiBlock) {
         IvTileEntityMultiBlock tileEntityMB = (IvTileEntityMultiBlock)tileEntity;
         if (parent) {
            this.parentTileEntity = tileEntityMB;
            this.childLocations.remove(this.parentTileEntity.getPos());
            this.parentTileEntity.becomeParent(this.childLocations);
         } else {
            tileEntityMB.becomeChild(this.parentTileEntity);
         }

         tileEntityMB.facing = this.direction;
         tileEntityMB.centerCoords = new double[]{this.center[0] - (double)pos.getX(), this.center[1] - (double)pos.getY(), this.center[2] - (double)pos.getZ()};
         tileEntityMB.centerCoordsSize = this.size;
         return tileEntityMB;
      } else {
         return null;
      }
   }

   public static double[] getTileEntityCenter(List<BlockPos> positions) {
      double[] result = getCenter(positions);
      return new double[]{result[0] + 0.5D, result[1] + 0.5D, result[2] + 0.5D};
   }

   public static double[] getTileEntitySize(List<BlockPos> positions) {
      return getSize(positions);
   }

   public static double[] getCenter(List<BlockPos> positions) {
      if (positions.size() > 0) {
         BlockPos min = BlockPositions.getLowerCorner(positions);
         BlockPos max = BlockPositions.getHigherCorner(positions);
         double[] result = new double[3];
         result[0] = (double)(min.getX() + max.getX()) * 0.5D;
         result[1] = (double)(min.getY() + max.getY()) * 0.5D;
         result[2] = (double)(min.getZ() + max.getZ()) * 0.5D;
         return result;
      } else {
         return null;
      }
   }

   public static double[] getSize(List<BlockPos> positions) {
      if (positions.size() > 0) {
         BlockPos min = BlockPositions.getLowerCorner(positions);
         BlockPos max = BlockPositions.getHigherCorner(positions);
         double[] result = new double[3];
         result[0] = (double)(min.getX() - max.getX() + 1) * 0.5D;
         result[1] = (double)(min.getY() - max.getY() + 1) * 0.5D;
         result[2] = (double)(min.getZ() - max.getZ() + 1) * 0.5D;
         return result;
      } else {
         return null;
      }
   }

   public static int[] getLengths(List<BlockPos> positions) {
      BlockPos min = BlockPositions.getLowerCorner(positions);
      BlockPos max = BlockPositions.getHigherCorner(positions);
      return new int[]{max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ()};
   }

   /** @deprecated */
   @Deprecated
   public static boolean canPlace(World world, IBlockState state, List<BlockPos> positions, Entity entity, ItemStack stack) {
      return canPlace(world, state, positions, entity);
   }

   public static boolean canPlace(World world, IBlockState state, List<BlockPos> positions, Entity entity) {
      for(BlockPos position : positions) {
         if (!world.mayPlace(state.getBlock(), position, false, EnumFacing.DOWN, entity)) {
            return false;
         }
      }

      return true;
   }

   public static List<List<BlockPos>> getValidPlacements(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state) {
      world.getBlockState(pos);
      IBlockState iblockstate = world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (block == Blocks.SNOW_LAYER && ((Integer)iblockstate.getValue(BlockSnow.LAYERS)).intValue() < 1) {
         side = EnumFacing.UP;
      } else if (!block.isReplaceable(world, pos)) {
         pos = pos.offset(side);
      }

      if (!player.canPlayerEdit(pos, side, itemStack)) {
         return new ArrayList();
      } else if (pos.getY() == world.getHeight() && state.getMaterial().isSolid()) {
         return new ArrayList();
      } else {
         int[] lengths = getLengths(positions);
         BlockPos min = BlockPositions.getLowerCorner(positions);
         ArrayList<List<BlockPos>> validPlacements = new ArrayList();

         for(int xShift = min.getX() - lengths[0]; xShift <= min.getX(); ++xShift) {
            for(int yShift = min.getY() - lengths[1]; yShift <= min.getY(); ++yShift) {
               for(int zShift = min.getZ() - lengths[2]; zShift <= min.getZ(); ++zShift) {
                  ArrayList<BlockPos> validPositions = new ArrayList();

                  for(BlockPos position : positions) {
                     validPositions.add(position.add(xShift, yShift, zShift).add(pos));
                  }

                  if (canPlace(world, state, validPositions, (Entity)null, itemStack)) {
                     validPlacements.add(validPositions);
                  }
               }
            }
         }

         return validPlacements;
      }
   }

   public static List<BlockPos> getBestPlacement(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state) {
      int[] lengths = getLengths(positions);
      List<List<BlockPos>> validPlacements = getValidPlacements(positions, world, pos, side, itemStack, player, state);
      if (validPlacements.size() > 0) {
         float[] center = new float[]{(float)pos.getX() - (float)lengths[0] * 0.5F, (float)pos.getY() - (float)lengths[1] * 0.5F, (float)pos.getZ() - (float)lengths[2] * 0.5F};
         List<BlockPos> preferredPositions = (List)validPlacements.get(0);

         for(int i = 1; i < validPlacements.size(); ++i) {
            BlockPos referenceBlock = (BlockPos)((List)validPlacements.get(i)).get(0);
            BlockPos referenceBlockOriginal = (BlockPos)preferredPositions.get(0);
            if (distanceSquared(BlockPositions.toIntArray(referenceBlock), center) < distanceSquared(BlockPositions.toIntArray(referenceBlockOriginal), center)) {
               preferredPositions = (List)validPlacements.get(i);
            }
         }

         return preferredPositions;
      } else {
         return null;
      }
   }

   private static float distanceSquared(int[] referenceBlock, float[] center) {
      float distX = (float)referenceBlock[0] - center[0];
      float distY = (float)referenceBlock[1] - center[1];
      float distZ = (float)referenceBlock[2] - center[2];
      return distX * distX + distY * distY + distZ * distZ;
   }

   public static List<BlockPos> getRotatedPositions(List<BlockPos> positions, EnumFacing facing) {
      ArrayList<BlockPos> returnList = new ArrayList(positions.size());

      for(BlockPos position : positions) {
         switch(facing) {
         case NORTH:
         case SOUTH:
            returnList.add(position);
            break;
         case WEST:
         case EAST:
            returnList.add(new BlockPos(position.getZ(), position.getY(), position.getX()));
         }
      }

      return returnList;
   }

   public static IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords) {
      IvRaytraceableAxisAlignedBox box = null;
      switch(direction) {
      case NORTH:
         box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
         break;
      case SOUTH:
         box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
         break;
      case WEST:
         box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
         break;
      case EAST:
         box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
      }

      return box;
   }

   public static AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords) {
      AxisAlignedBB box = null;
      switch(direction) {
      case NORTH:
         box = getBBWithLengths(centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
         break;
      case SOUTH:
         box = getBBWithLengths(centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
         break;
      case WEST:
         box = getBBWithLengths(centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
         break;
      case EAST:
         box = getBBWithLengths(centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
      }

      return box;
   }

   public static Vector3f getRotatedVector(Vector3f vector3f, EnumFacing facing) {
      switch(facing) {
      case NORTH:
         return new Vector3f(-vector3f.x, vector3f.y, -vector3f.z);
      case SOUTH:
         return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
      case WEST:
         return new Vector3f(-vector3f.z, vector3f.y, vector3f.x);
      case EAST:
         return new Vector3f(vector3f.z, vector3f.y, -vector3f.x);
      default:
         return null;
      }
   }

   public static Vec3d getRotatedVector(Vec3d vec3, EnumFacing facing) {
      switch(facing) {
      case NORTH:
         return new Vec3d(-vec3.x, vec3.y, -vec3.z);
      case SOUTH:
         return new Vec3d(vec3.x, vec3.y, vec3.z);
      case WEST:
         return new Vec3d(-vec3.z, vec3.y, vec3.x);
      case EAST:
         return new Vec3d(vec3.z, vec3.y, -vec3.x);
      default:
         return null;
      }
   }

   public static AxisAlignedBB getBBWithLengths(double x, double y, double z, double width, double height, double depth) {
      return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
   }

   public static EnumFacing getRotation(Entity entity) {
      return entity.getHorizontalFacing();
   }

   public static List<BlockPos> getRotatedPositions(EnumFacing facing, int width, int height, int length) {
      boolean affectsX = facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH;
      return getPositions(affectsX ? width : length, height, affectsX ? length : width);
   }

   public static List<BlockPos> getPositions(int width, int height, int length) {
      ArrayList<BlockPos> positions = new ArrayList();

      for(int x = 0; x < width; ++x) {
         for(int y = 0; y < height; ++y) {
            for(int z = 0; z < length; ++z) {
               positions.add(new BlockPos(x, y, z));
            }
         }
      }

      return positions;
   }
}
