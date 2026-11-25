package com.blamejared.compat.tconstruct.actions;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import crafttweaker.IAction;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class SetAttackAction implements IAction {
   private final ITICMaterial material;
   private final String stat;
   private final float newValue;

   public SetAttackAction(ITICMaterial material, String stat, float newValue) {
      this.material = material;
      this.stat = stat;
      this.newValue = newValue;
   }

   private static void set(Material material, String stat, float attack) {
      IMaterialStats oldStat = material.getStats(stat);
      if (oldStat instanceof HeadMaterialStats) {
         HeadMaterialStats headStat = (HeadMaterialStats)oldStat;
         HeadMaterialStats newHead = new HeadMaterialStats(headStat.durability, headStat.miningspeed, attack, headStat.harvestLevel);
         material.addStats(newHead);
      } else if (oldStat instanceof FletchingMaterialStats) {
         FletchingMaterialStats fletch = (FletchingMaterialStats)oldStat;
         FletchingMaterialStats newShaft = new FletchingMaterialStats(attack, fletch.modifier);
         material.addStats(newShaft);
      }

   }

   public void apply() {
      set((Material)this.material.getInternal(), this.stat, this.newValue);
   }

   public String describe() {
      return "Setting Attack of " + this.material.getName() + " to " + this.newValue + " for " + this.stat;
   }
}
