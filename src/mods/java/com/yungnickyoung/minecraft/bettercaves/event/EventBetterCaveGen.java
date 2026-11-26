package com.yungnickyoung.minecraft.bettercaves.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.mineshaft.MapGenBetterMineshaft;
import com.yungnickyoung.minecraft.bettercaves.world.ravine.MapGenBetterRavine;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EventBetterCaveGen {
   public EventBetterCaveGen() {
   }

   public void register(EventBus bus) {
      bus.register(InitMapGenEvent.class, EventPriority.NORMAL, false, this::onInitMapGenEvent);
   }

   public void onInitMapGenEvent(InitMapGenEvent event) {
      if ((event.getType() == EventType.CAVE || event.getType() == EventType.NETHER_CAVE) && !event.getOriginalGen().getClass().equals(MapGenBetterCaves.class)) {
         event.setNewGen(new MapGenBetterCaves(event));
      } else if (event.getType() == EventType.MINESHAFT && event.getOriginalGen() == event.getNewGen()) {
         event.setNewGen(new MapGenBetterMineshaft(event));
      } else if (event.getType() == EventType.RAVINE && event.getOriginalGen() == event.getNewGen()) {
         event.setNewGen(new MapGenBetterRavine(event));
      }

   }
}
