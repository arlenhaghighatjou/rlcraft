package xzeroair.trinkets.init;

import javax.annotation.Nullable;
import xzeroair.trinkets.traits.abilities.AbilityNightVision;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;

public class Abilities {
   public static final String nightVision = "night_vision";
   public static final String creativeFlight = "creative_flight";
   public static final String fireBreathing = "fire_breathing";
   public static final String fireImmunity = "fire_immunity";
   public static final String iceImmunity = "ice_immunity";
   public static final String blockDetection = "block_detection";
   public static final String blockClimbing = "block_climbing";
   public static final String witherImmunity = "wither_immunity";
   public static final String weightless = "weightless";
   public static final String wellRested = "well_rested";
   public static final String poisonAffinity = "poison_affinity";
   public static final String waterAffinity = "water_affinity";
   public static final String fallResistance = "fall_resistance";
   public static final String nullKinetic = "nullify_kinetic";
   public static final String reduceKinetic = "reduce_kinetic";
   public static final String safeGuard = "safe_guard";
   public static final String enderQueen = "ender_queen";
   public static final String viciousStrike = "vicious_strike";
   public static final String magnetic = "magnetic";
   public static final String repel = "repel";
   public static final String skilledMiner = "skilled_miner";
   public static final String psudoFortune = "psudo_fortune";
   public static final String chargedShot = "charged_shot";
   public static final String lightningBolt = "lightning_bolt";
   public static final String dodging = "dodging";
   public static final String frostWalker = "frost_walker";
   public static final String largeHands = "large_hands";
   public static final String heavy = "heavy";
   public static final String mountEnhancement = "mount_enhancement";
   public static final String survivalHeatImmunity = "heat_immunity";
   public static final String survivalColdImmunity = "cold_immunity";
   public static final String survivalThirstImmunity = "thirst_immunity";
   public static final String survivalParasitesImmunity = "parasites_immunity";
   public static final String firstAidReflex = "firstaid_reflex";

   public Abilities() {
   }

   @Nullable
   public static final IAbilityInterface getAbility(String ability) {
      byte var2 = -1;
      switch(ability.hashCode()) {
      case -1828732322:
         if (ability.equals("xat:night_vision")) {
            var2 = 0;
         }
      default:
         switch(var2) {
         case 0:
            return new AbilityNightVision();
         default:
            return null;
         }
      }
   }
}
