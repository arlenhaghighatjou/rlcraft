package com.blamejared.compat.tconstruct.actions;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import crafttweaker.IAction;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class SetBowRangeAction implements IAction {
   private final ITICMaterial material;
   private final String stat;
   private final float newValue;

   public SetBowRangeAction(ITICMaterial material, String stat, float newValue) {
      this.material = material;
      this.stat = stat;
      this.newValue = newValue;
   }

   private static void set(Material material, String stat, float range) {
      IMaterialStats oldStat = material.getStats(stat);
      if (oldStat instanceof BowMaterialStats) {
         BowMaterialStats bowStat = (BowMaterialStats)oldStat;
         BowMaterialStats newBow = new BowMaterialStats(bowStat.drawspeed, range, bowStat.bonusDamage);
         material.addStats(newBow);
      }

   }

   public void apply() {
      set((Material)this.material.getInternal(), this.stat, this.newValue);
   }

   public String describe() {
      return "Setting Range of " + this.material.getName() + " to " + this.newValue + " for " + this.stat;
   }
}
