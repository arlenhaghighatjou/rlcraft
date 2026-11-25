package xzeroair.trinkets.traits.abilities.base;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xzeroair.trinkets.traits.abilities.Ability;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;

public class AbilityPotionImmune extends Ability implements IPotionAbility {
   private String[] potions;

   public AbilityPotionImmune() {
   }

   public AbilityPotionImmune(String... strings) {
      this.potions = strings;
   }

   public AbilityPotionImmune setPotions(String... potions) {
      this.potions = potions;
      return this;
   }

   public void onAbilityAdded(EntityLivingBase entity) {
      for(String potion : this.potions) {
         Potion pot = Potion.getPotionFromResourceLocation(potion);
         if (pot != null) {
            entity.removePotionEffect(pot);
         }
      }

   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();

      for(String potion : this.potions) {
         if (e.contentEquals(potion)) {
            return true;
         }
      }

      return cancel;
   }
}
