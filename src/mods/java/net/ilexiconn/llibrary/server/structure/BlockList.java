package net.ilexiconn.llibrary.server.structure;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.state.IBlockState;

public class BlockList {
   private final float[] probabilities;
   private final IBlockState[] states;

   public BlockList(IBlockState blockState) {
      this(new IBlockState[]{blockState}, new float[]{1.0F});
   }

   public BlockList(IBlockState[] blockStates, float[] probabilities) {
      this.states = blockStates;
      this.probabilities = probabilities;
   }

   public IBlockState getRandom(Random rand) {
      float chosen = rand.nextFloat();
      IBlockState result = null;

      for(int index = 0; chosen >= 0.0F; ++index) {
         if (index >= this.states.length) {
            return null;
         }

         chosen -= this.probabilities[index];
         result = this.states[index];
      }

      return result;
   }

   public BlockList copy() {
      IBlockState[] newStates = (IBlockState[])Arrays.copyOf(this.states, this.states.length);
      float[] newProbabilities = Arrays.copyOf(this.probabilities, this.probabilities.length);
      return new BlockList(newStates, newProbabilities);
   }

   public IBlockState[] getStates() {
      return this.states;
   }

   public float[] getProbabilities() {
      return this.probabilities;
   }
}
