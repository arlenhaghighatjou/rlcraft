package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.cells.CellMetadata;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockBranchBasic extends BlockBranch {
   private int flammability;
   private int fireSpreadSpeed;
   protected static final PropertyInteger RADIUS = PropertyInteger.create("radius", 1, 8);
   protected IBlockState[] branchStates;

   public BlockBranchBasic(String name) {
      this(Material.WOOD, name);
   }

   public BlockBranchBasic(Material material, String name) {
      super(material, name);
      this.flammability = 5;
      this.fireSpreadSpeed = 5;
      this.setSoundType(SoundType.WOOD);
      this.setHarvestLevel("axe", 0);
      this.cacheBranchStates();
   }

   public void cacheBranchStates() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(RADIUS, Integer.valueOf(1)));
      this.branchStates = new IBlockState[9];
      this.branchStates[0] = Blocks.AIR.getDefaultState();

      for(int radius = 1; radius <= 8; ++radius) {
         this.branchStates[radius] = this.getDefaultState().withProperty(RADIUS, Integer.valueOf(radius));
      }

   }

   public IProperty<?>[] getIgnorableProperties() {
      return new IProperty[]{RADIUS};
   }

   protected BlockStateContainer createBlockState() {
      IProperty[] listedProperties = new IProperty[]{RADIUS};
      return new ExtendedBlockState(this, listedProperties, CONNECTIONS);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(RADIUS, Integer.valueOf((meta & 7) + 1));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(RADIUS)).intValue() - 1;
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
      if (!(state instanceof IExtendedBlockState)) {
         return state;
      } else {
         IExtendedBlockState retval = (IExtendedBlockState)state;
         int thisRadius = this.getRadius(state);

         for(EnumFacing dir : EnumFacing.VALUES) {
            retval = retval.withProperty(CONNECTIONS[dir.getIndex()], Integer.valueOf(this.getSideConnectionRadius(world, pos, thisRadius, dir)));
         }

         return retval;
      }
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return this.isSameTree(branch) ? setSupport(1, 1) : 0;
   }

   public boolean canFall() {
      return true;
   }

   public boolean checkForRot(World world, BlockPos pos, Species species, int radius, Random rand, float chance, boolean rapid) {
      if (rapid || chance != 0.0F && rand.nextFloat() <= chance) {
         int neigh = 0;

         for(EnumFacing dir : EnumFacing.VALUES) {
            BlockPos deltaPos = pos.offset(dir);
            IBlockState deltaBlockState = world.getBlockState(deltaPos);
            neigh += TreeHelper.getTreePart(deltaBlockState).branchSupport(deltaBlockState, world, this, deltaPos, dir, radius);
            if (getBranchSupport(neigh) >= 1 && getLeavesSupport(neigh) >= 2) {
               return false;
            }
         }

         boolean didRot = species.rot(world, pos, neigh & 15, radius, rand, rapid);
         if (rapid && didRot) {
            for(EnumFacing dir : EnumFacing.VALUES) {
               BlockPos neighPos = pos.offset(dir);
               IBlockState neighState = world.getBlockState(neighPos);
               if (neighState.getBlock() == this) {
                  this.checkForRot(world, neighPos, species, this.getRadius(neighState), rand, 1.0F, true);
               }
            }
         }

         return didRot;
      } else {
         return false;
      }
   }

   public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
      int radius = this.getRadius(blockState);
      float hardness = this.getFamily().getPrimitiveLog().getBlock().getBlockHardness(blockState, world, pos) * (float)(radius * radius) / 64.0F * 8.0F;
      hardness = Math.min(hardness, ModConfigs.maxTreeHardness);
      return hardness;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.flammability;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      int radius = this.getRadius(world.getBlockState(pos));
      return this.fireSpreadSpeed * radius / 8;
   }

   public BlockBranchBasic setFlammability(int flammability) {
      this.flammability = flammability;
      return this;
   }

   public BlockBranchBasic setFireSpreadSpeed(int fireSpreadSpeed) {
      this.fireSpreadSpeed = fireSpreadSpeed;
      return this;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return this.getRadius(state) == 8;
   }

   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return this.getRadius(blockState) != 8 || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesProperties) {
      TreeFamily thisTree = this.getFamily();
      if (leavesProperties.getTree() == thisTree) {
         int radiusAndMeta = thisTree.getRadiusForCellKit(world, pos, state, dir, this);
         int radius = CellMetadata.getRadius(radiusAndMeta);
         int metadata = CellMetadata.getMeta(radiusAndMeta);
         return leavesProperties.getCellKit().getCellForBranch(radius, metadata);
      } else {
         return CellNull.NULLCELL;
      }
   }

   public int getRadius(IBlockState state) {
      return state.getBlock() == this ? ((Integer)state.getValue(RADIUS)).intValue() : 0;
   }

   public int setRadius(World world, BlockPos pos, int radius, EnumFacing originDir, int flags) {
      destroyMode = BlockBranch.EnumDestroyMode.SETRADIUS;
      world.setBlockState(pos, this.getStateForRadius(radius), flags);
      destroyMode = BlockBranch.EnumDestroyMode.SLOPPY;
      return radius;
   }

   public IBlockState getStateForRadius(int radius) {
      return this.branchStates[MathHelper.clamp(radius, 0, this.getMaxRadius())];
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return this.isSameTree(from) ? this.getRadius(state) + 2 : 0;
   }

   public GrowSignal growIntoAir(World world, BlockPos pos, GrowSignal signal, int fromRadius) {
      Species species = signal.getSpecies();
      BlockDynamicLeaves leaves = TreeHelper.getLeaves(species.getLeavesProperties().getDynamicLeavesState());
      if (leaves != null) {
         if (fromRadius != 1) {
            return leaves.branchOut(world, pos, signal);
         }

         signal.success = leaves.growLeavesIfLocationIsSuitable(world, species.getLeavesProperties(), pos, 0);
      }

      return signal;
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      if (signal.step()) {
         IBlockState currBlockState = world.getBlockState(pos);
         Species species = signal.getSpecies();
         boolean inTrunk = signal.isInTrunk();
         EnumFacing originDir = signal.dir.getOpposite();
         EnumFacing targetDir = species.selectNewDirection(world, pos, this, signal);
         signal.doTurn(targetDir);
         BlockPos deltaPos = pos.offset(targetDir);
         IBlockState deltaState = world.getBlockState(deltaPos);
         ITreePart treepart = TreeHelper.getTreePart(deltaState);
         if (treepart != TreeHelper.nullTreePart) {
            signal = treepart.growSignal(world, deltaPos, signal);
         } else if (world.isAirBlock(deltaPos) || deltaState.getBlock() == ModBlocks.blockTrunkShell) {
            signal = this.growIntoAir(world, deltaPos, signal, this.getRadius(currBlockState));
         }

         float areaAccum = signal.radius * signal.radius;

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (!dir.equals(originDir) && !dir.equals(targetDir)) {
               BlockPos deltaPos = pos.offset(dir);
               IBlockState blockState = world.getBlockState(deltaPos);
               ITreePart treepart = TreeHelper.getTreePart(blockState);
               if (this.isSameTree(treepart)) {
                  int branchRadius = treepart.getRadius(blockState);
                  areaAccum += (float)(branchRadius * branchRadius);
               }
            }
         }

         if (!signal.choked) {
            int maxRadius = inTrunk ? species.maxBranchRadius() : Math.min(species.maxBranchRadius(), 8);
            signal.radius = MathHelper.clamp((float)Math.sqrt((double)areaAccum) + species.getTapering(), (float)this.getRadius(currBlockState), (float)maxRadius);
            int targetRadius = (int)Math.floor((double)signal.radius);
            int setRad = this.setRadius(world, pos, targetRadius, originDir);
            if (setRad < targetRadius) {
               signal.choked = true;
            }
         }
      }

      return signal;
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return ModConfigs.enableBranchClimbling && this.getRadius(state) <= 3;
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

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (this.getSideConnectionRadius(blockAccess, pos, thisRadius, dir) > 0) {
               connectionMade = true;
               aabb = aabb.expand((double)dir.getFrontOffsetX() * gap, (double)dir.getFrontOffsetY() * gap, (double)dir.getFrontOffsetZ() * gap);
            }
         }

         if (connectionMade) {
            return aabb.offset(0.5D, 0.5D, 0.5D);
         } else {
            return new AxisAlignedBB(0.5D - radius, 0.5D - radius, 0.5D - radius, 0.5D + radius, 0.5D + radius, 0.5D + radius);
         }
      }
   }

   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
      if (!(entityIn instanceof EntityFallingTree)) {
         boolean hasConnections = false;
         int thisRadius = this.getRadius(state);

         for(EnumFacing dir : EnumFacing.VALUES) {
            int connRadius = this.getSideConnectionRadius(world, pos, thisRadius, dir);
            if (connRadius > 0) {
               hasConnections = true;
               double radius = (double)MathHelper.clamp(connRadius, 1, thisRadius) / 16.0D;
               double gap = 0.5D - radius;
               AxisAlignedBB aabb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)).grow(radius);
               aabb = aabb.expand((double)dir.getFrontOffsetX() * gap, (double)dir.getFrontOffsetY() * gap, (double)dir.getFrontOffsetZ() * gap).offset(0.5D, 0.5D, 0.5D);
               addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
            }
         }

         if (!hasConnections) {
            AxisAlignedBB aabb = (new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D)).grow((double)thisRadius);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
         }

      }
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return this.getRadius(state);
   }

   protected int getSideConnectionRadius(IBlockAccess blockAccess, BlockPos pos, int radius, EnumFacing side) {
      BlockPos deltaPos = pos.offset(side);
      IBlockState blockState = blockAccess.getBlockState(deltaPos);
      return TreeHelper.getTreePart(blockState).getRadiusForConnection(blockState, blockAccess, deltaPos, this, side, radius);
   }

   protected int getMaxSignalDepth() {
      return this.getFamily().getMaxSignalDepth();
   }

   public MapSignal analyse(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      if (!signal.overflow && (!signal.trackVisited || !signal.doTrackingVisited(pos))) {
         if (signal.depth++ < this.getMaxSignalDepth()) {
            signal.run(blockState, world, pos, fromDir);

            for(EnumFacing dir : EnumFacing.VALUES) {
               if (dir != fromDir) {
                  BlockPos deltaPos = pos.offset(dir);
                  IBlockState deltaState = world.getBlockState(deltaPos);
                  ITreePart treePart = TreeHelper.getTreePart(deltaState);
                  if (treePart.shouldAnalyse()) {
                     signal = treePart.analyse(deltaState, world, deltaPos, dir.getOpposite(), signal);
                     if (signal.found && signal.localRootDir == null && fromDir == null) {
                        signal.localRootDir = dir;
                     }
                  }
               }
            }

            signal.returnRun(blockState, world, pos, fromDir);
         } else {
            IBlockState state = world.getBlockState(pos);
            if (signal.destroyLoopedNodes && state.getBlock() instanceof BlockBranch) {
               BlockBranch branch = (BlockBranch)state.getBlock();
               branch.breakDeliberate(world, pos, BlockBranch.EnumDestroyMode.OVERFLOW);
            }

            signal.overflow = true;
         }

         --signal.depth;
         return signal;
      } else {
         return signal;
      }
   }
}
