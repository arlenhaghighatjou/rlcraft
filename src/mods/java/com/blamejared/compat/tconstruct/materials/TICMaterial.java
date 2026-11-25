package com.blamejared.compat.tconstruct.materials;

import com.blamejared.compat.tconstruct.actions.SetAttackAction;
import com.blamejared.compat.tconstruct.actions.SetBowBonusDamageAction;
import com.blamejared.compat.tconstruct.actions.SetBowRangeAction;
import com.blamejared.compat.tconstruct.actions.SetDrawSpeedAction;
import com.blamejared.compat.tconstruct.actions.SetDurabilityAction;
import com.blamejared.compat.tconstruct.actions.SetHarvestLevelAction;
import com.blamejared.compat.tconstruct.actions.SetMiningSpeedAction;
import com.blamejared.compat.tconstruct.actions.SetModifierAction;
import crafttweaker.CraftTweakerAPI;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class TICMaterial implements ITICMaterial {
   private final Material material;

   public TICMaterial(Material material) {
      this.material = material;
   }

   public String getName() {
      return this.material.getIdentifier();
   }

   public boolean matches(ITICMaterial var1) {
      return var1.getName().equals(this.getName());
   }

   public Object getInternal() {
      return this.material;
   }

   public ITICMaterialDefinition getDefinition() {
      return new TICMaterialDefinition(this.material);
   }

   public boolean hasStats(String stat) {
      return this.material.hasStats(stat);
   }

   public void setDurabilityHead(int durability) {
      CraftTweakerAPI.apply(new SetDurabilityAction(this, "head", durability));
   }

   public int getDurabilityHead() {
      return ((HeadMaterialStats)this.material.getStats("head")).durability;
   }

   public void setMiningSpeedHead(float miningSpeed) {
      CraftTweakerAPI.apply(new SetMiningSpeedAction(this, "head", miningSpeed));
   }

   public float getMiningSpeedHead() {
      return ((HeadMaterialStats)this.material.getStats("head")).miningspeed;
   }

   public void setAttackHead(float attack) {
      CraftTweakerAPI.apply(new SetAttackAction(this, "head", attack));
   }

   public float getAttackHead() {
      return ((HeadMaterialStats)this.material.getStats("head")).attack;
   }

   public void setHarvestLevelHead(int level) {
      CraftTweakerAPI.apply(new SetHarvestLevelAction(this, "head", level));
   }

   public int getHarvestLevelHead() {
      return ((HeadMaterialStats)this.material.getStats("head")).harvestLevel;
   }

   public void setDurabilityHandle(int durability) {
      CraftTweakerAPI.apply(new SetDurabilityAction(this, "handle", durability));
   }

   public int getDurabilityHandle() {
      return ((HandleMaterialStats)this.material.getStats("handle")).durability;
   }

   public void setModifierHandle(float modifier) {
      CraftTweakerAPI.apply(new SetModifierAction(this, "handle", modifier));
   }

   public float getModifierHandle() {
      return ((HandleMaterialStats)this.material.getStats("handle")).modifier;
   }

   public void setDurabilityExtra(int durability) {
      CraftTweakerAPI.apply(new SetDurabilityAction(this, "extra", durability));
   }

   public int getDurabilityExtra() {
      return ((ExtraMaterialStats)this.material.getStats("extra")).extraDurability;
   }

   public void setArrowModifier(float modifier) {
      CraftTweakerAPI.apply(new SetModifierAction(this, "shaft", modifier));
   }

   public float getArrowModifier() {
      return ((ArrowShaftMaterialStats)this.material.getStats("shaft")).modifier;
   }

   public void setArrowBonusAmmo(int bonusAmmo) {
      CraftTweakerAPI.apply(new SetDurabilityAction(this, "shaft", bonusAmmo));
   }

   public int getArrowBonusAmmo() {
      return ((ArrowShaftMaterialStats)this.material.getStats("shaft")).bonusAmmo;
   }

   public void setFletchingModifier(float modifier) {
      CraftTweakerAPI.apply(new SetModifierAction(this, "fletching", modifier));
   }

   public float getFletchingModifier() {
      return ((FletchingMaterialStats)this.material.getStats("fletching")).modifier;
   }

   public void setFletchingAccuracy(float accuracy) {
      CraftTweakerAPI.apply(new SetAttackAction(this, "fletching", accuracy));
   }

   public float getFletchingAccuracy() {
      return ((FletchingMaterialStats)this.material.getStats("fletching")).accuracy;
   }

   public float getBowDrawSpeed() {
      return ((BowMaterialStats)this.material.getStats("bow")).drawspeed;
   }

   public void setBowDrawSpeed(float drawSpeed) {
      CraftTweakerAPI.apply(new SetDrawSpeedAction(this, "bow", drawSpeed));
   }

   public float getBowRange() {
      return ((BowMaterialStats)this.material.getStats("bow")).range;
   }

   public void setBowRange(float range) {
      CraftTweakerAPI.apply(new SetBowRangeAction(this, "bow", range));
   }

   public float getBonusDamage() {
      return ((BowMaterialStats)this.material.getStats("bow")).bonusDamage;
   }

   public void setBonusDamage(float bonusDamage) {
      CraftTweakerAPI.apply(new SetBowBonusDamageAction(this, "bow", bonusDamage));
   }

   public float getBowStringModifier() {
      return ((BowStringMaterialStats)this.material.getStats("bowstring")).modifier;
   }

   public void setBowStringModifier(float modifier) {
      CraftTweakerAPI.apply(new SetModifierAction(this, "bowstring", modifier));
   }
}
