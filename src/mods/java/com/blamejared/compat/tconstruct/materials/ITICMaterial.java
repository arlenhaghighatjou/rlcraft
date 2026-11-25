package com.blamejared.compat.tconstruct.materials;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("modtweaker.tconstruct.ITICMaterial")
@ZenRegister
public interface ITICMaterial {
   @ZenGetter
   String getName();

   @ZenMethod
   boolean matches(ITICMaterial var1);

   Object getInternal();

   @ZenGetter("definition")
   ITICMaterialDefinition getDefinition();

   @ZenMethod
   boolean hasStats(String var1);

   @ZenMethod
   default boolean hasHeadStats() {
      return this.hasStats("head");
   }

   @ZenMethod
   default boolean hasHandleStats() {
      return this.hasStats("handle");
   }

   @ZenMethod
   default boolean hasExtraStats() {
      return this.hasStats("extra");
   }

   @ZenMethod
   default boolean hasArrowStats() {
      return this.hasStats("shaft");
   }

   @ZenMethod
   default boolean hasFletchingStats() {
      return this.hasStats("fletching");
   }

   @ZenMethod
   default boolean hasBowStats() {
      return this.hasStats("bow");
   }

   @ZenMethod
   default boolean hasBowStringStats() {
      return this.hasStats("bowstring");
   }

   @ZenSetter("durabilityHead")
   void setDurabilityHead(int var1);

   @ZenGetter("durabilityHead")
   int getDurabilityHead();

   @ZenSetter("miningSpeedHead")
   void setMiningSpeedHead(float var1);

   @ZenGetter("miningSpeedHead")
   float getMiningSpeedHead();

   @ZenSetter("attackHead")
   void setAttackHead(float var1);

   @ZenGetter("attackHead")
   float getAttackHead();

   @ZenSetter("harvestLevelHead")
   void setHarvestLevelHead(int var1);

   @ZenGetter("harvestLevelHead")
   int getHarvestLevelHead();

   @ZenSetter("durabilityHandle")
   void setDurabilityHandle(int var1);

   @ZenGetter("durabilityHandle")
   int getDurabilityHandle();

   @ZenSetter("modifierHandle")
   void setModifierHandle(float var1);

   @ZenGetter("modifierHandle")
   float getModifierHandle();

   @ZenSetter("durabilityExtra")
   void setDurabilityExtra(int var1);

   @ZenGetter("durabilityExtra")
   int getDurabilityExtra();

   @ZenSetter("arrowModifier")
   void setArrowModifier(float var1);

   @ZenGetter("arrowModifier")
   float getArrowModifier();

   @ZenSetter("arrowBonusAmmo")
   void setArrowBonusAmmo(int var1);

   @ZenGetter("arrowBonusAmmo")
   int getArrowBonusAmmo();

   @ZenSetter("fletchingModifier")
   void setFletchingModifier(float var1);

   @ZenGetter("fletchingModifier")
   float getFletchingModifier();

   @ZenSetter("fletchingAccuracy")
   void setFletchingAccuracy(float var1);

   @ZenGetter("fletchingAccuracy")
   float getFletchingAccuracy();

   @ZenGetter("bowDrawSpeed")
   float getBowDrawSpeed();

   @ZenSetter("bowDrawSpeed")
   void setBowDrawSpeed(float var1);

   @ZenGetter("bowRange")
   float getBowRange();

   @ZenSetter("bowRange")
   void setBowRange(float var1);

   @ZenGetter("bonusDamage")
   float getBonusDamage();

   @ZenSetter("bonusDamage")
   void setBonusDamage(float var1);

   @ZenGetter("bowStringModifier")
   float getBowStringModifier();

   @ZenSetter("bowStringModifier")
   void setBowStringModifier(float var1);
}
