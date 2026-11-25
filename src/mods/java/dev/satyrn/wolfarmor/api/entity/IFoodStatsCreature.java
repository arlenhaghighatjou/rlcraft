package dev.satyrn.wolfarmor.api.entity;

import dev.satyrn.wolfarmor.api.util.CreatureFoodStats;
import javax.annotation.Nonnull;

public interface IFoodStatsCreature {
   @Nonnull
   CreatureFoodStats getFoodStats();

   void addExhaustion(float var1);
}
