package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.InfernalMobsReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.Watchdog;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.enchantment.Enchantment;

public class InfernalMobsHandler {
   InfernalMobsReflect reflector;

   public InfernalMobsHandler() {
      try {
         this.reflector = new InfernalMobsReflect();
         if (ModConfig.server.infernalmobs.useEnchantmentBlacklist) {
            this.tryRemoveBlacklistedEnchantments();
         }

         if (ModConfig.server.infernalmobs.stallWatchdog) {
            Watchdog.addRoutine("InfernalMobs Stall", new InfernalMobsHandler.InfernalMobsStallRoutine());
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup InfernalMobsHandler!", var2);
         ErrorUtil.logSilent("Infernal Mobs Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   private void tryRemoveBlacklistedEnchantments() {
      try {
         ArrayList<Enchantment> enchantments = this.reflector.getEnchantmentList();
         Iterator<Enchantment> it = enchantments.iterator();
         String[] enchantmentBlacklist = ModConfig.server.minecraft.blacklistedEnchantments;

         while(it.hasNext()) {
            String enchantName = ((Enchantment)it.next()).getRegistryName().toString();

            for(int i = 0; i < enchantmentBlacklist.length; ++i) {
               if (enchantmentBlacklist[i].equals(enchantName)) {
                  it.remove();
                  RLTweaker.logger.info("Removed Infernal Mobs Blacklisted Enchantment: " + enchantName);
                  break;
               }
            }
         }
      } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var6) {
         ErrorUtil.logSilent("Infernal Mobs Remove Blacklisted Enchantments Invocation");
      }

   }

   public class InfernalMobsStallRoutine extends Watchdog.Routine {
      private AtomicInteger semaphoreCount = new AtomicInteger(0);

      public InfernalMobsStallRoutine() {
      }

      public void run() throws Exception {
         if (InfernalMobsHandler.this.reflector.getSemaphor()) {
            int sem = this.semaphoreCount.incrementAndGet();
            if (sem > 5) {
               Watchdog.logger.warn("InfernalMobs may be stalling, attempting recovery");
               InfernalMobsHandler.this.reflector.setSemaphor(false);
               this.semaphoreCount.set(0);
            }
         } else {
            this.semaphoreCount.set(0);
         }

      }
   }
}
