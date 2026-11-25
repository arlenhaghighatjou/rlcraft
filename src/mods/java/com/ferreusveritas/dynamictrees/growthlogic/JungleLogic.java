package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JungleLogic implements IGrowthLogicKit {
   private static final int CANOPY_HEIGHT = 14;
   private static final int MEGA_CANOPY_HEIGHT = 25;
   private static final int BRANCH_OUT_CHANCE = 5;
   private static final int MEGA_BRANCH_OUT_CHANCE = 3;

   public JungleLogic() {
   }

   public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
      EnumFacing originDir = signal.dir.getOpposite();
      int treeHash = CoordUtils.coordHashCode(signal.rootPos, 2);
      int posHash = CoordUtils.coordHashCode(pos, 2);
      probMap[0] = 0;
      probMap[1] = signal.isInTrunk() ? species.getUpProbability() : 0;
      probMap[2] = probMap[3] = probMap[4] = probMap[5] = 0;
      boolean branchOut = (signal.numSteps + treeHash) % (species.isMega() ? 3 : 5) == 0;
      int sideTurn = signal.isInTrunk() && (!signal.isInTrunk() || !branchOut || radius <= 1) ? 0 : 2;
      int height = (species.isMega() ? 25 : 14) + treeHash % 7829 % 8;
      if (signal.delta.getY() < height) {
         probMap[2 + posHash % 4] = sideTurn;
      } else {
         probMap[1] = probMap[2] = probMap[3] = probMap[4] = probMap[5] = 2;
      }

      probMap[originDir.ordinal()] = 0;
      int var10001 = signal.dir.ordinal();
      probMap[var10001] += signal.isInTrunk() ? 0 : (signal.numTurns == 1 ? 2 : 1);
      return probMap;
   }

   public EnumFacing newDirectionSelected(Species species, EnumFacing newDir, GrowSignal signal) {
      if (signal.isInTrunk() && newDir != EnumFacing.UP) {
         signal.energy = species.isThick() ? 6.0F : 4.0F;
      }

      return newDir;
   }

   public float getEnergy(World world, BlockPos pos, Species species, float signalEnergy) {
      return signalEnergy * species.biomeSuitability(world, pos);
   }
}
