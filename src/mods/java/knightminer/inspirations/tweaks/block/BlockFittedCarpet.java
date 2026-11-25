package knightminer.inspirations.tweaks.block;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFittedCarpet extends BlockCarpet {
   public static final PropertyBool NORTHWEST = PropertyBool.create("northwest");
   public static final PropertyBool NORTHEAST = PropertyBool.create("northeast");
   public static final PropertyBool SOUTHWEST = PropertyBool.create("southwest");
   public static final PropertyBool SOUTHEAST = PropertyBool.create("southeast");
   private static final AxisAlignedBB[] BOUNDS;
   private static final AxisAlignedBB BOUNDS_NW = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 0.0625D, 0.5625D);
   private static final AxisAlignedBB BOUNDS_NE = new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.5625D);
   private static final AxisAlignedBB BOUNDS_SW = new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 0.0625D, 1.0D);
   private static final AxisAlignedBB BOUNDS_SE = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 0.0625D, 1.0D);

   public BlockFittedCarpet() {
      this.setHardness(0.1F);
      this.setSoundType(SoundType.CLOTH);
      this.setUnlocalizedName("woolCarpet");
      this.setLightOpacity(0);
      this.setDefaultState(this.getDefaultState().withProperty(COLOR, EnumDyeColor.WHITE).withProperty(NORTHWEST, Boolean.valueOf(false)).withProperty(NORTHEAST, Boolean.valueOf(false)).withProperty(SOUTHWEST, Boolean.valueOf(false)).withProperty(SOUTHEAST, Boolean.valueOf(false)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{COLOR, NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.setProperties(state, this.getStairShape(world, pos.down()));
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.getStairShape(world, pos) > 0 ? NULL_AABB : super.getCollisionBoundingBox(state, world, pos);
   }

   /** @deprecated */
   @Deprecated
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOUNDS[this.getStairShape(source, pos.down())];
   }

   /** @deprecated */
   @Deprecated
   public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
      int shape = this.getStairShape(world, pos.down());
      switch(shape) {
      case 1:
      case 2:
      case 4:
      case 6:
      case 8:
      case 9:
         List<RayTraceResult> list = new ArrayList();
         if ((shape & 8) == 0) {
            list.add(this.rayTrace(pos, start, end, BOUNDS_NW));
         }

         if ((shape & 4) == 0) {
            list.add(this.rayTrace(pos, start, end, BOUNDS_NE));
         }

         if ((shape & 2) == 0) {
            list.add(this.rayTrace(pos, start, end, BOUNDS_SW));
         }

         if ((shape & 1) == 0) {
            list.add(this.rayTrace(pos, start, end, BOUNDS_SE));
         }

         return Util.closestResult(list, end);
      case 3:
      case 5:
      case 7:
      default:
         return super.collisionRayTrace(state, world, pos, start, end);
      }
   }

   private int getStairShape(IBlockAccess world, BlockPos pos) {
      IBlockState stairs = world.getBlockState(pos);
      if (stairs.getBlock() instanceof BlockStairs && stairs.getValue(BlockStairs.HALF) != EnumHalf.TOP) {
         stairs = stairs.getActualState(world, pos);
         EnumShape shape = (EnumShape)stairs.getValue(BlockStairs.SHAPE);
         switch((EnumFacing)stairs.getValue(BlockStairs.FACING)) {
         case NORTH:
            switch(shape) {
            case STRAIGHT:
               return 3;
            case INNER_LEFT:
               return 1;
            case INNER_RIGHT:
               return 2;
            case OUTER_LEFT:
               return 7;
            case OUTER_RIGHT:
               return 11;
            }
         case SOUTH:
            switch(shape) {
            case STRAIGHT:
               return 12;
            case INNER_LEFT:
               return 8;
            case INNER_RIGHT:
               return 4;
            case OUTER_LEFT:
               return 14;
            case OUTER_RIGHT:
               return 13;
            }
         case WEST:
            switch(shape) {
            case STRAIGHT:
               return 5;
            case INNER_LEFT:
               return 4;
            case INNER_RIGHT:
               return 1;
            case OUTER_LEFT:
               return 13;
            case OUTER_RIGHT:
               return 7;
            }
         case EAST:
            switch(shape) {
            case STRAIGHT:
               return 10;
            case INNER_LEFT:
               return 2;
            case INNER_RIGHT:
               return 8;
            case OUTER_LEFT:
               return 11;
            case OUTER_RIGHT:
               return 14;
            }
         default:
            return 0;
         }
      } else {
         return 0;
      }
   }

   private IBlockState setProperties(IBlockState state, int i) {
      return state.withProperty(NORTHWEST, Boolean.valueOf((i & 8) > 0)).withProperty(NORTHEAST, Boolean.valueOf((i & 4) > 0)).withProperty(SOUTHWEST, Boolean.valueOf((i & 2) > 0)).withProperty(SOUTHEAST, Boolean.valueOf((i & 1) > 0));
   }

   static {
      BOUNDS = new AxisAlignedBB[]{CARPET_AABB, CARPET_AABB, CARPET_AABB, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.5625D), CARPET_AABB, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 0.0625D, 1.0D), CARPET_AABB, BOUNDS_NW, CARPET_AABB, CARPET_AABB, new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), BOUNDS_NE, new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 0.0625D, 1.0D), BOUNDS_SW, BOUNDS_SE, CARPET_AABB};
   }
}
