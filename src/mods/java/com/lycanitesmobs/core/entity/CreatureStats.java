package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import java.util.Locale;
import net.minecraft.world.EnumDifficulty;

public class CreatureStats {
   public static int BASE_LEVELUP_EXPERIENCE = 100;
   public static String[] STAT_NAMES = new String[]{"health", "defense", "armor", "speed", "damage", "attackSpeed", "rangedSpeed", "effect", "amplifier", "pierce", "sight"};
   public BaseCreatureEntity entity;

   public CreatureStats(BaseCreatureEntity entity) {
      this.entity = entity;
   }

   public double getHealth() {
      String statName = "health";
      double statValue = this.entity.creatureInfo.health;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierHealth;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostHealth;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return Math.max(0.0D, statValue);
   }

   public double getDefense() {
      String statName = "defense";
      double statValue = this.entity.creatureInfo.defense;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierDefense;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostDefense;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getArmor() {
      String statName = "armor";
      double statValue = this.entity.creatureInfo.armor;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierArmor;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostArmor;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getSpeed() {
      String statName = "speed";
      double statValue = this.entity.creatureInfo.speed / 100.0D;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierSpeed;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostSpeed;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getDamage() {
      String statName = "damage";
      double statValue = this.entity.creatureInfo.damage;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierDamage;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostDamage;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getAttackSpeed() {
      String statName = "attackSpeed";
      double statValue = this.entity.creatureInfo.attackSpeed;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierHaste;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostHaste;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getRangedSpeed() {
      String statName = "rangedSpeed";
      double statValue = this.entity.creatureInfo.rangedSpeed;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierHaste;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostHaste;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getEffect() {
      String statName = "effect";
      double statValue = this.entity.creatureInfo.effectDuration;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierEffect;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostEffect;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getAmplifier() {
      String statName = "amplifier";
      double statValue = this.entity.creatureInfo.effectAmplifier;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getPierce() {
      String statName = "pierce";
      double statValue = this.entity.creatureInfo.pierce;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
         if (this.entity.extraMobBehaviour != null) {
            statValue = statValue * this.entity.extraMobBehaviour.multiplierPierce;
            statValue = statValue + (double)this.entity.extraMobBehaviour.boostPierce;
         }
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public double getSight() {
      String statName = "sight";
      double statValue = this.entity.creatureInfo.sight;
      if (!this.entity.isTamed()) {
         statValue = statValue * this.getDifficultyMultiplier(statName);
         statValue = statValue * this.getVariantMultiplier(statName);
      }

      statValue = statValue * this.getLevelMultiplier(statName);
      return statValue;
   }

   public int BagSize() {
      return this.entity.creatureInfo.bagSize;
   }

   public double getKnockbackResistance() {
      return this.entity.creatureInfo.knockbackResistance;
   }

   protected double getDifficultyMultiplier(String stat) {
      EnumDifficulty difficulty = this.entity.getEntityWorld().getDifficulty();
      String difficultyName = "Easy";
      if (difficulty.getDifficultyId() >= 3) {
         difficultyName = "Hard";
      } else if (difficulty == EnumDifficulty.NORMAL) {
         difficultyName = "Normal";
      }

      return CreatureManager.getInstance().getDifficultyMultiplier(difficultyName.toUpperCase(Locale.ENGLISH), stat.toUpperCase(Locale.ENGLISH));
   }

   protected double getVariantMultiplier(String stat) {
      return this.entity.getVariant() != null && Variant.STAT_MULTIPLIERS.containsKey(this.entity.getVariant().rarity.toUpperCase(Locale.ENGLISH) + "-" + stat.toUpperCase(Locale.ENGLISH)) ? ((Double)Variant.STAT_MULTIPLIERS.get(this.entity.getVariant().rarity.toUpperCase(Locale.ENGLISH) + "-" + stat.toUpperCase(Locale.ENGLISH))).doubleValue() : 1.0D;
   }

   protected double getLevelMultiplier(String stat) {
      double statLevel = (double)Math.max(0, this.entity.getLevel() - 1);
      return 1.0D + statLevel * CreatureManager.getInstance().getLevelMultiplier(stat.toUpperCase(Locale.ENGLISH));
   }

   public int getExperienceForNextLevel() {
      return BASE_LEVELUP_EXPERIENCE + Math.round((float)(BASE_LEVELUP_EXPERIENCE * (this.entity.getLevel() - 1)) * 0.25F);
   }
}
