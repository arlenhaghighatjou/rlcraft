package com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds;

import net.minecraft.block.SoundType;

public class SoundTypeDefinition implements ISoundTypeDefinition {
   private SoundType soundType;

   public SoundTypeDefinition(SoundType soundType) {
      this.soundType = soundType;
   }

   public float getVolume() {
      return this.soundType.getVolume();
   }

   public float getPitch() {
      return this.soundType.getPitch();
   }

   public ISoundEventDefinition getBreakSound() {
      return new SoundEventDefinition(this.soundType.getBreakSound());
   }

   public ISoundEventDefinition getStepSound() {
      return new SoundEventDefinition(this.soundType.getStepSound());
   }

   public ISoundEventDefinition getPlaceSound() {
      return new SoundEventDefinition(this.soundType.getPlaceSound());
   }

   public ISoundEventDefinition getHitSound() {
      return new SoundEventDefinition(this.soundType.getHitSound());
   }

   public ISoundEventDefinition getFallSound() {
      return new SoundEventDefinition(this.soundType.getFallSound());
   }

   public SoundType getInternal() {
      return this.soundType;
   }
}
