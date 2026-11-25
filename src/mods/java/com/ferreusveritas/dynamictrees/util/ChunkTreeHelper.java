package com.ferreusveritas.dynamictrees.util;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockFruit;
import com.ferreusveritas.dynamictrees.blocks.BlockFruitCocoa;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeCollector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkTreeHelper {
   private static final int chunkWidth = 16;
   private static final byte NONE = 0;
   private static final byte TREE = 1;
   private static final byte SURR = 2;

   public ChunkTreeHelper() {
   }

   public static void removeOrphanedBranchNodes(World world, ChunkPos cPos, int radius) {
      if (cPos == null) {
         throw new NullPointerException("Null Chunk Position");
      } else {
         Set<BlockPos> found = new HashSet();
         BlockBounds bounds = getEffectiveBlockBounds(world, cPos, radius);

         for(MutableBlockPos pos : bounds.iterate()) {
            if (!found.contains(pos)) {
               IBlockState state = world.getBlockState(pos);
               Optional<BlockBranch> branchBlock = TreeHelper.getBranchOpt(state);
               if (branchBlock.isPresent()) {
                  BlockPos rootPos = TreeHelper.findRootNode(world, pos);
                  if (rootPos == BlockPos.ORIGIN) {
                     doTreeDestroy(world, branchBlock, rootPos);
                  } else {
                     IBlockState rootyState = world.getBlockState(rootPos);
                     Optional<BlockRooty> rootyBlock = TreeHelper.getRootyOpt(rootyState);
                     if (rootyBlock.isPresent()) {
                        EnumFacing trunkDir = ((BlockRooty)rootyBlock.get()).getTrunkDirection(world, rootPos);
                        BlockPos trunkPos = rootPos.offset(trunkDir);
                        IBlockState trunkState = world.getBlockState(trunkPos);
                        Optional<BlockBranch> trunk = TreeHelper.getBranchOpt(trunkState);
                        if (trunk.isPresent()) {
                           MapSignal signal = new MapSignal();
                           signal.destroyLoopedNodes = false;
                           ((BlockBranch)trunk.get()).analyse(trunkState, world, trunkPos, (EnumFacing)null, signal);
                           if (!signal.multiroot && !signal.overflow) {
                              ((BlockBranch)trunk.get()).analyse(trunkState, world, trunkPos, (EnumFacing)null, new MapSignal(new INodeInspector[]{new NodeCollector(found)}));
                           } else {
                              doTreeDestroy(world, branchBlock, pos);
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public static void removeAllBranchesFromChunk(World world, ChunkPos cPos, int radius) {
      if (cPos == null) {
         throw new NullPointerException("Null Chunk Position");
      } else {
         BlockBounds bounds = getEffectiveBlockBounds(world, cPos, radius);

         for(MutableBlockPos pos : bounds.iterate()) {
            IBlockState state = world.getBlockState(pos);
            Optional<BlockBranch> branchBlock = TreeHelper.getBranchOpt(state);
            if (branchBlock.isPresent()) {
               doTreeDestroy(world, branchBlock, pos);
            }
         }

      }
   }

   public static BlockBounds getEffectiveBlockBounds(World world, ChunkPos cPos, int radius) {
      Chunk chunk = world.getChunkFromChunkCoords(cPos.x, cPos.z);
      BlockBounds bounds = new BlockBounds(world, cPos);
      bounds.shrink(EnumFacing.UP, world.getHeight() - 1 - (chunk.getTopFilledSegment() + 16));

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         bounds.expand(dir, radius * 16);
      }

      return bounds;
   }

   private static void doTreeDestroy(World world, Optional<BlockBranch> branchBlock, BlockPos pos) {
      BranchDestructionData destroyData = ((BlockBranch)branchBlock.get()).destroyBranchFromNode(world, pos, EnumFacing.DOWN, true);
      destroyData.leavesDrops.clear();
      EntityFallingTree.dropTree(world, destroyData, new ArrayList(0), EntityFallingTree.DestroyType.ROOT);
      cleanupNeighbors(world, destroyData);
   }

   public static void cleanupNeighbors(World world, BranchDestructionData destroyData) {
      if (!world.isRemote) {
         BlockBounds treeBounds = new BlockBounds(destroyData.cutPos);
         destroyData.getPositions(BranchDestructionData.PosType.LEAVES, true).forEach((pos) -> {
            treeBounds.union(pos);
         });
         destroyData.getPositions(BranchDestructionData.PosType.BRANCHES, true).forEach((pos) -> {
            treeBounds.union(pos);
         });
         treeBounds.expand(1);
         SimpleVoxmap treeVoxmap = new SimpleVoxmap(treeBounds);
         destroyData.getPositions(BranchDestructionData.PosType.LEAVES, true).forEach((pos) -> {
            treeVoxmap.setVoxel(pos, (byte)1);
         });
         destroyData.getPositions(BranchDestructionData.PosType.BRANCHES, true).forEach((pos) -> {
            treeVoxmap.setVoxel(pos, (byte)1);
         });
         SimpleVoxmap outlineVoxmap = new SimpleVoxmap(treeVoxmap);
         treeVoxmap.getAllNonZero((byte)1).forEach((pos) -> {
            for(EnumFacing dir : EnumFacing.VALUES) {
               outlineVoxmap.setVoxel(pos.add(dir.getDirectionVec()), (byte)2);
            }

         });
         treeVoxmap.getAllNonZero((byte)1).forEach((pos) -> {
            outlineVoxmap.setVoxel(pos, (byte)0);
         });
         outlineVoxmap.getAllNonZero((byte)2).forEach((pos) -> {
            cleanupBlock(world, pos);
         });
      }
   }

   public static void cleanupBlock(World world, BlockPos pos) {
      IBlockState blockstate = world.getBlockState(pos);
      if (blockstate != ModBlocks.blockStates.air) {
         Block block = blockstate.getBlock();
         if (!(block instanceof BlockSnow) && !(block instanceof BlockFruit) && !(block instanceof BlockFruitCocoa) && !(block instanceof BlockSurfaceRoot)) {
            if (block instanceof BlockVine) {
               cleanupVines(world, pos);
            }
         } else {
            world.setBlockState(pos, ModBlocks.blockStates.air, 2);
         }

      }
   }

   public static void cleanupVines(World world, BlockPos pos) {
      MutableBlockPos mblock = new MutableBlockPos(pos);

      while(world.getBlockState(mblock).getBlock() instanceof BlockVine) {
         world.setBlockState(mblock, ModBlocks.blockStates.air, 2);
         mblock.move(EnumFacing.DOWN);
      }

   }
}
