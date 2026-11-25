package ichttt.mods.firstaid.api.distribution;

import java.util.function.Predicate;
import net.minecraft.util.DamageSource;

public interface IDamageDistributionBuilder {
   void registerDynamic(Predicate<DamageSource> var1);

   void registerStatic(DamageSource... var1);
}
