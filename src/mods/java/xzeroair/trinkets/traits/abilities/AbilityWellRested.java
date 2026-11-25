package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import xzeroair.trinkets.traits.abilities.interfaces.IHeldAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ISleepAbility;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.PotionHelper;

public class AbilityWellRested extends Ability implements IPotionAbility, ISleepAbility, IHeldAbility {
   public AbilityWellRested() {
      super("well_rested");
   }

   public SleepResult onStartSleeping(EntityLivingBase entity, BlockPos pos, SleepResult result) {
      return result;
   }

   public void onWakeUp(EntityLivingBase entity, boolean wakeImmediately, boolean updatedWorld, boolean setSpawn) {
      if (!entity.world.isRemote) {
         if (!(entity instanceof EntityPlayer) || ((EntityPlayer)entity).isPlayerFullyAsleep()) {
            if (TrinketsConfig.SERVER.Items.TEDDY_BEAR.sleep_bonus) {
               String[] config = TrinketsConfig.SERVER.Items.TEDDY_BEAR.buffs;
               int amount = TrinketsConfig.SERVER.Items.TEDDY_BEAR.randomBuff;
               if (amount > config.length) {
                  amount = config.length;
               }

               if (amount > 0) {
                  String potID = "";

                  for(int i = 0; i < amount; ++i) {
                     int potRand = Reference.random.nextInt(config.length);
                     potID = potID + config[potRand] + ",";
                  }

                  if (!potID.isEmpty()) {
                     String[] pots = potID.split(",");

                     for(String p : pots) {
                        if (!p.isEmpty()) {
                           PotionHelper.PotionHolder potion = PotionHelper.getPotionHolder(p);
                           if (!entity.isPotionActive(potion.getPotion())) {
                              entity.addPotionEffect(potion.getPotionEffect());
                           } else {
                              entity.getActivePotionEffect(potion.getPotion()).combine(potion.getPotionEffect());
                           }
                        }
                     }
                  }
               } else {
                  for(String potID : config) {
                     PotionHelper.PotionHolder potion = PotionHelper.getPotionHolder(potID);
                     if (potion.getPotion() != null) {
                        entity.addPotionEffect(potion.getPotionEffect());
                     }
                  }
               }
            }

         }
      }
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();

      for(String immunity : TrinketsConfig.SERVER.Items.TEDDY_BEAR.immunities) {
         Potion fear = Potion.getPotionFromResourceLocation(immunity);
         if (fear != null && e.contentEquals(fear.getRegistryName().toString())) {
            return true;
         }
      }

      return cancel;
   }
}
