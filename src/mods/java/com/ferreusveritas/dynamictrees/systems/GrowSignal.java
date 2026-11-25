package com.ferreusveritas.dynamictrees.systems;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GrowSignal {
   public float energy;
   public EnumFacing dir;
   public int numTurns;
   public int numSteps;
   private final Species species;
   public BlockPos rootPos;
   public BlockPos delta;
   public float radius;
   public float tapering;
   public boolean success;
   public boolean choked;
   public Random rand;

   public GrowSignal(Species species, BlockPos rootPos, float energy) {
      this.species = species;
      this.energy = energy;
      this.dir = EnumFacing.UP;
      this.radius = 0.0F;
      this.numTurns = 0;
      this.numSteps = 0;
      this.tapering = 0.3F;
      this.rand = new Random();
      this.success = true;
      this.choked = false;
      this.rootPos = rootPos;
      this.delta = new BlockPos(0, 0, 0);
   }

   public Species getSpecies() {
      return this.species;
   }

   public boolean step() {
      ++this.numSteps;
      this.delta = this.delta.offset(this.dir);
      if (--this.energy <= 0.0F) {
         this.success = false;
      }

      return this.success;
   }

   public boolean doTurn(EnumFacing targetDir) {
      if (this.dir != targetDir) {
         this.dir = targetDir;
         ++this.numTurns;
         return true;
      } else {
         return false;
      }
   }

   public boolean isInTrunk() {
      return this.numTurns == 0;
   }
}
