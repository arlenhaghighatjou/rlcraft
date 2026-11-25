package reborncore.common.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(
   modid = "reborncore"
)
public class ModSounds {
   public static SoundEvent BLOCK_DISMANTLE;

   public ModSounds() {
   }

   @SubscribeEvent
   public static void onSoundRegistry(Register<SoundEvent> event) {
      BLOCK_DISMANTLE = createSoundEvent("block_dismantle");
      event.getRegistry().register(BLOCK_DISMANTLE);
   }

   private static SoundEvent createSoundEvent(String str) {
      ResourceLocation resourceLocation = new ResourceLocation("reborncore", str);
      SoundEvent soundEvent = new SoundEvent(resourceLocation);
      soundEvent.setRegistryName(resourceLocation);
      return soundEvent;
   }
}
