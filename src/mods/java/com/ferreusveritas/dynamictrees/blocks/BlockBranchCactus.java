package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.google.common.base.Predicate;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBranchCactus extends BlockBranch {
   public static final PropertyEnum<EnumFacing> ORIGIN = PropertyEnum.create("origin", EnumFacing.class, new Predicate<EnumFacing>() {
      public boolean apply(@Nullable EnumFacing dir) {
         return dir != EnumFacing.UP;
      }
   });
   public static final PropertyBool TRUNK = PropertyBool.create("trunk");

   public BlockBranchCactus(String name) {
      super(Material.CACTUS, name);
      this.setSoundType(SoundType.CLOTH);
      this.setHarvestLevel("axe", 0);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TRUNK, Boolean.valueOf(true)).withProperty(ORIGIN, EnumFacing.DOWN));
   }

   protected BlockStateContainer createBlockState() {
      IProperty[] listedProperties = new IProperty[]{ORIGIN, TRUNK};
      return new ExtendedBlockState(this, listedProperties, CONNECTIONS);
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing rootDir = EnumFacing.getFront((meta & 7) % 6);
      boolean trunk = rootDir == EnumFacing.UP;
      return this.getDefaultState().withProperty(ORIGIN, trunk ? EnumFacing.DOWN : rootDir).withProperty(TRUNK, Boolean.valueOf(trunk));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(TRUNK)).booleanValue() ? EnumFacing.UP.getIndex() : ((EnumFacing)state.getValue(ORIGIN)).getIndex();
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess blockAcess, BlockPos pos) {
      if (!(state instanceof IExtendedBlockState)) {
         return state;
      } else {
         IExtendedBlockState retval = (IExtendedBlockState)state;
         int thisRadius = this.getRadius(state);

         for(EnumFacing dir : EnumFacing.VALUES) {
            retval = retval.withProperty(CONNECTIONS[dir.getIndex()], Integer.valueOf(this.getSideConnectionRadius(blockAcess, pos, thisRadius, dir)));
         }

         return retval;
      }
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return 0;
   }

   public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
      int radius = this.getRadius(blockState);
      float hardness = this.getFamily().getPrimitiveLog().getBlock().getBlockHardness(blockState, world, pos) * (float)(radius * radius) / 64.0F * 8.0F;
      hardness = Math.min(hardness, ModConfigs.maxTreeHardness);
      return hardness;
   }

   public boolean checkForRot(World world, BlockPos pos, Species species, int radius, EaglercraftRandom rand, float chance, boolean rapid) {
      return false;
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState returnState = this.getDefaultState();
      IBlockState adjState = world.getBlockState(pos.offset(facing.getOpposite()));
      boolean trunk = facing == EnumFacing.UP && (adjState.isSideSolid(world, pos.offset(facing.getOpposite()), facing) || adjState.getBlock() == this && ((Boolean)adjState.getValue(TRUNK)).booleanValue());
      return returnState.withProperty(TRUNK, Boolean.valueOf(trunk)).withProperty(ORIGIN, facing != EnumFacing.DOWN ? facing.getOpposite() : EnumFacing.DOWN);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesProperties) {
      return CellNull.NULLCELL;
   }

   public int getRadius(IBlockState state) {
      return state.getBlock() == this ? (((Boolean)state.getValue(TRUNK)).booleanValue() ? 5 : 4) : 0;
   }

   public int setRadius(World world, BlockPos pos, int radius, EnumFacing originDir, int flags) {
      return radius;
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return this.isSameTree(from) ? this.getRadius(state) + 2 : 0;
   }

   public GrowSignal growIntoAir(World world, BlockPos pos, GrowSignal signal, int fromRadius) {
      EnumFacing originDir = signal.dir.getOpposite();
      boolean trunk = signal.isInTrunk();
      if (originDir.getAxis() == Axis.Y || world.getBlockState(pos.up()).getBlock() != this && world.getBlockState(pos.down()).getBlock() != this) {
         signal.success = world.setBlockState(pos, this.blockState.getBaseState().withProperty(TRUNK, Boolean.valueOf(trunk)).withProperty(ORIGIN, originDir), 2);
         signal.radius = (float)((int)(trunk ? signal.getSpecies().getFamily().getPrimaryThickness() : signal.getSpecies().getFamily().getSecondaryThickness()));
         return signal;
      } else {
         signal.success = false;
         return signal;
      }
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      if (signal.step()) {
         Species species = signal.getSpecies();
         EnumFacing targetDir = species.selectNewDirection(world, pos, this, signal);
         signal.doTurn(targetDir);
         BlockPos deltaPos = pos.offset(targetDir);
         IBlockState deltaState = world.getBlockState(deltaPos);
         ITreePart treepart = TreeHelper.getTreePart(deltaState);
         if (treepart == this) {
            signal = treepart.growSignal(world, deltaPos, signal);
         } else if (world.isAirBlock(deltaPos)) {
            signal = this.growIntoAir(world, deltaPos, signal, (int)signal.radius);
         }
      }

      return signal;
   }

   public IBlockState getStateForRadius(int radius) {
      return this.getDefaultState().withProperty(TRUNK, Boolean.valueOf(radius > 4));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
      if (state.getBlock() != this) {
         return NULL_AABB;
      } else {
         int thisRadius = this.getRadius(state);
         boolean connectionMade = false;
         double radius = (double)thisRadius / 16.0D;
         double gap = 0.5D - radius;
         AxisAlignedBB aabb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)).grow(radius);
         int numConnections = 0;

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (this.getSideConnectionRadius(blockAccess, pos, thisRadius, dir) > 0) {
               connectionMade = true;
               ++numConnections;
               aabb = aabb.expand((double)dir.getFrontOffsetX() * gap, (double)dir.getFrontOffsetY() * gap, (double)dir.getFrontOffsetZ() * gap);
            }
         }

         if (!((Boolean)state.getValue(TRUNK)).booleanValue() && numConnections == 1 && ((EnumFacing)state.getValue(ORIGIN)).getAxis().isHorizontal()) {
            aabb = aabb.expand((double)EnumFacing.UP.getFrontOffsetX() * gap, (double)EnumFacing.UP.getFrontOffsetY() * gap, (double)EnumFacing.UP.getFrontOffsetZ() * gap);
         }

         if (connectionMade) {
            return aabb.offset(0.5D, 0.5D, 0.5D);
         } else {
            return new AxisAlignedBB(0.5D - radius, 0.5D - radius, 0.5D - radius, 0.5D + radius, 0.5D + radius, 0.5D + radius);
         }
      }
   }

   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
      int thisRadius = this.getRadius(state);
      int numConnections = 0;

      for(EnumFacing dir : EnumFacing.VALUES) {
         int connRadius = this.getSideConnectionRadius(world, pos, thisRadius, dir);
         if (connRadius > 0) {
            ++numConnections;
            double radius = (double)MathHelper.clamp(connRadius, 1, thisRadius) / 16.0D;
            double gap = 0.5D - radius;
            AxisAlignedBB aabb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)).grow(radius);
            aabb = aabb.offset((double)dir.getFrontOffsetX() * gap, (double)dir.getFrontOffsetY() * gap, (double)dir.getFrontOffsetZ() * gap).offset(0.5D, 0.5D, 0.5D);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
         }
      }

      if (!((Boolean)state.getValue(TRUNK)).booleanValue() && numConnections == 1 && ((EnumFacing)state.getValue(ORIGIN)).getAxis().isHorizontal()) {
         double radius = (double)MathHelper.clamp(4, 1, thisRadius) / 16.0D;
         double gap = 0.5D - radius;
         AxisAlignedBB aabb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)).grow(radius);
         aabb = aabb.offset((double)EnumFacing.UP.getFrontOffsetX() * gap, (double)EnumFacing.UP.getFrontOffsetY() * gap, (double)EnumFacing.UP.getFrontOffsetZ() * gap).offset(0.5D, 0.5D, 0.5D);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
      }

      double min = 0.5D - (double)thisRadius / 16.0D;
      double max = 0.5D + (double)thisRadius / 16.0D;
      addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(min, min, min, max, max, max));
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return 0;
   }

   protected int getSideConnectionRadius(IBlockAccess blockAccess, BlockPos pos, int radius, EnumFacing side) {
      BlockPos deltaPos = pos.offset(side);
      IBlockState otherState = blockAccess.getBlockState(deltaPos);
      IBlockState state = blockAccess.getBlockState(pos);
      if (otherState.getBlock() == this && state.getBlock() == this && (otherState.getValue(ORIGIN) == side.getOpposite() || state.getValue(ORIGIN) == side)) {
         return ((Boolean)state.getValue(TRUNK)).booleanValue() && ((Boolean)otherState.getValue(TRUNK)).booleanValue() ? 5 : 4;
      } else {
         return side == EnumFacing.DOWN && state.getBlock() == this && ((Boolean)state.getValue(TRUNK)).booleanValue() && state.getValue(ORIGIN) == side ? 5 : 0;
      }
   }

   public MapSignal analyse(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      if (signal.depth++ < 32) {
         IBlockState state = world.getBlockState(pos);
         signal.run(blockState, world, pos, fromDir);

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (dir != fromDir) {
               BlockPos deltaPos = pos.offset(dir);
               IBlockState deltaState = world.getBlockState(deltaPos);
               if (deltaState.getBlock() == this && deltaState.getValue(ORIGIN) == dir.getOpposite()) {
                  signal = ((ITreePart)deltaState.getBlock()).analyse(deltaState, world, deltaPos, dir.getOpposite(), signal);
               } else if (state.getBlock() == this && state.getValue(ORIGIN) == dir) {
                  signal = TreeHelper.getTreePart(deltaState).analyse(deltaState, world, deltaPos, dir.getOpposite(), signal);
               }

               if (signal.found && signal.localRootDir == null && fromDir == null) {
                  signal.localRootDir = dir;
               }
            }
         }

         signal.returnRun(blockState, world, pos, fromDir);
      } else {
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() instanceof BlockBranch) {
            BlockBranch branch = (BlockBranch)state.getBlock();
            branch.breakDeliberate(world, pos, BlockBranch.EnumDestroyMode.OVERFLOW);
         }

         signal.overflow = true;
      }

      --signal.depth;
      return signal;
   }
}
