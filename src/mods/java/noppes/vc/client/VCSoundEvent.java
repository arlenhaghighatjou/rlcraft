package noppes.vc.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

class VCSoundEvent extends SoundEvent {
   public VCSoundEvent(ResourceLocation soundNameIn) {
      super(soundNameIn);
      this.setRegistryName(soundNameIn);
   }
}
