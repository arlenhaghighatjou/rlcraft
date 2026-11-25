package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConiferLogic implements IGrowthLogicKit {
   private final float energyDivisor;
   private float horizontalLimiter;
   private int heightVariation;

   public ConiferLogic() {
      this(3.0F);
   }

   public ConiferLogic(float energyDivisor) {
      this.horizontalLimiter = 16.0F;
      this.heightVariation = 5;
      this.energyDivisor = energyDivisor;
   }

   public ConiferLogic setHorizontalLimiter(float energyLimiter) {
      this.horizontalLimiter = energyLimiter;
      return this;
   }

   public ConiferLogic setHeightVariation(int heightVariation) {
      this.heightVariation = heightVariation;
      return this;
   }

   public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
      EnumFacing originDir = signal.dir.getOpposite();
      probMap[0] = 0;
      probMap[1] = signal.isInTrunk() ? species.getUpProbability() : 0;
      probMap[2] = probMap[3] = probMap[4] = probMap[5] = signal.isInTrunk() && (!signal.isInTrunk() || signal.numSteps % 2 != 1 || radius <= 1) ? 0 : 2;
      probMap[originDir.ordinal()] = 0;
      int var10001 = signal.dir.ordinal();
      probMap[var10001] += signal.isInTrunk() ? 0 : (signal.numTurns == 1 ? 2 : 1);
      return probMap;
   }

   public EnumFacing newDirectionSelected(Species species, EnumFacing newDir, GrowSignal signal) {
      if (signal.isInTrunk() && newDir != EnumFacing.UP) {
         signal.energy /= this.energyDivisor;
         if (signal.energy > this.horizontalLimiter) {
            signal.energy = this.horizontalLimiter;
         }
      }

      return newDir;
   }

   public float getEnergy(World world, BlockPos pos, Species species, float signalEnergy) {
      long day = world.getTotalWorldTime() / 24000L;
      int month = (int)day / 30;
      return signalEnergy * species.biomeSuitability(world, pos) + (float)(CoordUtils.coordHashCode(pos.up(month), 2) % this.heightVariation);
   }
}
