package familiarfauna.init;

import familiarfauna.api.FFSounds;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModSounds {
   public ModSounds() {
   }

   public static void init() {
      FFSounds.deer_hurt = registerSound("entity.familiarfauna.deer.hurt");
      FFSounds.deer_dead = registerSound("entity.familiarfauna.deer.dead");
      FFSounds.deer_step = SoundEvents.ENTITY_SHEEP_STEP;
      FFSounds.dragonfly_ambient = registerSound("entity.familiarfauna.dragonfly.ambient");
      FFSounds.pixie_ambient = registerSound("entity.familiarfauna.pixie.ambient");
      FFSounds.pixie_hurt = registerSound("entity.familiarfauna.pixie.hurt");
      FFSounds.turkey_ambient = registerSound("entity.familiarfauna.turkey.ambient");
      FFSounds.turkey_hurt = registerSound("entity.familiarfauna.turkey.hurt");
      FFSounds.turkey_dead = registerSound("entity.familiarfauna.turkey.dead");
      FFSounds.turkey_step = SoundEvents.ENTITY_CHICKEN_STEP;
   }

   private static SoundEvent registerSound(String soundName) {
      ResourceLocation location = new ResourceLocation("familiarfauna", soundName);
      SoundEvent event = new SoundEvent(location);
      event.setRegistryName(location);
      ForgeRegistries.SOUND_EVENTS.register(event);
      return event;
   }
}
