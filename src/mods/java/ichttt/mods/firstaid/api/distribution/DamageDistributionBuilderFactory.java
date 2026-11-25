package ichttt.mods.firstaid.api.distribution;

import ichttt.mods.firstaid.api.IDamageDistribution;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DamageDistributionBuilderFactory {
   private static DamageDistributionBuilderFactory factory;

   public DamageDistributionBuilderFactory() {
   }

   public static void setInstance(DamageDistributionBuilderFactory builderFactory) {
      factory = builderFactory;
   }

   @Nullable
   public static DamageDistributionBuilderFactory getInstance() {
      return factory;
   }

   @Nonnull
   public abstract IStandardDamageDistributionBuilder newStandardBuilder();

   @Nonnull
   public abstract IRandomDamageDistributionBuilder newRandomBuilder();

   @Nonnull
   public abstract IEqualDamageDistributionBuilder newEqualBuilder();

   @Nonnull
   public abstract ICustomDamageDistributionBuilder newCustomBuilder(IDamageDistribution var1);
}
