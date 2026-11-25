package xzeroair.trinkets.traits.abilities.compat.survival;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xzeroair.trinkets.traits.abilities.Ability;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.compat.SurvivalCompat;

public class AbilityColdImmunity extends Ability implements ITickableAbility, IPotionAbility {
   public AbilityColdImmunity() {
      super("cold_immunity");
   }

   public void tickAbility(EntityLivingBase entity) {
      SurvivalCompat.immuneToCold(entity);
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      List<Potion> hypothermia = SurvivalCompat.getHypothermiaEffects();
      return hypothermia.contains(effect.getPotion()) ? true : cancel;
   }
}
