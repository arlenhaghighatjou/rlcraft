package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.init.ModPotionTypes;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;

public class AbilityIceImmunity extends Ability implements ITickableAbility, IPotionAbility, IAttackAbility {
   Potion ice_resist = (Potion)ModPotionTypes.TrinketPotions.get("ice_resistance");

   public AbilityIceImmunity() {
      super("ice_immunity");
   }

   public void tickAbility(EntityLivingBase entity) {
      AbilityHandler.AbilityHolder holder = this.getAbilityHolder();
      if (holder.getInfo().getHandlerType() != TrinketHelper.SlotInformation.ItemHandlerType.POTION) {
         if (this.ice_resist != null) {
            if (!entity.isPotionActive(this.ice_resist)) {
               entity.addPotionEffect(new PotionEffect(this.ice_resist, 60, 0, false, false));
            }

            if (entity.world.isRemote && entity.isPotionActive(this.ice_resist)) {
               entity.getActivePotionEffect(this.ice_resist).setPotionDurationMax(true);
            }
         }

      }
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      return cancel;
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      return !source.damageType.contentEquals("ooze") && !source.damageType.contentEquals("dragon_ice") && !source.damageType.contentEquals("cold_fire") ? cancel : true;
   }

   public void onAbilityAdded(EntityLivingBase entity) {
   }

   public void onAbilityRemoved(EntityLivingBase entity) {
      if (this.ice_resist != null) {
         entity.removePotionEffect(this.ice_resist);
      }

   }
}
