package c4.champions.common.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixEvents {
   public AffixEvents() {
   }

   @SubscribeEvent
   public void onLivingJoinWorld(EntityJoinWorldEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntity())) {
         EntityLiving living = (EntityLiving)evt.getEntity();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator rank = chp.getAffixes().iterator();

            while(rank.hasNext()) {
               String aff = (String)rank.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  affix.onSpawn(living, chp);
               }
            }

            Rank rank = chp.getRank();
            if (!living.world.isRemote && ChampionHelper.isElite(rank)) {
               List<Tuple<Potion, Integer>> potions = RankManager.getPotionsForTier(rank.getTier());
               if (potions != null) {
                  for(Tuple<Potion, Integer> potion : potions) {
                     living.addPotionEffect(new PotionEffect((Potion)potion.getFirst(), 200, ((Integer)potion.getSecond()).intValue()));
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator rank = chp.getAffixes().iterator();

            while(rank.hasNext()) {
               String aff = (String)rank.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  affix.onUpdate(living, chp);
               }
            }

            Rank rank = chp.getRank();
            if (ChampionHelper.isElite(rank)) {
               if (living.world.isRemote && !ConfigHandler.hideEffects) {
                  Champions.proxy.generateRankParticle(living, rank.getColor());
               } else if (living.ticksExisted % 100 == 0) {
                  List<Tuple<Potion, Integer>> potions = RankManager.getPotionsForTier(rank.getTier());
                  if (potions != null) {
                     for(Tuple<Potion, Integer> potion : potions) {
                        living.addPotionEffect(new PotionEffect((Potion)potion.getFirst(), 200, ((Integer)potion.getSecond()).intValue()));
                     }
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingAttacked(LivingAttackEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var4 = chp.getAffixes().iterator();

            while(var4.hasNext()) {
               String aff = (String)var4.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  affix.onAttacked(living, chp, evt.getSource(), evt.getAmount(), evt);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingAttack(LivingAttackEvent evt) {
      if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)evt.getSource().getTrueSource();
         if (ChampionHelper.isValidChampion(entityLivingBase)) {
            EntityLiving living = (EntityLiving)entityLivingBase;
            IChampionship chp = CapabilityChampionship.getChampionship(living);
            if (chp != null) {
               UnmodifiableIterator var5 = chp.getAffixes().iterator();

               while(var5.hasNext()) {
                  String aff = (String)var5.next();
                  AffixBase affix = AffixRegistry.getAffix(aff);
                  if (affix != null) {
                     affix.onAttack(living, chp, evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt);
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingWasHurt(LivingHurtEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         float amount = evt.getAmount();
         float newAmount = amount;
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var6 = chp.getAffixes().iterator();

            while(var6.hasNext()) {
               String aff = (String)var6.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  newAmount = affix.onHurt(living, chp, evt.getSource(), amount, newAmount);
               }
            }

            evt.setAmount(newAmount);
         }
      }

   }

   @SubscribeEvent
   public void onLivingDamaged(LivingDamageEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         float amount = evt.getAmount();
         float newAmount = amount;
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var6 = chp.getAffixes().iterator();

            while(var6.hasNext()) {
               String aff = (String)var6.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  newAmount = affix.onDamaged(living, chp, evt.getSource(), amount, newAmount);
               }
            }

            evt.setAmount(newAmount);
         }
      }

   }

   @SubscribeEvent
   public void onLivingKnockback(LivingKnockBackEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getOriginalAttacker())) {
         EntityLiving living = (EntityLiving)evt.getOriginalAttacker();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var4 = chp.getAffixes().iterator();

            while(var4.hasNext()) {
               String aff = (String)var4.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  affix.onKnockback(living, chp, evt);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingDeath(LivingDeathEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var4 = chp.getAffixes().iterator();

            while(var4.hasNext()) {
               String aff = (String)var4.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  affix.onDeath(living, chp, evt.getSource(), evt);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingHeal(LivingHealEvent evt) {
      if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
         float amount = evt.getAmount();
         float newAmount = amount;
         EntityLiving living = (EntityLiving)evt.getEntityLiving();
         IChampionship chp = CapabilityChampionship.getChampionship(living);
         if (chp != null) {
            UnmodifiableIterator var6 = chp.getAffixes().iterator();

            while(var6.hasNext()) {
               String aff = (String)var6.next();
               AffixBase affix = AffixRegistry.getAffix(aff);
               if (affix != null) {
                  newAmount = affix.onHealed(living, chp, amount, newAmount);
               }
            }

            evt.setAmount(newAmount);
         }
      }

   }
}
