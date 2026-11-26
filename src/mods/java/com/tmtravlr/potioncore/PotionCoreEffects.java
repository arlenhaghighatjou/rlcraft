package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.potion.PotionAntidote;
import com.tmtravlr.potioncore.potion.PotionArchery;
import com.tmtravlr.potioncore.potion.PotionBless;
import com.tmtravlr.potioncore.potion.PotionBrokenArmor;
import com.tmtravlr.potioncore.potion.PotionBrokenMagicShield;
import com.tmtravlr.potioncore.potion.PotionChance;
import com.tmtravlr.potioncore.potion.PotionClimb;
import com.tmtravlr.potioncore.potion.PotionCorePotion;
import com.tmtravlr.potioncore.potion.PotionCure;
import com.tmtravlr.potioncore.potion.PotionCurse;
import com.tmtravlr.potioncore.potion.PotionDiamondSkin;
import com.tmtravlr.potioncore.potion.PotionDisorganization;
import com.tmtravlr.potioncore.potion.PotionDispel;
import com.tmtravlr.potioncore.potion.PotionDrown;
import com.tmtravlr.potioncore.potion.PotionExplosion;
import com.tmtravlr.potioncore.potion.PotionExplosionSelf;
import com.tmtravlr.potioncore.potion.PotionExtension;
import com.tmtravlr.potioncore.potion.PotionFire;
import com.tmtravlr.potioncore.potion.PotionFlight;
import com.tmtravlr.potioncore.potion.PotionInvert;
import com.tmtravlr.potioncore.potion.PotionIronSkin;
import com.tmtravlr.potioncore.potion.PotionKlutz;
import com.tmtravlr.potioncore.potion.PotionLaunch;
import com.tmtravlr.potioncore.potion.PotionLightning;
import com.tmtravlr.potioncore.potion.PotionLove;
import com.tmtravlr.potioncore.potion.PotionMagicFocus;
import com.tmtravlr.potioncore.potion.PotionMagicInhibition;
import com.tmtravlr.potioncore.potion.PotionMagicShield;
import com.tmtravlr.potioncore.potion.PotionPerplexity;
import com.tmtravlr.potioncore.potion.PotionPotionSickness;
import com.tmtravlr.potioncore.potion.PotionPurity;
import com.tmtravlr.potioncore.potion.PotionReach;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRepair;
import com.tmtravlr.potioncore.potion.PotionRevival;
import com.tmtravlr.potioncore.potion.PotionRust;
import com.tmtravlr.potioncore.potion.PotionSlowfall;
import com.tmtravlr.potioncore.potion.PotionSolidCore;
import com.tmtravlr.potioncore.potion.PotionSpin;
import com.tmtravlr.potioncore.potion.PotionStepup;
import com.tmtravlr.potioncore.potion.PotionTeleport;
import com.tmtravlr.potioncore.potion.PotionTeleportSpawn;
import com.tmtravlr.potioncore.potion.PotionTeleportSurface;
import com.tmtravlr.potioncore.potion.PotionVulnerable;
import com.tmtravlr.potioncore.potion.PotionWeight;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHealth;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class PotionCoreEffects {
   public static final HashMap<String, PotionCorePotion> POTIONS = new HashMap();
   public static final ArrayList<Potion> GOOD_EFFECTS = new ArrayList();
   public static final ArrayList<Potion> BAD_EFFECTS = new ArrayList();
   public static final HashMap<Potion, Potion> OPPOSITE_EFFECTS = new HashMap();

   public PotionCoreEffects() {
   }

   public static void loadPotionEffects(IForgeRegistry<Potion> registry) {
      MobEffects.RESISTANCE.registerPotionAttributeModifier(PotionCoreAttributes.DAMAGE_RESISTANCE, "f26465f4-bdf0-4014-99ae-2d52c22953e0", 0.2D, 1);
      MobEffects.HASTE.registerPotionAttributeModifier(PotionCoreAttributes.DIG_SPEED, "00b4c426-2fae-46dd-a3f1-708c95beb21c", 0.2D, 1);
      if (ConfigLoader.fixSaturation) {
         Potion fixedSaturation = (Potion)(new PotionHealth(false, 16262179) {
            public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health) {
               amplifier = (int)(health * (double)amplifier);
               this.performEffect(entityLivingBaseIn, amplifier);
            }
         }).setPotionName("effect.saturation").setBeneficial().setRegistryName(new ResourceLocation("minecraft:saturation"));
         registry.register(fixedSaturation);
         replaceVanillaSaturation(fixedSaturation);
      }

      if (ConfigLoader.controllableJumpBoost) {
         MobEffects.JUMP_BOOST.registerPotionAttributeModifier(PotionCoreAttributes.JUMP_HEIGHT, "3f9ee864-485e-4ce8-9226-a0a4dbc50b0f", (double)PotionCoreHelper.vanillaJumpBoostModifier, 0);
      } else {
         MobEffects.JUMP_BOOST.registerPotionAttributeModifier(PotionCoreAttributes.JUMP_HEIGHT, "3f9ee864-485e-4ce8-9226-a0a4dbc50b0f", 1.0D, 0);
      }

      for(PotionCorePotion potion : POTIONS.values()) {
         if (potion.isEnabled()) {
            potion.registerPotionAttributeModifiers();
            registry.register(potion);
         }
      }

   }

   public static void loadGoodBadEffects() {
      for(ResourceLocation potionName : Potion.REGISTRY.getKeys()) {
         if (!ConfigLoader.chanceBlacklist.contains(potionName.toString())) {
            Potion potion = (Potion)Potion.REGISTRY.getObject(potionName);
            if (potion != null) {
               if (potion.isBadEffect()) {
                  BAD_EFFECTS.add(potion);
               } else {
                  GOOD_EFFECTS.add(potion);
               }
            }
         }
      }

   }

   public static void loadInversions() {
      loadInversion(MobEffects.BLINDNESS, MobEffects.NIGHT_VISION);
      loadInversion(MobEffects.STRENGTH, MobEffects.WEAKNESS);
      loadInversion(MobEffects.HASTE, MobEffects.MINING_FATIGUE);
      loadInversion(MobEffects.FIRE_RESISTANCE, PotionFire.INSTANCE);
      loadInversion(MobEffects.INSTANT_DAMAGE, MobEffects.INSTANT_HEALTH);
      loadInversion(MobEffects.HUNGER, MobEffects.SATURATION);
      loadInversion(MobEffects.JUMP_BOOST, PotionWeight.INSTANCE);
      loadInversion(MobEffects.SLOWNESS, MobEffects.SPEED);
      loadInversion(MobEffects.POISON, PotionAntidote.INSTANCE);
      loadInversion(MobEffects.REGENERATION, MobEffects.WITHER);
      loadInversion(MobEffects.RESISTANCE, PotionVulnerable.INSTANCE);
      loadInversion(MobEffects.WATER_BREATHING, PotionDrown.INSTANCE);
      loadInversion(PotionArchery.INSTANCE, PotionKlutz.INSTANCE);
      loadInversion(PotionBless.INSTANCE, PotionCurse.INSTANCE);
      loadInversion(PotionCure.INSTANCE, PotionDispel.INSTANCE);
      loadInversion(MobEffects.LEVITATION, PotionSlowfall.INSTANCE);
      loadInversion(PotionRepair.INSTANCE, PotionRust.INSTANCE);
      loadInversion(PotionMagicFocus.INSTANCE, PotionMagicInhibition.INSTANCE);
      loadInversion(PotionIronSkin.INSTANCE, PotionBrokenArmor.INSTANCE);
      loadInversion(PotionMagicShield.INSTANCE, PotionBrokenMagicShield.INSTANCE);
   }

   private static void loadInversion(Potion potion1, Potion potion2) {
      if (potion1 != null && potion2 != null) {
         OPPOSITE_EFFECTS.put(potion1, potion2);
         OPPOSITE_EFFECTS.put(potion2, potion1);
      }

   }

   private static void replaceVanillaSaturation(Potion fixedSaturation) {
      MobEffects.SATURATION = fixedSaturation;
   }

   static {
      POTIONS.put("fire", PotionFire.INSTANCE);
      POTIONS.put("lightning", PotionLightning.INSTANCE);
      POTIONS.put("explode", PotionExplosion.INSTANCE);
      POTIONS.put("burst", PotionExplosionSelf.INSTANCE);
      POTIONS.put("weight", PotionWeight.INSTANCE);
      POTIONS.put("recoil", PotionRecoil.INSTANCE);
      POTIONS.put("drown", PotionDrown.INSTANCE);
      POTIONS.put("archery", PotionArchery.INSTANCE);
      POTIONS.put("klutz", PotionKlutz.INSTANCE);
      POTIONS.put("vulnerable", PotionVulnerable.INSTANCE);
      POTIONS.put("antidote", PotionAntidote.INSTANCE);
      POTIONS.put("purity", PotionPurity.INSTANCE);
      POTIONS.put("cure", PotionCure.INSTANCE);
      POTIONS.put("dispel", PotionDispel.INSTANCE);
      POTIONS.put("slow_fall", PotionSlowfall.INSTANCE);
      POTIONS.put("solid_core", PotionSolidCore.INSTANCE);
      POTIONS.put("launch", PotionLaunch.INSTANCE);
      POTIONS.put("climb", PotionClimb.INSTANCE);
      POTIONS.put("love", PotionLove.INSTANCE);
      POTIONS.put("step_up", PotionStepup.INSTANCE);
      POTIONS.put("perplexity", PotionPerplexity.INSTANCE);
      POTIONS.put("disorganization", PotionDisorganization.INSTANCE);
      POTIONS.put("spin", PotionSpin.INSTANCE);
      POTIONS.put("repair", PotionRepair.INSTANCE);
      POTIONS.put("rust", PotionRust.INSTANCE);
      POTIONS.put("extension", PotionExtension.INSTANCE);
      POTIONS.put("chance", PotionChance.INSTANCE);
      POTIONS.put("bless", PotionBless.INSTANCE);
      POTIONS.put("curse", PotionCurse.INSTANCE);
      POTIONS.put("flight", PotionFlight.INSTANCE);
      POTIONS.put("teleport", PotionTeleport.INSTANCE);
      POTIONS.put("teleport_surface", PotionTeleportSurface.INSTANCE);
      POTIONS.put("teleport_spawn", PotionTeleportSpawn.INSTANCE);
      POTIONS.put("invert", PotionInvert.INSTANCE);
      POTIONS.put("revival", PotionRevival.INSTANCE);
      POTIONS.put("reach", PotionReach.INSTANCE);
      POTIONS.put("magic_focus", PotionMagicFocus.INSTANCE);
      POTIONS.put("magic_inhibition", PotionMagicInhibition.INSTANCE);
      POTIONS.put("magic_shield", PotionMagicShield.INSTANCE);
      POTIONS.put("broken_magic_shield", PotionBrokenMagicShield.INSTANCE);
      POTIONS.put("iron_skin", PotionIronSkin.INSTANCE);
      POTIONS.put("broken_armor", PotionBrokenArmor.INSTANCE);
      POTIONS.put("diamond_skin", PotionDiamondSkin.INSTANCE);
      POTIONS.put("potion_sickness", PotionPotionSickness.INSTANCE);
   }
}
