package com.tmtravlr.potioncore;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tmtravlr.potioncore.potion.PotionAntidote;
import com.tmtravlr.potioncore.potion.PotionArchery;
import com.tmtravlr.potioncore.potion.PotionBless;
import com.tmtravlr.potioncore.potion.PotionBrokenArmor;
import com.tmtravlr.potioncore.potion.PotionBrokenMagicShield;
import com.tmtravlr.potioncore.potion.PotionChance;
import com.tmtravlr.potioncore.potion.PotionClimb;
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
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IForgeRegistry;

public class PotionCoreTypes {
   public static final Multimap<Potion, PotionType> POTION_TYPES = ArrayListMultimap.create();
   public static final List<PotionType> EXTRA_VANILLA_TYPES = new ArrayList();

   public PotionCoreTypes() {
   }

   public static void loadPotionTypes(IForgeRegistry<PotionType> registry) {
      ListIterator<PotionType> it = EXTRA_VANILLA_TYPES.listIterator();

      while(it.hasNext()) {
         PotionType type = (PotionType)it.next();
         if (type.getRegistryName().getResourcePath().equals("saturation")) {
            it.set((new PotionType(MobEffects.SATURATION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.SATURATION, 1, ConfigLoader.saturationPerLevel - 1)})).setRegistryName(MobEffects.SATURATION.getRegistryName().getResourcePath()));
         } else if (type.getRegistryName().getResourcePath().equals("strong_saturation")) {
            it.set((new PotionType(MobEffects.SATURATION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.SATURATION, 1, ConfigLoader.saturationPerLevel * 2 - 1)})).setRegistryName("strong_" + MobEffects.SATURATION.getRegistryName().getResourcePath()));
         }
      }

      EXTRA_VANILLA_TYPES.forEach((typex) -> {
         if (ConfigLoader.extraVanillaPotionTypesEnabled.containsKey(type.getRegistryName()) && ((Boolean)ConfigLoader.extraVanillaPotionTypesEnabled.get(type.getRegistryName())).booleanValue()) {
            registry.register(type);
         }

      });
      PotionCoreEffects.POTIONS.values().forEach((potion) -> {
         if (potion.isEnabled()) {
            POTION_TYPES.get(potion).forEach((type) -> {
               registry.register(type);
            });
         }

      });
   }

   static {
      POTION_TYPES.put(PotionFire.INSTANCE, (new PotionType("fire", new PotionEffect[]{new PotionEffect(PotionFire.INSTANCE, 1)})).setRegistryName("fire"));
      POTION_TYPES.put(PotionFire.INSTANCE, (new PotionType("fire", new PotionEffect[]{new PotionEffect(PotionFire.INSTANCE, 1, 1)})).setRegistryName("strong_fire"));
      POTION_TYPES.put(PotionLightning.INSTANCE, (new PotionType("lightning", new PotionEffect[]{new PotionEffect(PotionLightning.INSTANCE, 1)})).setRegistryName("lightning"));
      POTION_TYPES.put(PotionExplosion.INSTANCE, (new PotionType("explode", new PotionEffect[]{new PotionEffect(PotionExplosion.INSTANCE, 1)})).setRegistryName("explode"));
      POTION_TYPES.put(PotionExplosion.INSTANCE, (new PotionType("explode", new PotionEffect[]{new PotionEffect(PotionExplosion.INSTANCE, 1, 1)})).setRegistryName("strong_explode"));
      POTION_TYPES.put(PotionExplosionSelf.INSTANCE, (new PotionType("burst", new PotionEffect[]{new PotionEffect(PotionExplosionSelf.INSTANCE, 1)})).setRegistryName("burst"));
      POTION_TYPES.put(PotionExplosionSelf.INSTANCE, (new PotionType("burst", new PotionEffect[]{new PotionEffect(PotionExplosionSelf.INSTANCE, 1, 1)})).setRegistryName("strong_burst"));
      POTION_TYPES.put(PotionLaunch.INSTANCE, (new PotionType("launch", new PotionEffect[]{new PotionEffect(PotionLaunch.INSTANCE, 1)})).setRegistryName("launch"));
      POTION_TYPES.put(PotionLaunch.INSTANCE, (new PotionType("launch", new PotionEffect[]{new PotionEffect(PotionLaunch.INSTANCE, 1, 1)})).setRegistryName("strong_launch"));
      POTION_TYPES.put(PotionLove.INSTANCE, (new PotionType("love", new PotionEffect[]{new PotionEffect(PotionLove.INSTANCE, 1)})).setRegistryName("love"));
      POTION_TYPES.put(PotionDisorganization.INSTANCE, (new PotionType("disorganization", new PotionEffect[]{new PotionEffect(PotionDisorganization.INSTANCE, 1)})).setRegistryName("disorganization"));
      POTION_TYPES.put(PotionInvert.INSTANCE, (new PotionType("invert", new PotionEffect[]{new PotionEffect(PotionInvert.INSTANCE, 1)})).setRegistryName("invert"));
      POTION_TYPES.put(PotionCure.INSTANCE, (new PotionType("cure", new PotionEffect[]{new PotionEffect(PotionCure.INSTANCE, 1)})).setRegistryName("cure"));
      POTION_TYPES.put(PotionDispel.INSTANCE, (new PotionType("dispel", new PotionEffect[]{new PotionEffect(PotionDispel.INSTANCE, 1)})).setRegistryName("dispel"));
      POTION_TYPES.put(PotionChance.INSTANCE, (new PotionType("chance", new PotionEffect[]{new PotionEffect(PotionChance.INSTANCE, 1)})).setRegistryName("chance"));
      POTION_TYPES.put(PotionChance.INSTANCE, (new PotionType("chance", new PotionEffect[]{new PotionEffect(PotionChance.INSTANCE, 1, 1)})).setRegistryName("strong_chance"));
      POTION_TYPES.put(PotionBless.INSTANCE, (new PotionType("bless", new PotionEffect[]{new PotionEffect(PotionBless.INSTANCE, 1)})).setRegistryName("bless"));
      POTION_TYPES.put(PotionBless.INSTANCE, (new PotionType("bless", new PotionEffect[]{new PotionEffect(PotionBless.INSTANCE, 1, 1)})).setRegistryName("strong_bless"));
      POTION_TYPES.put(PotionCurse.INSTANCE, (new PotionType("curse", new PotionEffect[]{new PotionEffect(PotionCurse.INSTANCE, 1)})).setRegistryName("curse"));
      POTION_TYPES.put(PotionCurse.INSTANCE, (new PotionType("curse", new PotionEffect[]{new PotionEffect(PotionCurse.INSTANCE, 1, 1)})).setRegistryName("strong_curse"));
      POTION_TYPES.put(PotionTeleport.INSTANCE, (new PotionType("teleport", new PotionEffect[]{new PotionEffect(PotionTeleport.INSTANCE, 1)})).setRegistryName("teleport"));
      POTION_TYPES.put(PotionTeleport.INSTANCE, (new PotionType("teleport", new PotionEffect[]{new PotionEffect(PotionTeleport.INSTANCE, 1, 1)})).setRegistryName("strong_teleport"));
      POTION_TYPES.put(PotionTeleportSurface.INSTANCE, (new PotionType("teleport_surface", new PotionEffect[]{new PotionEffect(PotionTeleportSurface.INSTANCE, 1)})).setRegistryName("teleport_surface"));
      POTION_TYPES.put(PotionTeleportSpawn.INSTANCE, (new PotionType("teleport_spawn", new PotionEffect[]{new PotionEffect(PotionTeleportSpawn.INSTANCE, 600)})).setRegistryName("teleport_spawn"));
      POTION_TYPES.put(PotionWeight.INSTANCE, (new PotionType("weight", new PotionEffect[]{new PotionEffect(PotionWeight.INSTANCE, 3600)})).setRegistryName("weight"));
      POTION_TYPES.put(PotionWeight.INSTANCE, (new PotionType("weight", new PotionEffect[]{new PotionEffect(PotionWeight.INSTANCE, 1800, 1)})).setRegistryName("strong_weight"));
      POTION_TYPES.put(PotionWeight.INSTANCE, (new PotionType("weight", new PotionEffect[]{new PotionEffect(PotionWeight.INSTANCE, 9600)})).setRegistryName("long_weight"));
      POTION_TYPES.put(PotionRecoil.INSTANCE, (new PotionType("recoil", new PotionEffect[]{new PotionEffect(PotionRecoil.INSTANCE, 3600)})).setRegistryName("recoil"));
      POTION_TYPES.put(PotionRecoil.INSTANCE, (new PotionType("recoil", new PotionEffect[]{new PotionEffect(PotionRecoil.INSTANCE, 1800, 1)})).setRegistryName("strong_recoil"));
      POTION_TYPES.put(PotionRecoil.INSTANCE, (new PotionType("recoil", new PotionEffect[]{new PotionEffect(PotionRecoil.INSTANCE, 9600)})).setRegistryName("long_recoil"));
      POTION_TYPES.put(PotionDrown.INSTANCE, (new PotionType("drown", new PotionEffect[]{new PotionEffect(PotionDrown.INSTANCE, 3600)})).setRegistryName("drown"));
      POTION_TYPES.put(PotionDrown.INSTANCE, (new PotionType("drown", new PotionEffect[]{new PotionEffect(PotionDrown.INSTANCE, 9600)})).setRegistryName("long_drown"));
      POTION_TYPES.put(PotionArchery.INSTANCE, (new PotionType("archery", new PotionEffect[]{new PotionEffect(PotionArchery.INSTANCE, 3600)})).setRegistryName("archery"));
      POTION_TYPES.put(PotionArchery.INSTANCE, (new PotionType("archery", new PotionEffect[]{new PotionEffect(PotionArchery.INSTANCE, 1800, 1)})).setRegistryName("strong_archery"));
      POTION_TYPES.put(PotionArchery.INSTANCE, (new PotionType("archery", new PotionEffect[]{new PotionEffect(PotionArchery.INSTANCE, 9600)})).setRegistryName("long_archery"));
      POTION_TYPES.put(PotionKlutz.INSTANCE, (new PotionType("klutz", new PotionEffect[]{new PotionEffect(PotionKlutz.INSTANCE, 3600)})).setRegistryName("klutz"));
      POTION_TYPES.put(PotionKlutz.INSTANCE, (new PotionType("klutz", new PotionEffect[]{new PotionEffect(PotionKlutz.INSTANCE, 1800, 1)})).setRegistryName("strong_klutz"));
      POTION_TYPES.put(PotionKlutz.INSTANCE, (new PotionType("klutz", new PotionEffect[]{new PotionEffect(PotionKlutz.INSTANCE, 9600)})).setRegistryName("long_klutz"));
      POTION_TYPES.put(PotionVulnerable.INSTANCE, (new PotionType("vulnerable", new PotionEffect[]{new PotionEffect(PotionVulnerable.INSTANCE, 3600)})).setRegistryName("vulnerable"));
      POTION_TYPES.put(PotionVulnerable.INSTANCE, (new PotionType("vulnerable", new PotionEffect[]{new PotionEffect(PotionVulnerable.INSTANCE, 1800, 1)})).setRegistryName("strong_vulnerable"));
      POTION_TYPES.put(PotionVulnerable.INSTANCE, (new PotionType("vulnerable", new PotionEffect[]{new PotionEffect(PotionVulnerable.INSTANCE, 9600)})).setRegistryName("long_vulnerable"));
      POTION_TYPES.put(PotionAntidote.INSTANCE, (new PotionType("antidote", new PotionEffect[]{new PotionEffect(PotionAntidote.INSTANCE, 3600)})).setRegistryName("antidote"));
      POTION_TYPES.put(PotionAntidote.INSTANCE, (new PotionType("antidote", new PotionEffect[]{new PotionEffect(PotionAntidote.INSTANCE, 9600)})).setRegistryName("long_antidote"));
      POTION_TYPES.put(PotionPurity.INSTANCE, (new PotionType("purity", new PotionEffect[]{new PotionEffect(PotionPurity.INSTANCE, 3600)})).setRegistryName("purity"));
      POTION_TYPES.put(PotionPurity.INSTANCE, (new PotionType("purity", new PotionEffect[]{new PotionEffect(PotionPurity.INSTANCE, 9600)})).setRegistryName("long_purity"));
      POTION_TYPES.put(PotionSlowfall.INSTANCE, (new PotionType("slow_fall", new PotionEffect[]{new PotionEffect(PotionSlowfall.INSTANCE, 3600)})).setRegistryName("slow_fall"));
      POTION_TYPES.put(PotionSlowfall.INSTANCE, (new PotionType("slow_fall", new PotionEffect[]{new PotionEffect(PotionSlowfall.INSTANCE, 1800, 1)})).setRegistryName("strong_slow_fall"));
      POTION_TYPES.put(PotionSlowfall.INSTANCE, (new PotionType("slow_fall", new PotionEffect[]{new PotionEffect(PotionSlowfall.INSTANCE, 9600)})).setRegistryName("long_slow_fall"));
      POTION_TYPES.put(PotionSolidCore.INSTANCE, (new PotionType("solid_core", new PotionEffect[]{new PotionEffect(PotionSolidCore.INSTANCE, 3600)})).setRegistryName("solid_core"));
      POTION_TYPES.put(PotionSolidCore.INSTANCE, (new PotionType("solid_core", new PotionEffect[]{new PotionEffect(PotionSolidCore.INSTANCE, 9600)})).setRegistryName("long_solid_core"));
      POTION_TYPES.put(PotionClimb.INSTANCE, (new PotionType("climb", new PotionEffect[]{new PotionEffect(PotionClimb.INSTANCE, 3600)})).setRegistryName("climb"));
      POTION_TYPES.put(PotionClimb.INSTANCE, (new PotionType("climb", new PotionEffect[]{new PotionEffect(PotionClimb.INSTANCE, 9600)})).setRegistryName("long_climb"));
      POTION_TYPES.put(PotionStepup.INSTANCE, (new PotionType("step_up", new PotionEffect[]{new PotionEffect(PotionStepup.INSTANCE, 3600)})).setRegistryName("step_up"));
      POTION_TYPES.put(PotionStepup.INSTANCE, (new PotionType("step_up", new PotionEffect[]{new PotionEffect(PotionStepup.INSTANCE, 1800, 1)})).setRegistryName("strong_step_up"));
      POTION_TYPES.put(PotionStepup.INSTANCE, (new PotionType("step_up", new PotionEffect[]{new PotionEffect(PotionStepup.INSTANCE, 9600)})).setRegistryName("long_step_up"));
      POTION_TYPES.put(PotionPerplexity.INSTANCE, (new PotionType("perplexity", new PotionEffect[]{new PotionEffect(PotionPerplexity.INSTANCE, 3600)})).setRegistryName("perplexity"));
      POTION_TYPES.put(PotionPerplexity.INSTANCE, (new PotionType("perplexity", new PotionEffect[]{new PotionEffect(PotionPerplexity.INSTANCE, 9600)})).setRegistryName("long_perplexity"));
      POTION_TYPES.put(PotionSpin.INSTANCE, (new PotionType("spin", new PotionEffect[]{new PotionEffect(PotionSpin.INSTANCE, 900)})).setRegistryName("spin"));
      POTION_TYPES.put(PotionSpin.INSTANCE, (new PotionType("spin", new PotionEffect[]{new PotionEffect(PotionSpin.INSTANCE, 450, 1)})).setRegistryName("strong_spin"));
      POTION_TYPES.put(PotionSpin.INSTANCE, (new PotionType("spin", new PotionEffect[]{new PotionEffect(PotionSpin.INSTANCE, 1800)})).setRegistryName("long_spin"));
      POTION_TYPES.put(PotionRepair.INSTANCE, (new PotionType("repair", new PotionEffect[]{new PotionEffect(PotionRepair.INSTANCE, 3600)})).setRegistryName("repair"));
      POTION_TYPES.put(PotionRepair.INSTANCE, (new PotionType("repair", new PotionEffect[]{new PotionEffect(PotionRepair.INSTANCE, 1800, 1)})).setRegistryName("strong_repair"));
      POTION_TYPES.put(PotionRepair.INSTANCE, (new PotionType("repair", new PotionEffect[]{new PotionEffect(PotionRepair.INSTANCE, 9600)})).setRegistryName("long_repair"));
      POTION_TYPES.put(PotionRust.INSTANCE, (new PotionType("rust", new PotionEffect[]{new PotionEffect(PotionRust.INSTANCE, 3600)})).setRegistryName("rust"));
      POTION_TYPES.put(PotionRust.INSTANCE, (new PotionType("rust", new PotionEffect[]{new PotionEffect(PotionRust.INSTANCE, 1800, 1)})).setRegistryName("strong_rust"));
      POTION_TYPES.put(PotionRust.INSTANCE, (new PotionType("rust", new PotionEffect[]{new PotionEffect(PotionRust.INSTANCE, 9600)})).setRegistryName("long_rust"));
      POTION_TYPES.put(PotionExtension.INSTANCE, (new PotionType("extension", new PotionEffect[]{new PotionEffect(PotionExtension.INSTANCE, 3600)})).setRegistryName("extension"));
      POTION_TYPES.put(PotionExtension.INSTANCE, (new PotionType("extension", new PotionEffect[]{new PotionEffect(PotionExtension.INSTANCE, 1800, 1)})).setRegistryName("strong_extension"));
      POTION_TYPES.put(PotionExtension.INSTANCE, (new PotionType("extension", new PotionEffect[]{new PotionEffect(PotionExtension.INSTANCE, 9600)})).setRegistryName("long_extension"));
      POTION_TYPES.put(PotionFlight.INSTANCE, (new PotionType("flight", new PotionEffect[]{new PotionEffect(PotionFlight.INSTANCE, 3600)})).setRegistryName("flight"));
      POTION_TYPES.put(PotionFlight.INSTANCE, (new PotionType("flight", new PotionEffect[]{new PotionEffect(PotionFlight.INSTANCE, 9600)})).setRegistryName("long_flight"));
      POTION_TYPES.put(PotionRevival.INSTANCE, (new PotionType("revival", new PotionEffect[]{new PotionEffect(PotionRevival.INSTANCE, 3600)})).setRegistryName("revival"));
      POTION_TYPES.put(PotionRevival.INSTANCE, (new PotionType("revival", new PotionEffect[]{new PotionEffect(PotionRevival.INSTANCE, 1800, 1)})).setRegistryName("strong_revival"));
      POTION_TYPES.put(PotionRevival.INSTANCE, (new PotionType("revival", new PotionEffect[]{new PotionEffect(PotionRevival.INSTANCE, 9600)})).setRegistryName("long_revival"));
      POTION_TYPES.put(PotionReach.INSTANCE, (new PotionType("reach", new PotionEffect[]{new PotionEffect(PotionReach.INSTANCE, 3600)})).setRegistryName("reach"));
      POTION_TYPES.put(PotionReach.INSTANCE, (new PotionType("reach", new PotionEffect[]{new PotionEffect(PotionReach.INSTANCE, 1800, 1)})).setRegistryName("strong_reach"));
      POTION_TYPES.put(PotionReach.INSTANCE, (new PotionType("reach", new PotionEffect[]{new PotionEffect(PotionReach.INSTANCE, 9600)})).setRegistryName("long_reach"));
      POTION_TYPES.put(PotionMagicFocus.INSTANCE, (new PotionType("magic_focus", new PotionEffect[]{new PotionEffect(PotionMagicFocus.INSTANCE, 3600)})).setRegistryName("magic_focus"));
      POTION_TYPES.put(PotionMagicFocus.INSTANCE, (new PotionType("magic_focus", new PotionEffect[]{new PotionEffect(PotionMagicFocus.INSTANCE, 1800, 1)})).setRegistryName("strong_magic_focus"));
      POTION_TYPES.put(PotionMagicFocus.INSTANCE, (new PotionType("magic_focus", new PotionEffect[]{new PotionEffect(PotionMagicFocus.INSTANCE, 9600)})).setRegistryName("long_magic_focus"));
      POTION_TYPES.put(PotionMagicInhibition.INSTANCE, (new PotionType("magic_inhibition", new PotionEffect[]{new PotionEffect(PotionMagicInhibition.INSTANCE, 3600)})).setRegistryName("magic_inhibition"));
      POTION_TYPES.put(PotionMagicInhibition.INSTANCE, (new PotionType("magic_inhibition", new PotionEffect[]{new PotionEffect(PotionMagicInhibition.INSTANCE, 1800, 1)})).setRegistryName("strong_magic_inhibition"));
      POTION_TYPES.put(PotionMagicInhibition.INSTANCE, (new PotionType("magic_inhibition", new PotionEffect[]{new PotionEffect(PotionMagicInhibition.INSTANCE, 9600)})).setRegistryName("long_magic_inhibition"));
      POTION_TYPES.put(PotionMagicShield.INSTANCE, (new PotionType("magic_shield", new PotionEffect[]{new PotionEffect(PotionMagicShield.INSTANCE, 3600)})).setRegistryName("magic_shield"));
      POTION_TYPES.put(PotionMagicShield.INSTANCE, (new PotionType("magic_shield", new PotionEffect[]{new PotionEffect(PotionMagicShield.INSTANCE, 1800, 1)})).setRegistryName("strong_magic_shield"));
      POTION_TYPES.put(PotionMagicShield.INSTANCE, (new PotionType("magic_shield", new PotionEffect[]{new PotionEffect(PotionMagicShield.INSTANCE, 9600)})).setRegistryName("long_magic_shield"));
      POTION_TYPES.put(PotionBrokenMagicShield.INSTANCE, (new PotionType("broken_magic_shield", new PotionEffect[]{new PotionEffect(PotionBrokenMagicShield.INSTANCE, 900)})).setRegistryName("broken_magic_shield"));
      POTION_TYPES.put(PotionBrokenMagicShield.INSTANCE, (new PotionType("broken_magic_shield", new PotionEffect[]{new PotionEffect(PotionBrokenMagicShield.INSTANCE, 450, 1)})).setRegistryName("strong_broken_magic_shield"));
      POTION_TYPES.put(PotionBrokenMagicShield.INSTANCE, (new PotionType("broken_magic_shield", new PotionEffect[]{new PotionEffect(PotionBrokenMagicShield.INSTANCE, 1800)})).setRegistryName("long_broken_magic_shield"));
      POTION_TYPES.put(PotionIronSkin.INSTANCE, (new PotionType("iron_skin", new PotionEffect[]{new PotionEffect(PotionIronSkin.INSTANCE, 3600)})).setRegistryName("iron_skin"));
      POTION_TYPES.put(PotionIronSkin.INSTANCE, (new PotionType("iron_skin", new PotionEffect[]{new PotionEffect(PotionIronSkin.INSTANCE, 1800, 1)})).setRegistryName("strong_iron_skin"));
      POTION_TYPES.put(PotionIronSkin.INSTANCE, (new PotionType("iron_skin", new PotionEffect[]{new PotionEffect(PotionIronSkin.INSTANCE, 9600)})).setRegistryName("long_iron_skin"));
      POTION_TYPES.put(PotionBrokenArmor.INSTANCE, (new PotionType("broken_armor", new PotionEffect[]{new PotionEffect(PotionBrokenArmor.INSTANCE, 900)})).setRegistryName("broken_armor"));
      POTION_TYPES.put(PotionBrokenArmor.INSTANCE, (new PotionType("broken_armor", new PotionEffect[]{new PotionEffect(PotionBrokenArmor.INSTANCE, 450, 1)})).setRegistryName("strong_broken_armor"));
      POTION_TYPES.put(PotionBrokenArmor.INSTANCE, (new PotionType("broken_armor", new PotionEffect[]{new PotionEffect(PotionBrokenArmor.INSTANCE, 1800)})).setRegistryName("long_broken_armor"));
      POTION_TYPES.put(PotionDiamondSkin.INSTANCE, (new PotionType("diamond_skin", new PotionEffect[]{new PotionEffect(PotionDiamondSkin.INSTANCE, 3600)})).setRegistryName("diamond_skin"));
      POTION_TYPES.put(PotionDiamondSkin.INSTANCE, (new PotionType("diamond_skin", new PotionEffect[]{new PotionEffect(PotionDiamondSkin.INSTANCE, 1800, 1)})).setRegistryName("strong_diamond_skin"));
      POTION_TYPES.put(PotionDiamondSkin.INSTANCE, (new PotionType("diamond_skin", new PotionEffect[]{new PotionEffect(PotionDiamondSkin.INSTANCE, 9600)})).setRegistryName("long_diamond_skin"));
      POTION_TYPES.put(PotionPotionSickness.INSTANCE, (new PotionType("potion_sickness", new PotionEffect[]{new PotionEffect(PotionPotionSickness.INSTANCE, 3600)})).setRegistryName("potion_sickness"));
      POTION_TYPES.put(PotionPotionSickness.INSTANCE, (new PotionType("potion_sickness", new PotionEffect[]{new PotionEffect(PotionPotionSickness.INSTANCE, 1800, 1)})).setRegistryName("strong_potion_sickness"));
      POTION_TYPES.put(PotionPotionSickness.INSTANCE, (new PotionType("potion_sickness", new PotionEffect[]{new PotionEffect(PotionPotionSickness.INSTANCE, 9600)})).setRegistryName("long_potion_sickness"));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.SATURATION.getRegistryName().getResourcePath(), new PotionEffect[0])).setRegistryName(MobEffects.SATURATION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.SATURATION.getRegistryName().getResourcePath(), new PotionEffect[0])).setRegistryName("strong_" + MobEffects.SATURATION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.WITHER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.WITHER, 900)})).setRegistryName(MobEffects.WITHER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.WITHER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.WITHER, 450, 1)})).setRegistryName("strong_" + MobEffects.WITHER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.WITHER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.WITHER, 1800)})).setRegistryName("long_" + MobEffects.WITHER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HUNGER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 900)})).setRegistryName(MobEffects.HUNGER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HUNGER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 450, 1)})).setRegistryName("strong_" + MobEffects.HUNGER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HUNGER.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 1800)})).setRegistryName("long_" + MobEffects.HUNGER.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.BLINDNESS.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.BLINDNESS, 900)})).setRegistryName(MobEffects.BLINDNESS.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.BLINDNESS.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.BLINDNESS, 450, 1)})).setRegistryName("strong_" + MobEffects.BLINDNESS.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.BLINDNESS.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.BLINDNESS, 1800)})).setRegistryName("long_" + MobEffects.BLINDNESS.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.NAUSEA.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.NAUSEA, 900)})).setRegistryName(MobEffects.NAUSEA.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.NAUSEA.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.NAUSEA, 1800)})).setRegistryName("long_" + MobEffects.NAUSEA.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.LEVITATION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.LEVITATION, 600)})).setRegistryName(MobEffects.LEVITATION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.LEVITATION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.LEVITATION, 300, 1)})).setRegistryName("strong_" + MobEffects.LEVITATION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.LEVITATION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.LEVITATION, 1200)})).setRegistryName("long_" + MobEffects.LEVITATION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.ABSORPTION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.ABSORPTION, 3600)})).setRegistryName(MobEffects.ABSORPTION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.ABSORPTION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.ABSORPTION, 1800, 1)})).setRegistryName("strong_" + MobEffects.ABSORPTION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.ABSORPTION.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.ABSORPTION, 9600)})).setRegistryName("long_" + MobEffects.ABSORPTION.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.GLOWING.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.GLOWING, 3600)})).setRegistryName(MobEffects.GLOWING.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.GLOWING.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.GLOWING, 9600)})).setRegistryName("long_" + MobEffects.GLOWING.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HASTE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HASTE, 3600)})).setRegistryName(MobEffects.HASTE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HASTE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HASTE, 1800, 1)})).setRegistryName("strong_" + MobEffects.HASTE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HASTE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HASTE, 9600)})).setRegistryName("long_" + MobEffects.HASTE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.MINING_FATIGUE, 3600)})).setRegistryName(MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.MINING_FATIGUE, 1800, 1)})).setRegistryName("strong_" + MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.MINING_FATIGUE, 9600)})).setRegistryName("long_" + MobEffects.MINING_FATIGUE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HEALTH_BOOST, 3600)})).setRegistryName(MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HEALTH_BOOST, 1800, 1)})).setRegistryName("strong_" + MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.HEALTH_BOOST, 9600)})).setRegistryName("long_" + MobEffects.HEALTH_BOOST.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.RESISTANCE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.RESISTANCE, 3600)})).setRegistryName(MobEffects.RESISTANCE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.RESISTANCE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.RESISTANCE, 1800, 1)})).setRegistryName("strong_" + MobEffects.RESISTANCE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.RESISTANCE.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.RESISTANCE, 9600)})).setRegistryName("long_" + MobEffects.RESISTANCE.getRegistryName().getResourcePath()));
      EXTRA_VANILLA_TYPES.add((new PotionType(MobEffects.UNLUCK.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(MobEffects.UNLUCK, 6000)})).setRegistryName(MobEffects.UNLUCK.getRegistryName().getResourcePath()));
   }
}
