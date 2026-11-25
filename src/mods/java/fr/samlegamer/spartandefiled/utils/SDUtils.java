package fr.samlegamer.spartandefiled.utils;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import net.minecraft.item.Item.ToolMaterial;
import org.apache.commons.lang3.StringUtils;

public class SDUtils {
   public SDUtils() {
   }

   public static ToolMaterialEx spartanMatFromToolMat(String name, ToolMaterial material, int color1, int color2, String repairName) {
      return new ToolMaterialEx(name, repairName, "spartandefiled", color1, color2, material.getHarvestLevel(), material.getMaxUses(), material.getEfficiency(), material.getAttackDamage(), material.getEnchantability());
   }

   public static ToolMaterialEx spartanMatFromToolMat(String name, ToolMaterial material, int color1, int color2) {
      return new ToolMaterialEx(name, "ingot" + StringUtils.capitalize(name), "spartandefiled", color1, color2, material.getHarvestLevel(), material.getMaxUses(), material.getEfficiency(), material.getAttackDamage(), material.getEnchantability());
   }
}
