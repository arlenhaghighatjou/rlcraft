package net.silentchaos512.scalinghealth.init;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.silentchaos512.lib.registry.SRegistry;

public final class ModSounds {
   private static final Map<String, SoundEvent> SOUND_EVENTS = new LinkedHashMap();
   public static final SoundEvent CURSED_HEART_USE = create("cursed_heart_use");
   public static final SoundEvent ENCHANTED_HEART_USE = create("enchanted_heart_use");
   public static final SoundEvent HEART_CONTAINER_USE = create("heart_container_use");
   public static final SoundEvent PLAYER_DIED = create("player_died");

   private ModSounds() {
   }

   public static void registerAll(SRegistry reg) {
      SOUND_EVENTS.forEach((name, sound) -> {
         reg.registerSoundEvent(sound, name);
      });
   }

   private static SoundEvent create(String soundId) {
      ResourceLocation name = new ResourceLocation("scalinghealth", soundId);
      SoundEvent sound = new SoundEvent(name);
      SOUND_EVENTS.put(soundId, sound);
      return sound;
   }
}
