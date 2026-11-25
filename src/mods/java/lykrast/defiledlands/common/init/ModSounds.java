package lykrast.defiledlands.common.init;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ModSounds {
   public static SoundEvent bookWyrmIdle = registerSoundEvent("book_wyrm.idle");
   public static SoundEvent bookWyrmHurt = registerSoundEvent("book_wyrm.hurt");
   public static SoundEvent bookWyrmDeath = registerSoundEvent("book_wyrm.death");
   private static List<SoundEvent> soundList = new ArrayList();

   public ModSounds() {
   }

   @SubscribeEvent
   public static void registerSoundEvents(Register<SoundEvent> event) {
      for(SoundEvent s : soundList) {
         event.getRegistry().register(s);
      }

      soundList = null;
   }

   public static SoundEvent registerSoundEvent(String name) {
      ResourceLocation location = new ResourceLocation("defiledlands", name);
      SoundEvent event = (SoundEvent)(new SoundEvent(location)).setRegistryName(location);
      soundList.add(event);
      return event;
   }
}
