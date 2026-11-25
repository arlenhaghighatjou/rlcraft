package com.charles445.rltweaker.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Watchdog {
   public static Logger logger = LogManager.getLogger("RLTweakerWatchdog");
   protected static ScheduledExecutorService executorService;
   protected static Map<String, Watchdog.Routine> routines = new ConcurrentHashMap();

   public Watchdog() {
   }

   public static void init() {
      if (executorService == null) {
         executorService = Executors.newScheduledThreadPool(1);
         executorService.scheduleAtFixedRate(new Watchdog.RoutineManager(), 5L, 1L, TimeUnit.SECONDS);
      }

   }

   public static Watchdog.Routine addRoutine(String name, Watchdog.Routine routine) {
      logger.info("Adding Watchdog Routine: " + name);
      return (Watchdog.Routine)routines.put(name, routine);
   }

   public static Watchdog.Routine removeRoutine(String name) {
      return (Watchdog.Routine)routines.remove(name);
   }

   public abstract static class Routine {
      public Routine() {
      }

      public abstract void run() throws Exception;
   }

   static class RoutineManager implements Runnable {
      private Set<String> failedSet = new HashSet();

      RoutineManager() {
      }

      public void run() {
         for(Entry<String, Watchdog.Routine> entry : Watchdog.routines.entrySet()) {
            try {
               ((Watchdog.Routine)entry.getValue()).run();
            } catch (Exception var4) {
               this.failedSet.add(entry.getKey());
               Watchdog.logger.error("Failed to run watchdog routine: " + (String)entry.getKey(), var4);
            }
         }

         if (!this.failedSet.isEmpty()) {
            for(String removal : this.failedSet) {
               Watchdog.removeRoutine(removal);
               Watchdog.logger.warn("Routine was removed: " + removal);
            }

            this.failedSet.clear();
         }

      }
   }
}
