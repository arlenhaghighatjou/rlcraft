package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class HookEnchant {
   public HookEnchant() {
   }

   public static List<EnchantmentData> restrictEnchantmentDatas(List<EnchantmentData> input) {
      Iterator<EnchantmentData> it = input.iterator();

      while(it.hasNext()) {
         EnchantmentData data = (EnchantmentData)it.next();
         if (isEnchantmentBlacklisted(data.enchantment)) {
            it.remove();
         }
      }

      return input;
   }

   public static boolean addEnchantmentRestricted(List<Enchantment> list, Enchantment enchant) {
      return !isEnchantmentBlacklisted(enchant) ? list.add(enchant) : false;
   }

   public static Enchantment getRandomRestricted(Object registry, EaglercraftRandom rand) {
      int overload = 50000;

      Enchantment ench;
      for(ench = (Enchantment)Enchantment.REGISTRY.getRandomObject(rand); isEnchantmentBlacklisted(ench) && overload > 0; ench = (Enchantment)Enchantment.REGISTRY.getRandomObject(rand)) {
         --overload;
      }

      if (overload <= 0) {
         ErrorUtil.logSilent("Enchantment getRandomObject Overload");
         RLTweaker.logger.error("Enchantment getRandomRestricted was unable to get a random enchantment - are all enchantments blacklisted?");
      }

      return ench;
   }

   private static boolean isEnchantmentBlacklisted(Enchantment enchant) {
      String enchReg = enchant.getRegistryName().toString();

      for(String s : ModConfig.server.minecraft.blacklistedEnchantments) {
         if (enchReg.equals(s)) {
            return true;
         }
      }

      return false;
   }
}
