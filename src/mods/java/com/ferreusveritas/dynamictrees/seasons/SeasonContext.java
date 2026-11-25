package com.ferreusveritas.dynamictrees.seasons;

import com.ferreusveritas.dynamictrees.api.seasons.ClimateZoneType;
import com.ferreusveritas.dynamictrees.api.seasons.ISeasonGrowthCalculator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonContext {
   private final ISeasonProvider provider;
   private final ISeasonGrowthCalculator calculator;
   private float temperateGrowthFactor;
   private float tropicalGrowthFactor;
   private float temperateSeedDropFactor;
   private float tropicalSeedDropFactor;
   private float temperateFruitProductionFactor;
   private float tropicalFruitProductionFactor;
   private Float seasonValue;
   private long methodTicks = 0L;

   public SeasonContext(ISeasonProvider provider, ISeasonGrowthCalculator calculator) {
      this.provider = provider;
      this.calculator = calculator;
   }

   public void updateTick(World world, long worldTicks) {
      if (this.methodTicks % 20L == 0L) {
         this.provider.updateTick(world, worldTicks);
         this.seasonValue = this.provider.getSeasonValue(world, BlockPos.ORIGIN);
         this.temperateGrowthFactor = this.calculator.calcGrowthRate(this.seasonValue, ClimateZoneType.TEMPERATE);
         this.tropicalGrowthFactor = this.calculator.calcGrowthRate(this.seasonValue, ClimateZoneType.TROPICAL);
         this.temperateSeedDropFactor = this.calculator.calcSeedDropRate(this.seasonValue, ClimateZoneType.TEMPERATE);
         this.tropicalSeedDropFactor = this.calculator.calcSeedDropRate(this.seasonValue, ClimateZoneType.TROPICAL);
         this.temperateFruitProductionFactor = this.calculator.calcFruitProduction(this.seasonValue, ClimateZoneType.TEMPERATE);
         this.tropicalFruitProductionFactor = this.calculator.calcFruitProduction(this.seasonValue, ClimateZoneType.TROPICAL);
      }

      ++this.methodTicks;
   }

   public ISeasonProvider getSeasonProvider() {
      return this.provider;
   }

   public ISeasonGrowthCalculator getCalculator() {
      return this.calculator;
   }

   public float getTemperateGrowthFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcGrowthRate(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TEMPERATE) : this.temperateGrowthFactor;
   }

   public float getTropicalGrowthFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcGrowthRate(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TROPICAL) : this.tropicalGrowthFactor;
   }

   public float getTemperateSeedDropFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcSeedDropRate(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TEMPERATE) : this.temperateSeedDropFactor;
   }

   public float getTropicalSeedDropFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcSeedDropRate(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TROPICAL) : this.tropicalSeedDropFactor;
   }

   public float getTemperateFruitProductionFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcFruitProduction(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TEMPERATE) : this.temperateFruitProductionFactor;
   }

   public float getTropicalFruitProductionFactor(float offset) {
      return offset != 0.0F && this.seasonValue != null ? this.calculator.calcFruitProduction(Float.valueOf(this.seasonValue.floatValue() + offset), ClimateZoneType.TROPICAL) : this.tropicalFruitProductionFactor;
   }
}
