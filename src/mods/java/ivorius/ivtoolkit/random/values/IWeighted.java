package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class IWeighted implements IValue {
   public int[] values;
   public int[] weights;

   public IWeighted(int[] values, int[] weights) {
      this.values = values;
      this.weights = weights;
   }

   public IWeighted(int... valuesWithWeights) {
      this.values = new int[valuesWithWeights.length / 2];
      this.weights = new int[this.values.length];

      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = valuesWithWeights[i * 2];
         this.weights[i] = valuesWithWeights[i * 2 + 1];
      }

   }

   public Integer getValue(Random random) {
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
