package com.yungnickyoung.minecraft.bettermineshafts.event;

import com.yungnickyoung.minecraft.bettermineshafts.init.ModConfig;
import com.yungnickyoung.minecraft.bettermineshafts.world.MapGenBetterMineshaft;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventMineshaftGen {
   public EventMineshaftGen() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onMineshaftGen(InitMapGenEvent event) {
      if (event.getType() == EventType.MINESHAFT) {
         ModConfig.initCustomFiles();
         event.setNewGen(new MapGenBetterMineshaft());
      }

   }
}
