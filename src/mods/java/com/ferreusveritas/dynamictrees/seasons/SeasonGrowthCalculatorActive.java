package com.ferreusveritas.dynamictrees.seasons;

import com.ferreusveritas.dynamictrees.api.seasons.ClimateZoneType;
import com.ferreusveritas.dynamictrees.api.seasons.ISeasonGrowthCalculator;
import net.minecraft.util.math.MathHelper;

public class SeasonGrowthCalculatorActive implements ISeasonGrowthCalculator {
   public SeasonGrowthCalculatorActive() {
   }

   protected float clippedsineWave(float seasonValue, float qPhase, float amplitude, float bias) {
      return MathHelper.clamp((float)Math.sin((double)(seasonValue / 2.0F) * 3.141592653589793D + 0.7853981633974483D * (double)qPhase) * amplitude + bias, 0.0F, 1.0F);
   }

   public float calcGrowthRate(Float seasonValue, ClimateZoneType type) {
      if (seasonValue == null) {
         return 1.0F;
      } else {
         switch(type) {
         case TEMPERATE:
            return this.clippedsineWave(seasonValue.floatValue(), 7.0F, 0.8F, 1.0F);
         case TROPICAL:
            return this.clippedsineWave(seasonValue.floatValue(), 2.0F, 0.31F, 0.9F);
         default:
            return 1.0F;
         }
      }
   }

   public float calcSeedDropRate(Float seasonValue, ClimateZoneType type) {
      if (seasonValue == null) {
         return 1.0F;
      } else {
         switch(type) {
         case TEMPERATE:
            return this.clippedsineWave(seasonValue.floatValue(), 5.0F, 1.5F, -0.25F);
         case TROPICAL:
            return this.clippedsineWave(seasonValue.floatValue(), 7.0F, 0.31F, 0.9F);
         default:
            return 1.0F;
         }
      }
   }

   public float calcFruitProduction(Float seasonValue, ClimateZoneType type) {
      if (seasonValue == null) {
         return 1.0F;
      } else {
         switch(type) {
         case TEMPERATE:
            return this.clippedsineWave(seasonValue.floatValue(), 7.0F, 1.0F, 1.0F);
         case TROPICAL:
            return this.clippedsineWave(seasonValue.floatValue(), 1.0F, 0.31F, 0.9F);
         default:
            return 1.0F;
         }
      }
   }
}
