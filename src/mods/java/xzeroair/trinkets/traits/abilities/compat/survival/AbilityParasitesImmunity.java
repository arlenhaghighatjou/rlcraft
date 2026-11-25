package xzeroair.trinkets.traits.abilities.compat.survival;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xzeroair.trinkets.traits.abilities.Ability;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.compat.SurvivalCompat;

public class AbilityParasitesImmunity extends Ability implements ITickableAbility, IPotionAbility {
   public AbilityParasitesImmunity() {
      super("parasites_immunity");
   }

   public void tickAbility(EntityLivingBase entity) {
      SurvivalCompat.clearParasites(entity);
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      Potion parasites = SurvivalCompat.getSDParasitesPotionEffect();
      return parasites != null && effect.getPotion().equals(parasites) ? true : cancel;
   }
}
