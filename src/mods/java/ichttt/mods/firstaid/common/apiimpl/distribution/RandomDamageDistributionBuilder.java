package ichttt.mods.firstaid.common.apiimpl.distribution;

import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.distribution.IRandomDamageDistributionBuilder;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import javax.annotation.Nonnull;

public class RandomDamageDistributionBuilder extends BaseDamageDistributionBuilder implements IRandomDamageDistributionBuilder {
   private boolean nearestFirst = false;
   private boolean tryNoKill = false;

   public RandomDamageDistributionBuilder() {
   }

   @Nonnull
   public IRandomDamageDistributionBuilder useNearestFirst() {
      this.nearestFirst = true;
      return this;
   }

   @Nonnull
   public IRandomDamageDistributionBuilder tryNoKill() {
      this.tryNoKill = true;
      return this;
   }

   public IDamageDistribution build() {
      if (this.nearestFirst) {
         return this.tryNoKill ? RandomDamageDistribution.NEAREST_NOKILL : RandomDamageDistribution.NEAREST_KILL;
      } else {
         return this.tryNoKill ? RandomDamageDistribution.ANY_NOKILL : RandomDamageDistribution.ANY_KILL;
      }
   }
}
