package de.impelon.disenchanter.crafting;

import com.google.gson.JsonObject;
import de.impelon.disenchanter.DisenchanterConfig;
import java.util.function.BooleanSupplier;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConfigConditionFactory implements IConditionFactory {
   public ConfigConditionFactory() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      final String type = (new ResourceLocation(JsonUtils.getString(json, "type"))).getResourcePath();
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            String var1 = type;
            byte var2 = -1;
            switch(var1.hashCode()) {
            case -2022897255:
               if (var1.equals("overload_upgrade_enabled")) {
                  var2 = 7;
               }
               break;
            case -1578091217:
               if (var1.equals("clear_jar_upgrades_enabled")) {
                  var2 = 9;
               }
               break;
            case -1577733118:
               if (var1.equals("cycling_upgrade_enabled")) {
                  var2 = 3;
               }
               break;
            case -789338471:
               if (var1.equals("capacity_upgrade_enabled")) {
                  var2 = 8;
               }
               break;
            case -673070582:
               if (var1.equals("automatic_upgrade_enabled")) {
                  var2 = 1;
               }
               break;
            case -252586067:
               if (var1.equals("voiding_upgrade_enabled")) {
                  var2 = 4;
               }
               break;
            case 1113715548:
               if (var1.equals("clear_table_upgrades_enabled")) {
                  var2 = 5;
               }
               break;
            case 1624590842:
               if (var1.equals("disenchantment_table_enabled")) {
                  var2 = 0;
               }
               break;
            case 1710516680:
               if (var1.equals("experience_jar_enabled")) {
                  var2 = 6;
               }
               break;
            case 1769849860:
               if (var1.equals("bulkdisenchanting_upgrade_enabled")) {
                  var2 = 2;
               }
            }

            switch(var2) {
            case 0:
               return DisenchanterConfig.crafting.enableDisenchantmentTableRecipe;
            case 1:
               return DisenchanterConfig.crafting.enableAutomaticTableUpgradeRecipe;
            case 2:
               return DisenchanterConfig.crafting.enableBulkDisenchantingTableUpgradeRecipe;
            case 3:
               return DisenchanterConfig.crafting.enableCyclingTableUpgradeRecipe;
            case 4:
               return DisenchanterConfig.crafting.enableVoidingTableUpgradeRecipe;
            case 5:
               return DisenchanterConfig.crafting.enableClearTableRecipe;
            case 6:
               return DisenchanterConfig.crafting.enableJarRecipe;
            case 7:
               return DisenchanterConfig.crafting.enableOverloadJarUpgradeRecipe;
            case 8:
               return DisenchanterConfig.crafting.enableCapacityJarUpgradeRecipe;
            case 9:
               return DisenchanterConfig.crafting.enableClearJarRecipe;
            default:
               return false;
            }
         }
      };
   }
}
