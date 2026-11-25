package com.Shultrea.Rin.Utility_Sector;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SMEsounds {
   public static SoundEvent critattack;
   public static SoundEvent cullingfinished;
   public static SoundEvent cullingfailed;
   public static SoundEvent wateraspecthit;
   public static final SMEsounds INSTANCE = new SMEsounds();
   static List<SoundEvent> SOUND_EVENTS = new ArrayList();

   public SMEsounds() {
   }

   public static void mainRegistry() {
   }

   public static void registerSounds() {
      critattack = registerSound("attack_crit_hit");
      cullingfinished = registerSound("attack_culling_finished");
      cullingfailed = registerSound("attack_culling_failed");
      wateraspecthit = registerSound("attack_wateraspect_hit");
   }

   private static SoundEvent registerSound(String name) {
      ResourceLocation res = new ResourceLocation("somanyenchantments", name);
      SoundEvent evt = (SoundEvent)(new SoundEvent(res)).setRegistryName(res);
      SMEsounds var10000 = INSTANCE;
      SOUND_EVENTS.add(evt);
      return evt;
   }

   @EventBusSubscriber
   public static class EventSubscriber {
      public EventSubscriber() {
      }

      @SubscribeEvent
      public static void registerEnchantment(Register<SoundEvent> event) {
         event.getRegistry().registerAll((IForgeRegistryEntry[])SMEsounds.SOUND_EVENTS.toArray(new SoundEvent[0]));
      }
   }
}
