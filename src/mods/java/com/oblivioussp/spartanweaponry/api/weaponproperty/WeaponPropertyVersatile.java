package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class WeaponPropertyVersatile extends WeaponProperty {
   public WeaponPropertyVersatile(String propType, String propModId) {
      super(propType, propModId);
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      Set<String> toolClasses = stack.getItem().getToolClasses(stack);
      String types = "";
      Iterator<String> it = toolClasses.iterator();

      for(int i = 0; i < toolClasses.size(); ++i) {
         String toolClass = (String)it.next();
         types = types + (i > 0 ? " & " : "") + SpartanWeaponryAPI.internalHandler.translateString("versatile." + toolClass, "tooltip", this.modId);
      }

      tooltip.add(TextFormatting.ITALIC + "   " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, types));
   }
}
