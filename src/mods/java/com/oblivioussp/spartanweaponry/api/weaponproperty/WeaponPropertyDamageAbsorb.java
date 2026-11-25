package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class WeaponPropertyDamageAbsorb extends WeaponPropertyWithCallback {
   public WeaponPropertyDamageAbsorb(String propType, String propModId, float propMagnitude) {
      super(propType, propModId, propMagnitude);
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, this.magnitude * 100.0F));
   }

   public float modifyDamageTaken(ToolMaterialEx material, float baseDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      ItemStack heldItemVictim = victim.getHeldItemMainhand();
      if (!heldItemVictim.isEmpty()) {
         heldItemVictim.damageItem(MathHelper.floor(baseDamage * this.magnitude), victim);
         return baseDamage * (1.0F - this.magnitude);
      } else {
         return baseDamage;
      }
   }
}
