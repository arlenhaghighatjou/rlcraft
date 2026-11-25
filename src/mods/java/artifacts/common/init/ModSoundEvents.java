package artifacts.common.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSoundEvents {
   public static final SoundEvent FART = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "fart"))).setRegistryName("fart");
   public static final SoundEvent BUBBLE_WRAP = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "bubble_wrap"))).setRegistryName("bubble_wrap");
   public static final SoundEvent MIMIC_HURT = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "mimic.hurt"))).setRegistryName("mimic.hurt");
   public static final SoundEvent MIMIC_DEATH = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "mimic.death"))).setRegistryName("mimic.death");
   public static final SoundEvent MIMIC_OPEN = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "mimic.open"))).setRegistryName("mimic.open");
   public static final SoundEvent MIMIC_CLOSE = (SoundEvent)(new SoundEvent(new ResourceLocation("artifacts", "mimic.close"))).setRegistryName("mimic.close");

   public ModSoundEvents() {
   }

   public static void registerSoundEvents(IForgeRegistry<SoundEvent> registry) {
      registry.register(FART);
      registry.register(BUBBLE_WRAP);
      registry.register(MIMIC_CLOSE);
      registry.register(MIMIC_OPEN);
      registry.register(MIMIC_HURT);
      registry.register(MIMIC_DEATH);
   }
}
