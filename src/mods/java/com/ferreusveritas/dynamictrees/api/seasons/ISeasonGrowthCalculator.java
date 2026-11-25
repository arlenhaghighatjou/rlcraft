package com.ferreusveritas.dynamictrees.api.seasons;

public interface ISeasonGrowthCalculator {
   float calcGrowthRate(Float var1, ClimateZoneType var2);

   float calcSeedDropRate(Float var1, ClimateZoneType var2);

   float calcFruitProduction(Float var1, ClimateZoneType var2);
}
