package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.IPostGrowFeature;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeFindEnds;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class FeatureGenPodzol implements IPostGrowFeature {
   public FeatureGenPodzol() {
   }

   public boolean postGrow(World world, BlockPos rootPos, BlockPos treePos, Species species, int soilLife, boolean natural) {
      if (ModConfigs.podzolGen) {
         NodeFindEnds endFinder = new NodeFindEnds();
         TreeHelper.startAnalysisFromRoot(world, rootPos, new MapSignal(new INodeInspector[]{endFinder}));
         List<BlockPos> endPoints = endFinder.getEnds();
         if (!endPoints.isEmpty()) {
            Random random = world.rand;
            BlockPos pos = (BlockPos)endPoints.get(random.nextInt(endPoints.size()));
            int x = pos.getX() + random.nextInt(5) - 2;
            int z = pos.getZ() + random.nextInt(5) - 2;
            int darkThreshold = 4;

            for(int i = 0; i < 32; ++i) {
               BlockPos offPos = new BlockPos(x, pos.getY() - 1 - i, z);
               if (!world.isAirBlock(offPos)) {
                  Block block = world.getBlockState(offPos).getBlock();
                  if (!(block instanceof BlockBranch) && !(block instanceof BlockMushroom) && !(block instanceof BlockLeaves)) {
                     if (!(block instanceof BlockFlower) && !(block instanceof BlockTallGrass) && !(block instanceof BlockDoublePlant)) {
                        if (block == Blocks.DIRT || block == Blocks.GRASS) {
                           if (world.getLightFor(EnumSkyBlock.SKY, offPos.up()) <= 4) {
                              world.setBlockState(offPos, ModBlocks.blockStates.podzol);
                           } else {
                              spreadPodzol(world, pos);
                           }
                        }
                        break;
                     }

                     if (world.getLightFor(EnumSkyBlock.SKY, offPos) <= 4) {
                        world.setBlockToAir(pos);
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public static void spreadPodzol(World world, BlockPos pos) {
      int Podzolish = 0;

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         BlockPos deltaPos = pos.offset(dir);
         Block testBlock = world.getBlockState(deltaPos).getBlock();
         Podzolish = Podzolish + (testBlock == Blocks.DIRT && world.getBlockState(deltaPos).getValue(BlockDirt.VARIANT) == DirtType.PODZOL ? 1 : 0);
         Podzolish = Podzolish + (testBlock == ModBlocks.blockRootyDirt ? 1 : 0);
         if (Podzolish >= 3) {
            world.setBlockState(pos, ModBlocks.blockStates.podzol);
            break;
         }
      }

   }
}
