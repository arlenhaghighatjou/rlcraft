package com.blamejared.compat.tconstruct.actions;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import crafttweaker.IAction;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class SetMiningSpeedAction implements IAction {
   private final ITICMaterial material;
   private final String stat;
   private final float newValue;

   public SetMiningSpeedAction(ITICMaterial material, String stat, float newValue) {
      this.material = material;
      this.stat = stat;
      this.newValue = newValue;
   }

   private static void set(Material material, String stat, float miningSpeed) {
      IMaterialStats oldStat = material.getStats(stat);
      if (oldStat instanceof HeadMaterialStats) {
         HeadMaterialStats headStat = (HeadMaterialStats)oldStat;
         HeadMaterialStats newHead = new HeadMaterialStats(headStat.durability, miningSpeed, headStat.attack, headStat.harvestLevel);
         material.addStats(newHead);
      }

   }

   public void apply() {
      set((Material)this.material.getInternal(), this.stat, this.newValue);
   }

   public String describe() {
      return "Setting MiningSpeed of " + this.material.getName() + " to " + this.newValue + " for " + this.stat;
   }
}
