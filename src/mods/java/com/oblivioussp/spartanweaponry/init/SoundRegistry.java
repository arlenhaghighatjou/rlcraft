package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.util.SoundEventSW;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class SoundRegistry {
   public static final SoundEvent CROSSBOW_LOAD = new SoundEventSW(new ResourceLocation("spartanweaponry", "crossbow_load"));
   public static final SoundEvent CROSSBOW_FIRE = new SoundEventSW(new ResourceLocation("spartanweaponry", "crossbow_fire"));
   public static final SoundEvent THROWING_WEAPON_THROW = new SoundEventSW(new ResourceLocation("spartanweaponry", "throwing_weapon_throw"));
   public static final SoundEvent THROWING_WEAPON_HIT = new SoundEventSW(new ResourceLocation("spartanweaponry", "throwing_weapon_hit"));
   public static final SoundEvent THROWING_WEAPON_RETURN = new SoundEventSW(new ResourceLocation("spartanweaponry", "throwing_weapon_return"));
   public static final SoundEvent BOOMERANG_THROW = new SoundEventSW(new ResourceLocation("spartanweaponry", "boomerang_throw"));
   public static final SoundEvent BOOMERANG_FLIGHT = new SoundEventSW(new ResourceLocation("spartanweaponry", "boomerang_flight"));

   public SoundRegistry() {
   }

   @SubscribeEvent
   public static void registerSounds(Register<SoundEvent> ev) {
      IForgeRegistry<SoundEvent> reg = ev.getRegistry();
      reg.registerAll(new SoundEvent[]{CROSSBOW_LOAD, CROSSBOW_FIRE, THROWING_WEAPON_THROW, THROWING_WEAPON_HIT, THROWING_WEAPON_RETURN, BOOMERANG_THROW, BOOMERANG_FLIGHT});
   }
}
