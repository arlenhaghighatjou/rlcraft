package ichttt.mods.firstaid.api.distribution;

import javax.annotation.Nonnull;

public interface IRandomDamageDistributionBuilder extends IDamageDistributionBuilder {
   @Nonnull
   IRandomDamageDistributionBuilder useNearestFirst();

   @Nonnull
   IRandomDamageDistributionBuilder tryNoKill();
}
