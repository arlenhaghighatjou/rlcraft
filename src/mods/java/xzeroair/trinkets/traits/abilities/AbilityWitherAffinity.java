package xzeroair.trinkets.traits.abilities;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigWitherRing;

public class AbilityWitherAffinity extends Ability implements IAttackAbility, IPotionAbility {
   private static final ConfigWitherRing serverConfig;

   public AbilityWitherAffinity() {
      super("wither_immunity");
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      return source.equals(DamageSource.WITHER) ? true : cancel;
   }

   public boolean attackEntity(EntityLivingBase target, DamageSource source, float dmg, boolean cancel) {
      if (serverConfig.wither && !target.isPotionActive(MobEffects.WITHER)) {
         Random rand = new Random();
         if (rand.nextInt(serverConfig.wither_chance) == 0) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, serverConfig.wither_duration, 0, false, true));
         }
      }

      return cancel;
   }

   public float damageEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (serverConfig.leech && target.isPotionActive(MobEffects.WITHER) && serverConfig.leech_amount > 0.0F) {
         if (dmg >= serverConfig.leech_amount) {
            ((EntityLivingBase)source.getTrueSource()).heal(serverConfig.leech_amount);
         } else {
            ((EntityLivingBase)source.getTrueSource()).heal(dmg);
         }
      }

      return dmg;
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();
      return !e.contentEquals("minecraft:wither") && !e.contentEquals("minecraft:nausea") ? cancel : true;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.WITHER_RING;
   }
}
