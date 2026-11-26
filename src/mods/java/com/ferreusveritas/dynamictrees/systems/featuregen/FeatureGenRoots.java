package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.api.IPostGrowFeature;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenRoots implements IPostGrowFeature, IPostGenFeature {
   private int levelLimit = 2;
   private final int minTrunkRadius;
   private BiFunction<Integer, Integer, Integer> scaler = (i, j) -> {
      return i;
   };
   private final SimpleVoxmap[] rootMaps;

   public FeatureGenRoots(int minTrunkRadius) {
      this.minTrunkRadius = minTrunkRadius;
      this.rootMaps = this.createRootMaps();
   }

   protected SimpleVoxmap[] createRootMaps() {
      byte[][] rootData = new byte[][]{{0, 3, 0, 0, 0, 0, 0, 0, 5, 6, 7, 0, 3, 2, 0, 0, 0, 8, 0, 5, 0, 0, 6, 8, 0, 8, 7, 0, 0, 0, 0, 7, 0, 0, 0, 0, 3, 4, 6, 4, 0, 0, 0, 2, 0, 0, 3, 2, 1}, {0, 3, 0, 0, 0, 0, 0, 0, 5, 6, 7, 0, 3, 2, 0, 0, 0, 8, 0, 5, 0, 0, 6, 8, 0, 8, 7, 0, 0, 0, 0, 7, 0, 0, 0, 0, 3, 4, 6, 4, 0, 0, 0, 2, 0, 0, 3, 2, 1}, {0, 0, 2, 0, 0, 0, 0, 3, 4, 6, 0, 0, 0, 0, 1, 0, 7, 8, 0, 0, 0, 0, 0, 0, 0, 7, 6, 0, 0, 0, 0, 8, 0, 5, 4, 0, 5, 6, 7, 0, 0, 2, 2, 4, 0, 0, 0, 0, 0}, {0, 4, 0, 0, 0, 0, 0, 0, 5, 6, 0, 0, 1, 0, 0, 0, 7, 0, 0, 3, 0, 0, 0, 8, 0, 8, 7, 0, 0, 0, 0, 8, 0, 5, 4, 0, 0, 6, 7, 3, 0, 2, 0, 4, 5, 0, 0, 0, 0}, {3, 4, 5, 0, 0, 0, 0, 2, 0, 6, 0, 0, 0, 0, 0, 0, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 7, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 2, 3, 5, 2, 0}, {0, 0, 4, 0, 0, 0, 0, 0, 0, 6, 7, 0, 2, 0, 0, 0, 0, 8, 0, 3, 0, 5, 7, 8, 0, 6, 5, 0, 3, 0, 0, 8, 0, 2, 1, 0, 3, 0, 7, 0, 0, 0, 0, 4, 5, 6, 0, 0, 0}};
      SimpleVoxmap[] maps = new SimpleVoxmap[rootData.length];

      for(int i = 0; i < maps.length; ++i) {
         maps[i] = (new SimpleVoxmap(7, 1, 7, rootData[i])).setCenter(new BlockPos(3, 0, 3));
      }

      return maps;
   }

   public FeatureGenRoots setLevelLimit(int limit) {
      this.levelLimit = limit;
      return this;
   }

   public FeatureGenRoots setScaler(BiFunction<Integer, Integer, Integer> scaler) {
      this.scaler = scaler;
      return this;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      BlockPos treePos = rootPos.up();
      int trunkRadius = TreeHelper.getRadius(world, treePos);
      return trunkRadius >= this.minTrunkRadius ? this.startRoots(world, treePos, species, trunkRadius) : false;
   }

   public boolean startRoots(World world, BlockPos treePos, Species species, int trunkRadius) {
      int hash = CoordUtils.coordHashCode(treePos, 2);
      SimpleVoxmap rootMap = this.rootMaps[hash % this.rootMaps.length];
      this.nextRoot(world, rootMap, treePos, species, trunkRadius, BlockPos.ORIGIN, 0, -1, (EnumFacing)null, 0);
      return true;
   }

   public boolean postGrow(World world, BlockPos rootPos, BlockPos treePos, Species species, int soilLife, boolean natural) {
      int trunkRadius = TreeHelper.getRadius(world, treePos);
      if (soilLife > 0 && trunkRadius >= this.minTrunkRadius) {
         CoordUtils.Surround surr = CoordUtils.Surround.values()[world.rand.nextInt(8)];
         BlockPos dPos = treePos.add(surr.getOffset());
         if (world.getBlockState(dPos).getBlock() instanceof BlockSurfaceRoot) {
            world.setBlockState(dPos, ModBlocks.blockTrunkShell.getDefaultState().withProperty(BlockTrunkShell.COREDIR, surr.getOpposite()));
         }

         this.startRoots(world, treePos, species, trunkRadius);
      }

      return true;
   }

   protected void nextRoot(World world, SimpleVoxmap rootMap, BlockPos trunkPos, Species species, int trunkRadius, BlockPos pos, int height, int levelCount, EnumFacing fromDir, int radius) {
      for(int depth = 0; depth < 2; ++depth) {
         BlockPos currPos = trunkPos.add(pos).up(height - depth);
         IBlockState placeState = world.getBlockState(currPos);
         IBlockState belowState = world.getBlockState(currPos.down());
         boolean onNormalCube = belowState.isNormalCube();
         if (pos == BlockPos.ORIGIN || this.isReplaceableWithRoots(world, placeState, currPos) && (depth == 1 || onNormalCube)) {
            if (radius > 0) {
               species.getFamily().getSurfaceRoots().setRadius(world, currPos, radius, fromDir, 3);
            }

            if (onNormalCube) {
               for(EnumFacing dir : EnumFacing.HORIZONTALS) {
                  if (dir != fromDir) {
                     BlockPos dPos = pos.offset(dir);
                     int nextRad = ((Integer)this.scaler.apply(Integer.valueOf(rootMap.getVoxel(dPos)), Integer.valueOf(trunkRadius))).intValue();
                     if (pos != BlockPos.ORIGIN && nextRad >= radius) {
                        nextRad = radius - 1;
                     }

                     int thisLevelCount = depth == 1 ? 1 : levelCount + 1;
                     if (nextRad > 0 && thisLevelCount <= this.levelLimit) {
                        this.nextRoot(world, rootMap, trunkPos, species, trunkRadius, dPos, height - depth, thisLevelCount, dir.getOpposite(), nextRad);
                     }
                  }
               }
            }
            break;
         }
      }

   }

   protected boolean isReplaceableWithRoots(World world, IBlockState placeState, BlockPos pos) {
      Block block = placeState.getBlock();
      if (block != Blocks.AIR && block != ModBlocks.blockTrunkShell) {
         Material material = placeState.getMaterial();
         return block.isReplaceable(world, pos) && material != Material.WATER && material != Material.LAVA;
      } else {
         return true;
      }
   }
}
