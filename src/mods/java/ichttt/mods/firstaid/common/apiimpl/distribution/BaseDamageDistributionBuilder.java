package ichttt.mods.firstaid.common.apiimpl.distribution;

import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.distribution.IDamageDistributionBuilder;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import java.util.function.Predicate;
import net.minecraft.util.DamageSource;

public abstract class BaseDamageDistributionBuilder implements IDamageDistributionBuilder {
   public BaseDamageDistributionBuilder() {
   }

   public void registerDynamic(Predicate<DamageSource> matcher) {
      FirstAidRegistryImpl.INSTANCE.registerDistribution(matcher, this.build());
   }

   public void registerStatic(DamageSource... source) {
      FirstAidRegistryImpl.INSTANCE.registerDistribution(source, this.build());
   }

   public abstract IDamageDistribution build();
}
