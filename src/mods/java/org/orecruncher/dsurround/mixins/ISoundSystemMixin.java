package org.orecruncher.dsurround.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;

@Mixin({SoundSystem.class})
public interface ISoundSystemMixin {
   @Accessor(
      value = "soundLibrary",
      remap = false
   )
   Library getSoundLibrary();
}
