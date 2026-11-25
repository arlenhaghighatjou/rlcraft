package ichttt.mods.firstaid.api.distribution;

import javax.annotation.Nonnull;

public interface IEqualDamageDistributionBuilder extends IDamageDistributionBuilder {
   @Nonnull
   IEqualDamageDistributionBuilder tryNoKill();

   @Nonnull
   IEqualDamageDistributionBuilder reductionMultiplier(float var1);
}
