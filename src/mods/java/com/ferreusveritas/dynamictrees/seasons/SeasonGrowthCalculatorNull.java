package com.ferreusveritas.dynamictrees.seasons;

import com.ferreusveritas.dynamictrees.api.seasons.ClimateZoneType;
import com.ferreusveritas.dynamictrees.api.seasons.ISeasonGrowthCalculator;

public class SeasonGrowthCalculatorNull implements ISeasonGrowthCalculator {
   public SeasonGrowthCalculatorNull() {
   }

   public float calcGrowthRate(Float seasonValue, ClimateZoneType type) {
      return 1.0F;
   }

   public float calcSeedDropRate(Float seasonValue, ClimateZoneType type) {
      return 1.0F;
   }

   public float calcFruitProduction(Float seasonValue, ClimateZoneType type) {
      return 1.0F;
   }
}
