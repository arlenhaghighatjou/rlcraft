package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DarkOakLogic implements IGrowthLogicKit {
   public DarkOakLogic() {
   }

   public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
      probMap[EnumFacing.UP.getIndex()] = 4;
      if (!signal.isInTrunk()) {
         probMap[EnumFacing.UP.getIndex()] = 0;
         probMap[EnumFacing.DOWN.getIndex()] = 0;
         int var10001 = signal.dir.ordinal();
         probMap[var10001] = (int)((double)probMap[var10001] * 0.35D);
      }

      float energyRatio = (float)signal.delta.getY() / species.getEnergy(world, pos);
      float spreadPush = energyRatio * 2.0F;
      spreadPush = spreadPush < 1.0F ? 1.0F : spreadPush;

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         int var22 = dir.ordinal();
         probMap[var22] = (int)((float)probMap[var22] * spreadPush);
      }

      if (signal.numTurns == 1 && signal.delta.distanceSq(0.0D, (double)signal.delta.getY(), 0.0D) == 1.0D) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            if (signal.dir != dir) {
               probMap[dir.ordinal()] = 0;
            }
         }
      }

      if (signal.isInTrunk()) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            if (probMap[dir.ordinal()] >= 7) {
               probMap[dir.ordinal()] = 2;
            }
         }

         if (signal.delta.getY() > species.getLowestBranchHeight() + 5) {
            probMap[EnumFacing.UP.ordinal()] = 0;
            signal.energy = 2.0F;
         }
      }

      return probMap;
   }

   public EnumFacing newDirectionSelected(Species species, EnumFacing newDir, GrowSignal signal) {
      return newDir;
   }

   public float getEnergy(World world, BlockPos pos, Species species, float signalEnergy) {
      return signalEnergy;
   }
}
