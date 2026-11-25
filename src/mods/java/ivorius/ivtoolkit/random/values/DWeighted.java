package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DWeighted implements DValue {
   public double[] values;
   public int[] weights;

   public DWeighted(double[] values, int[] weights) {
      this.values = values;
      this.weights = weights;
   }

   public DWeighted(Number... valuesWithWeights) {
      this.values = new double[valuesWithWeights.length / 2];
      this.weights = new int[this.values.length];

      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = ((Double)valuesWithWeights[i * 2]).doubleValue();
         this.weights[i] = ((Integer)valuesWithWeights[i * 2 + 1]).intValue();
      }

   }

   public Double getValue(EaglercraftRandom random) {
      int total = getTotalWeight(this.weights);
      int selected = random.nextInt(total);

      for(int i = 0; i < this.weights.length; ++i) {
         selected -= this.weights[i];
         if (selected < 0) {
            return this.values[i];
         }
      }

      throw new RuntimeException("Weights have invalid values!");
   }

   public static int getTotalWeight(int[] weights) {
      int weight = 0;

      for(int i : weights) {
         weight += i;
      }

      return weight;
   }
}
