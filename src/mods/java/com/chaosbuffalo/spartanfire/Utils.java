package com.chaosbuffalo.spartanfire;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import net.minecraft.item.Item.ToolMaterial;
import org.apache.commons.lang3.StringUtils;

public class Utils {
   public Utils() {
   }

   public static ToolMaterialEx spartanMatFromToolMat(String name, ToolMaterial material, int color1, int color2, String repairName) {
      return new ToolMaterialEx(name, repairName, "spartanfire", color1, color2, material.getHarvestLevel(), material.getMaxUses(), material.getEfficiency(), material.getAttackDamage(), material.getEnchantability());
   }

   public static ToolMaterialEx spartanMatFromToolMat(String name, ToolMaterial material, int color1, int color2) {
      return new ToolMaterialEx(name, "ingot" + StringUtils.capitalize(name), "spartanfire", color1, color2, material.getHarvestLevel(), material.getMaxUses(), material.getEfficiency(), material.getAttackDamage(), material.getEnchantability());
   }
}
