package com.fantasticsource.mctools;

import java.util.HashSet;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FirstTimeEventMessenger {
   protected static final HashSet<Class<? extends Event>> DONE = new HashSet();

   public FirstTimeEventMessenger() {
   }

   public static void init() {
   }

   @SubscribeEvent
   public static void event(Event event) {
      if (!DONE.contains(event.getClass())) {
         DONE.add(event.getClass());
         System.out.println(TextFormatting.DARK_AQUA + "Event: " + event.getClass().getName());
      }
   }

   static {
      MinecraftForge.EVENT_BUS.register(FirstTimeEventMessenger.class);
   }
}
