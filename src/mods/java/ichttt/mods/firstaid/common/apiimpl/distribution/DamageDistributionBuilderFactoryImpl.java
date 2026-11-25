package ichttt.mods.firstaid.common.apiimpl.distribution;

import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory;
import ichttt.mods.firstaid.api.distribution.ICustomDamageDistributionBuilder;
import ichttt.mods.firstaid.api.distribution.IEqualDamageDistributionBuilder;
import ichttt.mods.firstaid.api.distribution.IRandomDamageDistributionBuilder;
import ichttt.mods.firstaid.api.distribution.IStandardDamageDistributionBuilder;
import javax.annotation.Nonnull;

public class DamageDistributionBuilderFactoryImpl extends DamageDistributionBuilderFactory {
   public static final DamageDistributionBuilderFactoryImpl INSTANCE = new DamageDistributionBuilderFactoryImpl();

   private DamageDistributionBuilderFactoryImpl() {
   }

   @Nonnull
   public IStandardDamageDistributionBuilder newStandardBuilder() {
      return new StandardDamageDistributionBuilder();
   }

   @Nonnull
   public IRandomDamageDistributionBuilder newRandomBuilder() {
      return new RandomDamageDistributionBuilder();
   }

   @Nonnull
   public IEqualDamageDistributionBuilder newEqualBuilder() {
      return new EqualDamageDistributionBuilder();
   }

   @Nonnull
   public ICustomDamageDistributionBuilder newCustomBuilder(IDamageDistribution distribution) {
      return new CustomDamageDistributionBuilder(distribution);
   }
}
