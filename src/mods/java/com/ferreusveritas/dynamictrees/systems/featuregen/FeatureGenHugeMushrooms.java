package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenHugeMushrooms implements IPostGenFeature {
   protected final FeatureGenHugeMushroom mushGen;
   protected int maxShrooms;
   protected int maxAttempts;

   public FeatureGenHugeMushrooms(FeatureGenHugeMushroom mushGen) {
      this.maxShrooms = 2;
      this.maxAttempts = 4;
      this.mushGen = mushGen;
   }

   public FeatureGenHugeMushrooms() {
      this(new FeatureGenHugeMushroom());
   }

   public FeatureGenHugeMushrooms setMaxShrooms(int max) {
      this.maxShrooms = max;
      return this;
   }

   public FeatureGenHugeMushrooms setMaxAttempts(int max) {
      this.maxAttempts = max;
      return this;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      boolean worldGen = safeBounds != SafeChunkBounds.ANY;
      if (!endPoints.isEmpty() && worldGen) {
         BlockPos lowest = (BlockPos)Collections.min(endPoints, (a, b) -> {
            return (boolean)(a.getY() - b.getY());
         });
         Random rand = world.rand;
         int success = 0;
         if (radius >= 5) {
            for(int tries = 0; tries < this.maxAttempts; ++tries) {
               float angle = (float)((double)rand.nextFloat() * 3.141592653589793D * 2.0D);
               int xOff = (int)(MathHelper.sin(angle) * (float)(radius - 1));
               int zOff = (int)(MathHelper.cos(angle) * (float)(radius - 1));
               BlockPos mushPos = rootPos.add(xOff, 0, zOff);
               mushPos = CoordUtils.findGround(world, new BlockPos(mushPos)).up();
               if (safeBounds.inBounds(mushPos, true)) {
                  int maxHeight = lowest.getY() - mushPos.getY();
                  if (maxHeight >= 2) {
                     int height = MathHelper.clamp(rand.nextInt(maxHeight) + 3, 3, maxHeight);
                     if (this.mushGen.setHeight(height).generate(world, mushPos.down(), species, biome, rand, radius, safeBounds)) {
                        ++success;
                        if (success >= this.maxShrooms) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
