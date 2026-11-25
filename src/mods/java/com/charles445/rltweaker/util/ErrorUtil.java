package com.charles445.rltweaker.util;

import com.charles445.rltweaker.RLTweaker;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.ICrashCallable;

public class ErrorUtil {
   public static final Map<String, Integer> errorCount = new ConcurrentHashMap();

   public ErrorUtil() {
   }

   public static void logEnchantmentError(Enchantment enchantment) {
      if (enchantment == null) {
         RLTweaker.logger.error("logEnchantmentError was sent a null enchantment value!");
      } else {
         incrementError("Enchantment " + enchantment.getRegistryName().toString());
      }
   }

   public static void logEnchantmentHandlerError(Enchantment enchantment) {
      if (enchantment == null) {
         RLTweaker.logger.error("logEnchantmentHandlerError was sent a null enchantment value!");
      } else {
         incrementError("Enchantment Handler " + enchantment.getRegistryName().toString());
      }
   }

   public static void logSilent(String key) {
      incrementError(key);
   }

   private static void incrementError(String key) {
      Integer got = (Integer)errorCount.get(key);
      if (got == null) {
         errorCount.put(key, Integer.valueOf(1));
      } else {
         errorCount.put(key, Integer.valueOf(got.intValue() + 1));
      }

   }

   public static class CrashCallable implements ICrashCallable {
      public CrashCallable() {
      }

      public String call() throws Exception {
         StringBuilder sb = new StringBuilder();
         sb.append("\n ");

         for(Entry<String, Integer> entry : ErrorUtil.errorCount.entrySet()) {
            sb.append((String)entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());
            sb.append("\n ");
         }

         return sb.toString();
      }

      public String getLabel() {
         return "RLTweaker Error Report";
      }
   }
}
