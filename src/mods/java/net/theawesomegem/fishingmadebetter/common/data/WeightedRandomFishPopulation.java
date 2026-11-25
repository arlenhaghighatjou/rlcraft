package net.theawesomegem.fishingmadebetter.common.data;

import net.minecraft.util.WeightedRandom.Item;

public class WeightedRandomFishPopulation extends Item {
   public final String fishId;
   public final int population;

   public WeightedRandomFishPopulation(int itemWeightIn, String fishId, int population) {
      super(itemWeightIn);
      this.fishId = fishId;
      this.population = population;
   }
}
