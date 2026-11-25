package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.blocks.NullTreePart;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeTwinkle;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.Deprecatron;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TreeHelper {
   public static final ITreePart nullTreePart = new NullTreePart();

   public TreeHelper() {
   }

   public static void growPulse(World world, BlockPos rootPos) {
      IBlockState rootyState = world.getBlockState(rootPos);
      BlockRooty dirt = getRooty(rootyState);
      if (dirt != null) {
         dirt.updateTree(rootyState, world, rootPos, world.rand, false);
         ageVolume(world, rootPos, 8, 32, 1, SafeChunkBounds.ANY);
      }

   }

   public static void ageVolume(World world, SimpleVoxmap leafMap, int iterations, SafeChunkBounds safeBounds) {
      SimpleVoxmap iterMap = leafMap != null ? new SimpleVoxmap(leafMap) : null;
      Iterable<MutableBlockPos> iterable = iterMap.getAllNonZero();

      for(int i = 0; i < iterations; ++i) {
         for(MutableBlockPos iPos : iterable) {
            IBlockState blockState = world.getBlockState(iPos);
            Block block = blockState.getBlock();
            if (block instanceof BlockDynamicLeaves) {
               int prevHydro = leafMap.getVoxel(iPos);
               int newHydro = ((IAgeable)block).age(world, iPos, blockState, world.rand, safeBounds);
               if (newHydro == -1) {
                  leafMap.setVoxel(iPos, (byte)0);
                  iterMap.setVoxel(iPos, (byte)0);
               } else if (prevHydro == newHydro) {
                  iterMap.setVoxel(iPos, (byte)0);
               } else {
                  leafMap.setVoxel(iPos, (byte)newHydro);
                  iterMap.setVoxel(iPos, (byte)newHydro);

                  for(EnumFacing dir : EnumFacing.values()) {
                     BlockPos dPos = iPos.offset(dir);
                     iterMap.setVoxel(dPos, leafMap.getVoxel(dPos));
                  }
               }
            } else if (block instanceof IAgeable) {
               ((IAgeable)block).age(world, iPos, blockState, world.rand, safeBounds);
            } else {
               leafMap.setVoxel(iPos, (byte)0);
               iterMap.setVoxel(iPos, (byte)0);
            }
         }
      }

   }

   public static void ageVolume(World world, BlockPos treePos, int halfWidth, int height, int iterations, SafeChunkBounds safeBounds) {
      Iterable<MutableBlockPos> iterable = BlockPos.getAllInBoxMutable(treePos.add(new BlockPos(-halfWidth, 0, -halfWidth)), treePos.add(new BlockPos(halfWidth, height, halfWidth)));

      for(int i = 0; i < iterations; ++i) {
         for(MutableBlockPos iPos : iterable) {
            IBlockState blockState = world.getBlockState(iPos);
            Block block = blockState.getBlock();
            if (block instanceof IAgeable) {
               ((IAgeable)block).age(world, iPos, blockState, world.rand, safeBounds);
            }
         }
      }

   }

   public static Optional<JoCode> getJoCode(World world, BlockPos pos) {
      return getJoCode(world, pos, EnumFacing.SOUTH);
   }

   public static Optional<JoCode> getJoCode(World world, BlockPos pos, Species species) {
      return getJoCode(world, pos, EnumFacing.SOUTH, species);
   }

   public static Optional<JoCode> getJoCode(World world, BlockPos pos, EnumFacing facing) {
      return getJoCode(world, pos, facing, Species.NULLSPECIES);
   }

   public static Optional<JoCode> getJoCode(World world, BlockPos pos, EnumFacing facing, Species species) {
      if (pos == null) {
         return Optional.empty();
      } else {
         BlockPos rootPos = findRootNode(world, pos);
         return rootPos != BlockPos.ORIGIN ? Optional.of(species.isValid() ? species.getJoCode(world, rootPos, facing) : new JoCode(world, rootPos, facing)) : Optional.empty();
      }
   }

   public static BlockPos dereferenceTrunkShell(World world, BlockPos pos) {
      IBlockState blockState = world.getBlockState(pos);
      if (blockState.getBlock() == ModBlocks.blockTrunkShell) {
         BlockTrunkShell.ShellMuse muse = ((BlockTrunkShell)blockState.getBlock()).getMuse(world, blockState, pos);
         if (muse != null) {
            return muse.pos;
         }
      }

      return pos;
   }

   public static Species getCommonSpecies(World world, BlockPos pos) {
      pos = dereferenceTrunkShell(world, pos);
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() instanceof BlockBranch) {
         BlockBranch branch = (BlockBranch)state.getBlock();
         return branch.getFamily().getCommonSpecies();
      } else {
         return Species.NULLSPECIES;
      }
   }

   public static Species getExactSpecies(World world, BlockPos pos) {
      BlockPos rootPos = findRootNode(world, pos);
      if (rootPos != BlockPos.ORIGIN) {
         IBlockState rootyState = world.getBlockState(rootPos);
         return getRooty(rootyState).getSpecies(rootyState, world, rootPos);
      } else {
         return Species.NULLSPECIES;
      }
   }

   /** @deprecated */
   @Deprecated
   public static Species getExactSpecies(IBlockState unused, World world, BlockPos pos) {
      Deprecatron.Complain("getExactSpecies", "Deprecated use of getExactSpecies method. Will be removed in the future.");
      return getExactSpecies(world, pos);
   }

   public static Species getBestGuessSpecies(World world, BlockPos pos) {
      Species species = getExactSpecies(world, pos);
      return species == Species.NULLSPECIES ? getCommonSpecies(world, pos) : species;
   }

   /** @deprecated */
   @Deprecated
   public static BlockPos findRootNode(IBlockState state, World world, BlockPos pos) {
      return findRootNode(world, pos);
   }

   public static BlockPos findRootNode(World world, BlockPos pos) {
      pos = dereferenceTrunkShell(world, pos);
      IBlockState state = world.getBlockState(pos);
      ITreePart treePart = getTreePart(state);
      switch(treePart.getTreePartType()) {
      case BRANCH:
         MapSignal signal = treePart.analyse(state, world, pos, (EnumFacing)null, new MapSignal());
         if (signal.found) {
            return signal.root;
         }

         return BlockPos.ORIGIN;
      case ROOT:
         return pos;
      default:
         return BlockPos.ORIGIN;
      }
   }

   public static void setCustomRootBlockDecay(ICustomRootDecay decay) {
      BlockRooty.customRootDecay = decay;
   }

   public static ICustomRootDecay getCustomRootBlockDecay() {
      return BlockRooty.customRootDecay;
   }

   public static void treeParticles(World world, BlockPos rootPos, EnumParticleTypes type, int num) {
      if (world.isRemote) {
         startAnalysisFromRoot(world, rootPos, new MapSignal(new INodeInspector[]{new NodeTwinkle(type, num)}));
      }

   }

   public static boolean startAnalysisFromRoot(World world, BlockPos rootPos, MapSignal signal) {
      BlockRooty dirt = getRooty(world.getBlockState(rootPos));
      if (dirt != null) {
         dirt.startAnalysis(world, rootPos, signal);
         return true;
      } else {
         return false;
      }
   }

   public static final boolean isTreePart(Block block) {
      return block instanceof ITreePart;
   }

   public static final boolean isTreePart(IBlockState blockState) {
      return isTreePart(blockState.getBlock());
   }

   public static final boolean isTreePart(IBlockAccess blockAccess, BlockPos pos) {
      return isTreePart(blockAccess.getBlockState(pos).getBlock());
   }

   public static final ITreePart getTreePart(Block block) {
      return isTreePart(block) ? (ITreePart)block : nullTreePart;
   }

   public static final ITreePart getTreePart(IBlockState blockState) {
      return getTreePart(blockState.getBlock());
   }

   public static final boolean isBranch(Block block) {
      return block instanceof BlockBranch;
   }

   public static final boolean isBranch(IBlockState state) {
      return state != null && isBranch(state.getBlock());
   }

   public static final BlockBranch getBranch(Block block) {
      return isBranch(block) ? (BlockBranch)block : null;
   }

   public static final BlockBranch getBranch(ITreePart treepart) {
      return treepart instanceof BlockBranch ? (BlockBranch)treepart : null;
   }

   public static final BlockBranch getBranch(IBlockState state) {
      return getBranch(state.getBlock());
   }

   public static final int getRadius(IBlockAccess access, BlockPos pos) {
      IBlockState state = access.getBlockState(pos);
      return getTreePart(state).getRadius(state);
   }

   public static final Optional<BlockBranch> getBranchOpt(Block block) {
      return isBranch(block) ? Optional.of((BlockBranch)block) : Optional.empty();
   }

   public static final Optional<BlockBranch> getBranchOpt(IBlockState state) {
      Block block = state.getBlock();
      return isBranch(block) ? Optional.of((BlockBranch)block) : Optional.empty();
   }

   public static final Optional<BlockBranch> getBranchOpt(ITreePart treepart) {
      return treepart instanceof BlockBranch ? Optional.of((BlockBranch)treepart) : Optional.empty();
   }

   public static final boolean isLeaves(Block block) {
      return block instanceof BlockDynamicLeaves;
   }

   public static final boolean isLeaves(IBlockState blockState) {
      return isLeaves(blockState.getBlock());
   }

   public static final BlockDynamicLeaves getLeaves(Block block) {
      return isLeaves(block) ? (BlockDynamicLeaves)block : null;
   }

   public static final BlockDynamicLeaves getLeaves(ITreePart treepart) {
      return treepart instanceof BlockDynamicLeaves ? (BlockDynamicLeaves)treepart : null;
   }

   public static final BlockDynamicLeaves getLeaves(IBlockState state) {
      return getLeaves(state.getBlock());
   }

   public static final boolean isRooty(Block block) {
      return block instanceof BlockRooty;
   }

   public static final boolean isRooty(IBlockState blockState) {
      return isRooty(blockState.getBlock());
   }

   public static final BlockRooty getRooty(Block block) {
      return isRooty(block) ? (BlockRooty)block : null;
   }

   public static final BlockRooty getRooty(ITreePart treepart) {
      return treepart instanceof BlockRooty ? (BlockRooty)treepart : null;
   }

   public static final BlockRooty getRooty(IBlockState blockState) {
      return getRooty(blockState.getBlock());
   }

   public static final Optional<BlockRooty> getRootyOpt(IBlockState blockState) {
      Block block = blockState.getBlock();
      return isRooty(block) ? Optional.of((BlockRooty)block) : Optional.empty();
   }
}
