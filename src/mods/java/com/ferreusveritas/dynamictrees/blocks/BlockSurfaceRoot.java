package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModTabs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties.PropertyAdapter;

public class BlockSurfaceRoot extends Block {
   public static final int RADMAX_NORMAL = 8;
   protected static final PropertyInteger RADIUS = PropertyInteger.create("radius", 1, 8);
   public static final IUnlistedProperty GROUNDED = new PropertyAdapter(PropertyBool.create("grounded"));
   public static final IUnlistedProperty[] CONNECTIONS = new IUnlistedProperty[]{new PropertyAdapter(PropertyInteger.create("radiuss", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiusw", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiusn", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiuse", 0, 8))};
   public static final IUnlistedProperty[] LEVELS = new IUnlistedProperty[]{new PropertyAdapter(PropertyEnum.create("levels", BlockSurfaceRoot.ConnectionLevel.class)), new PropertyAdapter(PropertyEnum.create("levelw", BlockSurfaceRoot.ConnectionLevel.class)), new PropertyAdapter(PropertyEnum.create("leveln", BlockSurfaceRoot.ConnectionLevel.class)), new PropertyAdapter(PropertyEnum.create("levele", BlockSurfaceRoot.ConnectionLevel.class))};
   public static final CoordUtils.Surround[] sidesFirst = new CoordUtils.Surround[]{CoordUtils.Surround.N, CoordUtils.Surround.S, CoordUtils.Surround.W, CoordUtils.Surround.E, CoordUtils.Surround.NW, CoordUtils.Surround.NE, CoordUtils.Surround.SW, CoordUtils.Surround.SE};

   public BlockSurfaceRoot(Material material, String name) {
      super(material);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(RADIUS, Integer.valueOf(1)));
      this.setHarvestLevel("axe", 0);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(2.5F);
      this.setResistance(1.0F);
      this.setCreativeTab(ModTabs.dynamicTreesTab);
   }

   protected BlockStateContainer createBlockState() {
      IProperty[] listedProperties = new IProperty[]{RADIUS};
      IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{CONNECTIONS[0], CONNECTIONS[1], CONNECTIONS[2], CONNECTIONS[3], LEVELS[0], LEVELS[1], LEVELS[2], LEVELS[3], GROUNDED};
      return new ExtendedBlockState(this, listedProperties, unlistedProperties);
   }

   public IProperty<?>[] getIgnorableProperties() {
      return new IProperty[]{RADIUS};
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(RADIUS, Integer.valueOf((meta & 7) + 1));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(RADIUS)).intValue() - 1;
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
      if (state instanceof IExtendedBlockState) {
         IExtendedBlockState retval = (IExtendedBlockState)state;
         int thisRadius = this.getRadius(state);
         retval = retval.withProperty(GROUNDED, Boolean.valueOf(world.isSideSolid(pos.down(), EnumFacing.UP, false)));

         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockSurfaceRoot.RootConnection conn = this.getSideConnectionRadius(world, pos, thisRadius, dir);
            if (conn != null) {
               int horIndex = dir.getHorizontalIndex();
               retval = retval.withProperty(CONNECTIONS[horIndex], Integer.valueOf(conn.radius)).withProperty(LEVELS[horIndex], conn.level);
            }
         }

         return retval;
      } else {
         return state;
      }
   }

   public int getRadius(IBlockState blockState) {
      return blockState.getBlock() == this ? ((Integer)blockState.getValue(RADIUS)).intValue() : 0;
   }

   public int setRadius(World world, BlockPos pos, int radius, EnumFacing originDir, int flags) {
      world.setBlockState(pos, this.getStateForRadius(radius), flags);
      return radius;
   }

   public IBlockState getStateForRadius(int radius) {
      return this.getDefaultState().withProperty(RADIUS, Integer.valueOf(MathHelper.clamp(radius, 0, this.getMaxRadius())));
   }

   public int getMaxRadius() {
      return 8;
   }

   public int getRadialHeight(int radius) {
      return radius * 2;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
      if (state.getBlock() != this) {
         return NULL_AABB;
      } else {
         AxisAlignedBB trunkBB = null;

         for(CoordUtils.Surround dir : sidesFirst) {
            BlockPos dPos = pos.add(dir.getOffset());
            IBlockState testState = blockAccess.getBlockState(dPos);
            if (testState.getBlock() instanceof BlockBranchThick) {
               BlockBranchThick trunk = (BlockBranchThick)testState.getBlock();
               trunkBB = trunk.getBoundingBox(testState, blockAccess, dPos).offset(dir.getOffsetPos()).intersect(FULL_BLOCK_AABB);
               break;
            }
         }

         int thisRadius = this.getRadius(state);
         int radialHeight = this.getRadialHeight(thisRadius);
         double radius = (double)thisRadius / 16.0D;
         double gap = 0.5D - radius;
         AxisAlignedBB aabb = new AxisAlignedBB(-radius, 0.0D, -radius, radius, (double)radialHeight / 16.0D, radius);

         for(EnumFacing dir : EnumFacing.VALUES) {
            BlockSurfaceRoot.RootConnection conn = this.getSideConnectionRadius(blockAccess, pos, thisRadius, dir);
            if (conn != null) {
               aabb = aabb.expand((double)dir.getFrontOffsetX() * gap, (double)dir.getFrontOffsetY() * gap, (double)dir.getFrontOffsetZ() * gap);
               if (conn.level == BlockSurfaceRoot.ConnectionLevel.HIGH) {
                  aabb = aabb.setMaxY(1.0D + (double)radialHeight / 16.0D);
               }
            }
         }

         aabb = aabb.offset(0.5D, 0.0D, 0.5D);
         return trunkBB != null ? trunkBB.union(aabb) : aabb;
      }
   }

   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
      if (!(entityIn instanceof EntityFallingTree)) {
         boolean connectionMade = false;
         int thisRadius = this.getRadius(state);

         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockSurfaceRoot.RootConnection conn = this.getSideConnectionRadius(world, pos, thisRadius, dir);
            if (conn != null) {
               connectionMade = true;
               int r = MathHelper.clamp(conn.radius, 1, thisRadius);
               double radius = (double)r / 16.0D;
               double radialHeight = (double)this.getRadialHeight(r) / 16.0D;
               double gap = 0.5D - radius;
               AxisAlignedBB aabb = new AxisAlignedBB(-radius, 0.0D, -radius, radius, radialHeight, radius);
               aabb = aabb.expand((double)dir.getFrontOffsetX() * gap, 0.0D, (double)dir.getFrontOffsetZ() * gap).offset(0.5D, 0.0D, 0.5D);
               addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
            }
         }

         if (!connectionMade) {
            double radius = (double)thisRadius / 16.0D;
            double radialHeight = (double)this.getRadialHeight(thisRadius) / 16.0D;
            AxisAlignedBB aabb = new AxisAlignedBB(0.5D - radius, 0.0D, 0.5D - radius, 0.5D + radius, radialHeight, 0.5D + radius);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
         }

      }
   }

   protected BlockSurfaceRoot.RootConnection getSideConnectionRadius(IBlockAccess blockAccess, BlockPos pos, int radius, EnumFacing side) {
      if (side.getAxis().isHorizontal()) {
         BlockPos dPos = pos.offset(side);
         IBlockState blockState = blockAccess.getBlockState(dPos);
         IBlockState upState = blockAccess.getBlockState(pos.up());
         BlockSurfaceRoot.ConnectionLevel level = upState.getBlock() == Blocks.AIR && blockState.isNormalCube() ? BlockSurfaceRoot.ConnectionLevel.HIGH : (blockState.getBlock() == Blocks.AIR ? BlockSurfaceRoot.ConnectionLevel.LOW : BlockSurfaceRoot.ConnectionLevel.MID);
         if (level != BlockSurfaceRoot.ConnectionLevel.MID) {
            dPos = dPos.up(level.yOffset);
            blockState = blockAccess.getBlockState(dPos);
         }

         if (blockState.getBlock() instanceof BlockSurfaceRoot) {
            return new BlockSurfaceRoot.RootConnection(level, ((BlockSurfaceRoot)blockState.getBlock()).getRadius(blockState));
         }

         if (level == BlockSurfaceRoot.ConnectionLevel.MID && TreeHelper.isBranch(blockState) && TreeHelper.getTreePart(blockState).getRadius(blockState) >= 8) {
            return new BlockSurfaceRoot.RootConnection(BlockSurfaceRoot.ConnectionLevel.MID, 8);
         }
      }

      return null;
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState upstate = worldIn.getBlockState(pos.up());
      if (upstate.getBlock() == ModBlocks.blockTrunkShell) {
         worldIn.setBlockState(pos, upstate);
      }

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         BlockPos dPos = pos.offset(dir).down();
         worldIn.getBlockState(dPos).neighborChanged(worldIn, dPos, this, pos);
      }

   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos neighbor) {
      if (!this.canBlockStay(world, pos, state)) {
         world.setBlockToAir(pos);
      }

   }

   protected boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      BlockPos below = pos.down();
      IBlockState belowState = world.getBlockState(below);
      int thisRadius = this.getRadius(state);
      if (belowState.isNormalCube()) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockSurfaceRoot.RootConnection conn = this.getSideConnectionRadius(world, pos, thisRadius, dir);
            if (conn != null && conn.radius > thisRadius) {
               return true;
            }
         }

         return false;
      } else {
         boolean connections = false;

         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockSurfaceRoot.RootConnection conn = this.getSideConnectionRadius(world, pos, thisRadius, dir);
            if (conn != null) {
               if (conn.level == BlockSurfaceRoot.ConnectionLevel.MID) {
                  return false;
               }

               if (conn.radius > thisRadius) {
                  connections = true;
               }
            }
         }

         return connections;
      }
   }

   public static enum ConnectionLevel implements IStringSerializable {
      MID(0),
      LOW(-1),
      HIGH(1);

      private final int yOffset;

      private ConnectionLevel(int y) {
         this.yOffset = y;
      }

      public String getName() {
         return this.toString().toLowerCase();
      }

      public int getYOffset() {
         return this.yOffset;
      }
   }

   public class RootConnection {
      public BlockSurfaceRoot.ConnectionLevel level;
      public int radius;

      public RootConnection(BlockSurfaceRoot.ConnectionLevel level, int radius) {
         this.level = level;
         this.radius = radius;
      }
   }
}
