package xzeroair.trinkets.traits.abilities;

import java.util.function.BiFunction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Loader;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.capabilities.statushandler.TrinketStatusEffect;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.statuseffects.StatusEffectsEnum;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.lycanitesmobs.LycanitesCompat;
import xzeroair.trinkets.util.config.trinkets.ConfigFaelisClaw;

public class AbilityViciousStrike extends Ability implements IAttackAbility {
   public static final ConfigFaelisClaw serverConfig;

   public AbilityViciousStrike() {
      super("vicious_strike");
   }

   public float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (this.isIndirectDamage(source)) {
         return dmg;
      } else {
         boolean faelis = false;
         int chance = serverConfig.chance > 0 ? this.random.nextInt(serverConfig.chance) : 0;
         if (source.getTrueSource() instanceof EntityLivingBase) {
            boolean isFaelis = ((Boolean)Capabilities.getEntityProperties(source.getTrueSource(), Boolean.valueOf(faelis), (prop, rtn) -> {
               return prop.getCurrentRace().equals(EntityRaces.faelis);
            })).booleanValue();
            if (isFaelis) {
               chance /= 2;
               faelis = true;
            }
         }

         if (chance == 0) {
            int duration = (int)(faelis ? (double)serverConfig.bleedDuration : (double)serverConfig.bleedDuration * 0.33D);
            int amplifier = 1;
            if (Loader.isModLoaded("lycanitesmobs") && TrinketsConfig.compat.lycanites && serverConfig.compat.lycanites.useLycaniteBleed) {
               LycanitesCompat.applyEffect(target, "bleed", duration, 0);
            } else if (Loader.isModLoaded("defiledlands") && TrinketsConfig.compat.defiledlands && serverConfig.compat.defiledlands.useDefiledLandsBleed) {
               try {
                  Potion effect = Potion.getPotionFromResourceLocation("defiledlands:bleeding");
                  if (effect != null) {
                     boolean hasPot = target.isPotionActive(effect);
                     if (!hasPot) {
                        PotionEffect bleed = new PotionEffect(effect, duration, 0, false, false);
                        target.addPotionEffect(bleed);
                     } else {
                        PotionEffect bleeding = target.getActivePotionEffect(effect);
                        if (bleeding.getAmplifier() < 3) {
                           PotionEffect bleed = new PotionEffect(effect, duration, bleeding.getAmplifier() + 1, false, false);
                           target.addPotionEffect(bleed);
                        }
                     }
                  }
               } catch (Exception var12) {
                  var12.printStackTrace();
               }
            } else {
               StatusHandler status = Capabilities.getStatusHandler(target);
               if (status != null && !status.getActiveEffects().containsKey(StatusEffectsEnum.bleed.getName())) {
                  TrinketStatusEffect effect = new TrinketStatusEffect(StatusEffectsEnum.bleed, duration, 1, source.getTrueSource());
                  status.apply(effect);
               }
            }
         }

         return dmg;
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.FAELIS_CLAW;
   }
}
