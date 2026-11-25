package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xzeroair.trinkets.traits.abilities.interfaces.IJumpAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.compat.lycanitesmobs.LycanitesCompat;

public class AbilityWeightless extends Ability implements ITickableAbility, IPotionAbility, IJumpAbility {
   public AbilityWeightless() {
      super("weightless");
   }

   public void tickAbility(EntityLivingBase entity) {
      if (!entity.onGround) {
         entity.motionY = 0.0D;
         if (!entity.isSneaking() && entity.isSwingInProgress) {
            entity.motionY += 0.1D;
         }

         if (entity.isSneaking() && entity.isSwingInProgress) {
            entity.motionY -= 0.1D;
         }
      }

   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();
      Potion weight = LycanitesCompat.getPotionEffectByName("weight");
      return weight != null && e.contentEquals(weight.getRegistryName().toString()) ? true : cancel;
   }

   public boolean fall(EntityLivingBase entity, float distance, float damage, boolean cancel) {
      return true;
   }
}
