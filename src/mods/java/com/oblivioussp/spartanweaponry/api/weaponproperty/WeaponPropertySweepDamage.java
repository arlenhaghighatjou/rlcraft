package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class WeaponPropertySweepDamage extends WeaponPropertyWithMagnitude {
   public WeaponPropertySweepDamage(String propType, String propModId, int propLevel, float propMagnitude) {
      super(propType, propModId, propLevel, propMagnitude);
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      if (this.level == 1) {
         tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc.normal", "tooltip", this.modId, this.magnitude));
      } else {
         super.addTooltipDescription(stack, tooltip);
      }

   }
}
