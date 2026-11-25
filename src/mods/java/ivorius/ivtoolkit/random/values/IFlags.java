package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class IFlags implements IValue {
   public int[] flags;
   public double[] chances;
   public int minFlags;

   public IFlags(int minFlags, int[] flags, double[] chances) {
      this.minFlags = minFlags;
      this.flags = flags;
      this.chances = chances;
   }

   public IFlags(int minFlags, Object... flagsWithChances) {
      this.minFlags = minFlags;
      this.flags = new int[flagsWithChances.length / 2];
      this.chances = new double[this.flags.length];

      for(int i = 0; i < this.flags.length; ++i) {
         this.flags[i] = ((Integer)flagsWithChances[i * 2]).intValue();
         this.chances[i] = ((Double)flagsWithChances[i * 2 + 1]).doubleValue();
      }

   }

   public Integer getValue(Random random) {
      int value = 0;
      int flagsSet = 0;

      for(int i = 0; i < this.flags.length; ++i) {
         if (random.nextDouble() < this.chances[i]) {
            value |= 1 << this.flags[i];
            ++flagsSet;
         }
      }

      while(flagsSet < this.minFlags) {
         int[] values = new int[this.flags.length - flagsSet];
         double[] weights = new double[values.length];
         int currentIndex = 0;

         for(int i = 0; i < values.length; ++i) {
            while((flagsSet & 1 << this.flags[currentIndex]) > 0) {
               ++currentIndex;
            }

            values[i] = this.flags[currentIndex];
            weights[i] = this.chances[currentIndex];
            ++currentIndex;
         }

         value |= 1 << getRandomValue(random, values, weights);
         ++flagsSet;
      }

      return value;
   }

   public static int getRandomValue(Random random, int[] values, double[] weights) {
      double total = 0.0D;

      for(double weight : weights) {
         total += weight;
      }

      double selected = random.nextDouble() * total;

      for(int i = 0; i < weights.length; ++i) {
         selected -= weights[i];
         if (selected < 0.0D) {
            return values[i];
         }
      }

      throw new RuntimeException("Weights have invalid values!");
   }
}
