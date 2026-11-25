package xzeroair.trinkets.traits.abilities.compat.survival;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xzeroair.trinkets.traits.abilities.Ability;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.compat.SurvivalCompat;

public class AbilityThirstImmunity extends Ability implements ITickableAbility, IPotionAbility {
   public AbilityThirstImmunity() {
      super("thirst_immunity");
   }

   public void tickAbility(EntityLivingBase entity) {
      SurvivalCompat.clearThirst(entity);
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      List<Potion> thirsts = SurvivalCompat.getThirstEffects();
      return thirsts.contains(effect.getPotion()) ? true : cancel;
   }
}
