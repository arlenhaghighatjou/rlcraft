package com.Shultrea.Rin.Utility_Sector;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;

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

   public static class EventSubscriber {
      public EventSubscriber() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, SoundEvent.class, EventSubscriber::registerEnchantment);
      }

      public static void registerEnchantment(Register<SoundEvent> event) {
         event.getRegistry().registerAll(SMEsounds.SOUND_EVENTS.toArray(new SoundEvent[0]));
      }
   }
}
