package bettercombat.mod.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(
   modid = "bettercombatmod"
)
public class Sounds {
   public static final SoundEvent SWORD_SLASH = registerSound("player.swordslash");
   public static final SoundEvent CRITICAL_STRIKE = registerSound("player.criticalstrike");

   public Sounds() {
   }

   private static SoundEvent registerSound(String soundName) {
      ResourceLocation soundID = new ResourceLocation("bettercombatmod", soundName);
      return (SoundEvent)(new SoundEvent(soundID)).setRegistryName(soundID);
   }

   @SubscribeEvent
   public static void registerSounds(Register<SoundEvent> event) {
      event.getRegistry().registerAll(new SoundEvent[]{SWORD_SLASH, CRITICAL_STRIKE});
   }
}
