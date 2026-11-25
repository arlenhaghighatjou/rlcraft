package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModifierArmor extends ModifierBase {
   public ModifierArmor() {
      super("Armor");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      float value = 0.0F;
      value = value + this.checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
      value = value + this.checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
      value = value + this.checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
      value = value + this.checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
      return value;
   }

   private float checkArmorSlot(ItemStack stack) {
      if (stack.isEmpty()) {
         return 0.0F;
      } else {
         float sum = 0.0F;
         if (ModConfig.server.miscellaneous.registerEnchantments) {
            if (EnchantmentHelper.getEnchantmentLevel(SDEnchantments.chilling, stack) > 0) {
               sum -= (float)ModConfig.server.temperature.enchantmentTemperature;
            } else if (EnchantmentHelper.getEnchantmentLevel(SDEnchantments.heating, stack) > 0) {
               sum += (float)ModConfig.server.temperature.enchantmentTemperature;
            }
         }

         sum = sum + this.processStackJSON(stack);
         sum = sum + TemperatureUtil.getArmorTemperatureTag(stack);
         return sum;
      }
   }

   private float processStackJSON(ItemStack stack) {
      List<JsonTemperatureIdentity> armorList = (List)JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());
      if (armorList != null) {
         for(JsonTemperatureIdentity jtm : armorList) {
            if (jtm != null && jtm.matches(stack)) {
               return jtm.temperature;
            }
         }
      }

      return 0.0F;
   }
}
