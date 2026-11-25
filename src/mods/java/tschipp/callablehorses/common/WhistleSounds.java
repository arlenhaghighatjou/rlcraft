package tschipp.callablehorses.common;

import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class WhistleSounds {
   public static SoundEvent whistle;
   private static ArrayList<SoundEvent> sounds = new ArrayList();

   public WhistleSounds() {
   }

   public static void registerSounds() {
      whistle = registerSound("whistle");
   }

   private static SoundEvent registerSound(String soundName) {
      ResourceLocation soundID = new ResourceLocation("callablehorses", soundName);
      SoundEvent s = new SoundEvent(soundID);
      s.setRegistryName(soundID);
      ForgeRegistries.SOUND_EVENTS.register(s);
      sounds.add(s);
      return s;
   }

   public static SoundEvent getRandomWhistle() {
      return whistle;
   }
}
