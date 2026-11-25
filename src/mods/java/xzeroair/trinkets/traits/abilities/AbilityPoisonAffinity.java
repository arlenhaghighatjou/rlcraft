package xzeroair.trinkets.traits.abilities;

import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigPoisonStone;

public class AbilityPoisonAffinity extends Ability implements IAttackAbility, IPotionAbility, ITickableAbility {
   private final ConfigPoisonStone serverConfig;

   public AbilityPoisonAffinity() {
      super("poison_affinity");
      this.serverConfig = TrinketsConfig.SERVER.Items.POISON_STONE;
   }

   public void tickAbility(EntityLivingBase entity) {
      Map<Potion, PotionEffect> potMap = entity.getActivePotionMap();
      if (!potMap.isEmpty()) {
         for(String immunities : TrinketsConfig.SERVER.Items.POISON_STONE.immunities) {
            Potion effect = Potion.getPotionFromResourceLocation(immunities);
            if (effect != null) {
               entity.removePotionEffect(effect);
            }
         }
      }

   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      return !source.damageType.contentEquals("poison") && !source.damageType.contentEquals("xat.poison") ? cancel : true;
   }

   public float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (this.serverConfig.poison && !target.isPotionActive(MobEffects.POISON) && !source.isMagicDamage() && !source.isFireDamage() && !source.isExplosion() && (this.serverConfig.poison_chance == 0 || this.serverConfig.poison_chance > 0 && this.random.nextInt(this.serverConfig.poison_chance) == 0)) {
         target.addPotionEffect(new PotionEffect(MobEffects.POISON, this.serverConfig.poison_duration, 0, false, true));
      }

      if (target.isPotionActive(MobEffects.POISON) && !source.isMagicDamage() && !source.isFireDamage() && !source.isExplosion()) {
         if (this.serverConfig.bonus_damage) {
            float multipliedDamage = dmg * this.serverConfig.bonus_damage_amount;
            return multipliedDamage;
         } else {
            return dmg;
         }
      } else {
         return dmg;
      }
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();

      for(String immunity : TrinketsConfig.SERVER.Items.POISON_STONE.immunities) {
         Potion pot = Potion.getPotionFromResourceLocation(immunity);
         if (pot != null && e.contentEquals(pot.getRegistryName().toString())) {
            return true;
         }
      }

      return cancel;
   }
}
