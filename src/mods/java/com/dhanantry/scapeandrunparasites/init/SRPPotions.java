package com.dhanantry.scapeandrunparasites.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class SRPPotions {
   public static final Potion COTH_E = new SRPEffectBase("coth", true, 894258, 0, 0);
   public static final Potion FEAR_E = new SRPEffectBase("fear", true, 894257, 0, 0);
   public static final Potion RES_E = new SRPEffectBase("antimall", true, 894257, 0, 0);
   public static final Potion BLEED_E = new SRPEffectBase("bleed", true, 894257, 0, 0);
   public static final Potion CORRO_E = new SRPEffectBase("corrosive", true, 894257, 0, 0);
   public static final Potion VIRA_E = new SRPEffectBase("viral", true, 894257, 0, 0);
   public static final Potion RAGE_E = (new SRPEffectBase("rage", true, 894257, 0, 0)).registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, EaglercraftUUID.randomUUID().toString(), SRPConfigSystems.rageSpeed, 2).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, EaglercraftUUID.randomUUID().toString(), SRPConfigSystems.rageDamage, 2);
   public static final Potion EPEL_E = new SRPEffectBase("repel", true, 894257, 0, 0);
   public static final Potion SENS_E = (new SRPEffectBase("senses", true, 894257, 0, 0)).registerPotionAttributeModifier(SharedMonsterAttributes.FOLLOW_RANGE, EaglercraftUUID.randomUUID().toString(), 0.1D, 2);
   public static final Potion PREY_E = new SRPEffectBase("prey", true, 894257, 0, 0);
   public static final Potion DEBAR_E = new SRPEffectBase("debar", true, 894258, 0, 0);
   public static final Potion DLER_E = new SRPEffectBase("needler", true, 894258, 0, 0);
   public static final Potion FOSTER_E = new SRPEffectBase("foster", true, 894258, 0, 0);
   public static final Potion LINK_E = new SRPEffectBase("link", true, 894258, 0, 0);
   public static final PotionType COTH_P = (PotionType)(new PotionType("srparasites:coth", new PotionEffect[]{new PotionEffect(COTH_E, 2400)})).setRegistryName("srparasites:coth");
   public static final PotionType FEAR_P = (PotionType)(new PotionType("srparasites:fear", new PotionEffect[]{new PotionEffect(FEAR_E, 2400)})).setRegistryName("srparasites:fear");
   public static final PotionType RES_P = (PotionType)(new PotionType("srparasites:antimall", new PotionEffect[]{new PotionEffect(RES_E, 2400)})).setRegistryName("srparasites:res");
   public static final PotionType CORRO_P = (PotionType)(new PotionType("srparasites:corrosive", new PotionEffect[]{new PotionEffect(CORRO_E, 2400)})).setRegistryName("srparasites:corro");
   public static final PotionType VIRA_P = (PotionType)(new PotionType("srparasites:viral", new PotionEffect[]{new PotionEffect(VIRA_E, 2400)})).setRegistryName("srparasites:vira");
   public static final PotionType RAGE_P = (PotionType)(new PotionType("srparasites:rage", new PotionEffect[]{new PotionEffect(RAGE_E, 2400)})).setRegistryName("srparasites:rage");
   public static final PotionType EPEL_P = (PotionType)(new PotionType("srparasites:repel", new PotionEffect[]{new PotionEffect(EPEL_E, 2400)})).setRegistryName("srparasites:repel");
   public static final PotionType SENS_P = (PotionType)(new PotionType("srparasites:senses", new PotionEffect[]{new PotionEffect(SENS_E, 2400)})).setRegistryName("srparasites:senses");
   public static final PotionType DEBAR_P = (PotionType)(new PotionType("srparasites:debar", new PotionEffect[]{new PotionEffect(DEBAR_E, 2400)})).setRegistryName("srparasites:debar");
   public static final PotionType FOSTER_P = (PotionType)(new PotionType("srparasites:foster", new PotionEffect[]{new PotionEffect(FOSTER_E, 2400)})).setRegistryName("srparasites:foster");
   public static final PotionType LINK_P = (PotionType)(new PotionType("srparasites:link", new PotionEffect[]{new PotionEffect(LINK_E, 2400)})).setRegistryName("srparasites:link");

   public SRPPotions() {
   }

   public static void applyStackPotion(Potion effect, EntityLivingBase in, int duration, int amp) {
      if (amp >= 0) {
         if (amp + 1 >= 256) {
            return;
         }
      } else if (amp - 1 <= -256) {
         return;
      }

      PotionEffect flag = in.getActivePotionEffect(effect);
      if (flag == null) {
         in.addPotionEffect(new PotionEffect(effect, duration, amp, false, false));
      } else {
         String name = effect.getRegistryName().toString();
         int lockAmp = 0;
         String[] here = new String[2];
         int i = 0;

         while(true) {
            if (i < SRPConfig.stackablePotionsLimit.length) {
               here = SRPConfig.stackablePotionsLimit[i].split(";");
               if (!here[0].equals(name)) {
                  ++i;
                  continue;
               }

               lockAmp = Integer.parseInt(here[1]);
               System.out.println("p " + name + " a " + lockAmp + " f " + flag.getAmplifier());
               if (amp > lockAmp || flag.getAmplifier() + 1 > lockAmp) {
                  in.addPotionEffect(new PotionEffect(effect, duration, lockAmp, false, false));
                  return;
               }
            }

            if (flag.getAmplifier() < amp) {
               in.addPotionEffect(new PotionEffect(effect, duration, amp, false, false));
               return;
            }

            i = flag.getAmplifier();
            if (i < 0) {
               --i;
            } else {
               ++i;
            }

            int newDur = flag.getDuration();
            if (newDur + 40 <= duration) {
               newDur = duration;
            } else {
               newDur = newDur + 10;
            }

            in.addPotionEffect(new PotionEffect(effect, newDur, i, false, false));
            break;
         }
      }

   }

   public static boolean applySense(EntityLivingBase in, int duration, double rangeToCover, int withLimit) {
      if (in instanceof EntityPStationary) {
         return false;
      } else if (rangeToCover >= (double)(withLimit * withLimit)) {
         return false;
      } else {
         double rangeB = in.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue();
         if (rangeB * rangeB > rangeToCover) {
            return true;
         } else {
            int amp = 0;
            double bonus = rangeB * 0.1D;
            int count = 0;
            boolean flag = true;

            while(flag) {
               if ((rangeB + bonus) * (rangeB + bonus) >= rangeToCover) {
                  in.addPotionEffect(new PotionEffect(SENS_E, duration, amp, false, false));
                  flag = false;
                  return true;
               }

               ++amp;
               bonus = rangeB * 0.1D * (double)amp;
               ++count;
               if (count > 10) {
                  flag = false;
                  return false;
               }
            }

            return false;
         }
      }
   }

   public static class RegistrationHandler {
      public RegistrationHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Potion.class, RegistrationHandler::onEventE);
         bus.registerGeneric(Register.class, PotionType.class, RegistrationHandler::onEventP);
      }

      public static void onEventE(Register<Potion> event) {
         IForgeRegistry<Potion> registry = event.getRegistry();
         registry.register(SRPPotions.COTH_E);
         registry.register(SRPPotions.FEAR_E);
         registry.register(SRPPotions.RES_E);
         registry.register(SRPPotions.BLEED_E);
         registry.register(SRPPotions.CORRO_E);
         registry.register(SRPPotions.VIRA_E);
         registry.register(SRPPotions.RAGE_E);
         registry.register(SRPPotions.EPEL_E);
         registry.register(SRPPotions.SENS_E);
         registry.register(SRPPotions.PREY_E);
         registry.register(SRPPotions.DEBAR_E);
         registry.register(SRPPotions.DLER_E);
         registry.register(SRPPotions.FOSTER_E);
         registry.register(SRPPotions.LINK_E);
      }

      public static void onEventP(Register<PotionType> event) {
         IForgeRegistry<PotionType> registry = event.getRegistry();
         registry.register(SRPPotions.COTH_P);
         registry.register(SRPPotions.FEAR_P);
         registry.register(SRPPotions.RES_P);
         registry.register(SRPPotions.CORRO_P);
         registry.register(SRPPotions.VIRA_P);
         registry.register(SRPPotions.RAGE_P);
         registry.register(SRPPotions.EPEL_P);
         registry.register(SRPPotions.SENS_P);
         registry.register(SRPPotions.DEBAR_P);
         registry.register(SRPPotions.FOSTER_P);
         registry.register(SRPPotions.LINK_P);
      }
   }
}
