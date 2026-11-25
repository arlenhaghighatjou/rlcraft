package com.fantasticsource.mctools;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerTickTimer {
   private static long currentTick = 0L;
   private static LinkedHashMap<Runnable, Long> runnables = new LinkedHashMap();

   public ServerTickTimer() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void update(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         ++currentTick;

         for(Entry<Runnable, Long> entry : (Entry[])runnables.entrySet().toArray(new Entry[0])) {
            if (currentTick >= ((Long)entry.getValue()).longValue()) {
               runnables.remove(entry.getKey());
               ((Runnable)entry.getKey()).run();
            }
         }
      }

   }

   public static long currentTick() {
      return currentTick;
   }

   public static void schedule(int tickDelay, Runnable runnable) {
      runnables.put(runnable, Long.valueOf(currentTick + (long)tickDelay));
   }

   public static void unschedule(Runnable runnable) {
      runnables.remove(runnable);
   }

   static {
      MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
   }
}
