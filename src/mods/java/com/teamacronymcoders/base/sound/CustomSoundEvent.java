package com.teamacronymcoders.base.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class CustomSoundEvent extends SoundEvent implements IHasSoundGroup {
   private final ResourceLocation soundName;
   private final boolean stream;

   public CustomSoundEvent(ResourceLocation soundName) {
      this(soundName, false);
   }

   public CustomSoundEvent(ResourceLocation soundName, boolean stream) {
      super(soundName);
      this.setRegistryName(soundName);
      this.soundName = soundName;
      this.stream = stream;
   }

   public String getName() {
      return this.soundName.getResourcePath().replace("/", ".");
   }

   public SoundGroup getSoundGroup() {
      return new SoundGroup(new SoundGroupItem[]{new SoundGroupItem(this.soundName.toString(), this.stream)}, String.format("subtitle.%s.%s", this.soundName.getResourceDomain(), this.soundName.getResourcePath().replace("/", ".")));
   }
}
