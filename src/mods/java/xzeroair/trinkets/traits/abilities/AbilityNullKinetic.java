package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IJumpAbility;

public class AbilityNullKinetic extends Ability implements IAttackAbility, IJumpAbility {
   float fallMultiplier = 1.0F;

   public AbilityNullKinetic() {
      super("nullify_kinetic");
   }

   public AbilityNullKinetic setFallMultiplier(float fallMultiplier) {
      this.fallMultiplier = fallMultiplier;
      return this;
   }

   public float getFallMultiplier() {
      return this.fallMultiplier;
   }

   public boolean fall(EntityLivingBase entity, float distance, float multiplier, boolean cancel) {
      return this.fallMultiplier <= 0.0F ? true : cancel;
   }

   public float fallDamageMultiplier(EntityLivingBase entity, float multiplier) {
      if (this.fallMultiplier != 1.0F) {
         multiplier = this.fallMultiplier;
      }

      return multiplier;
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      return this.fallMultiplier > 0.0F || !source.equals(DamageSource.FALL) && !source.equals(DamageSource.FALLING_BLOCK) && !source.equals(DamageSource.FLY_INTO_WALL) ? cancel : true;
   }
}
