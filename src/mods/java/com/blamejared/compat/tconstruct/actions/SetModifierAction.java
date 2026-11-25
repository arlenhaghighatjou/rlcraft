package com.blamejared.compat.tconstruct.actions;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import crafttweaker.IAction;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class SetModifierAction implements IAction {
   private final ITICMaterial material;
   private final String stat;
   private final float newValue;

   public SetModifierAction(ITICMaterial material, String stat, float newValue) {
      this.material = material;
      this.stat = stat;
      this.newValue = newValue;
   }

   private static void set(Material material, String stat, float modifier) {
      IMaterialStats oldStat = material.getStats(stat);
      if (oldStat instanceof HandleMaterialStats) {
         HandleMaterialStats handleStat = (HandleMaterialStats)oldStat;
         HandleMaterialStats newHandle = new HandleMaterialStats(modifier, handleStat.durability);
         material.addStats(newHandle);
      } else if (oldStat instanceof ArrowShaftMaterialStats) {
         ArrowShaftMaterialStats shaftStat = (ArrowShaftMaterialStats)oldStat;
         ArrowShaftMaterialStats newShaft = new ArrowShaftMaterialStats(modifier, shaftStat.bonusAmmo);
         material.addStats(newShaft);
      } else if (oldStat instanceof FletchingMaterialStats) {
         FletchingMaterialStats fletch = (FletchingMaterialStats)oldStat;
         FletchingMaterialStats newShaft = new FletchingMaterialStats(fletch.accuracy, modifier);
         material.addStats(newShaft);
      } else if (oldStat instanceof BowStringMaterialStats) {
         BowStringMaterialStats newBowString = new BowStringMaterialStats(modifier);
         material.addStats(newBowString);
      }

   }

   public void apply() {
      set((Material)this.material.getInternal(), this.stat, this.newValue);
   }

   public String describe() {
      return "Setting Modifier of " + this.material.getName() + " to " + this.newValue + " for " + this.stat;
   }
}
