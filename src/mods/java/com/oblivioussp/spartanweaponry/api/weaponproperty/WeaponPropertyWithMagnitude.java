package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class WeaponPropertyWithMagnitude extends WeaponProperty {
   public WeaponPropertyWithMagnitude(String propType, String propModId, float propMagnitude) {
      super(propType, propModId, propMagnitude);
   }

   public WeaponPropertyWithMagnitude(String propType, String propModId, int propLevel, float propMagnitude) {
      super(propType, propModId, propLevel, propMagnitude);
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, this.magnitude));
   }
}
