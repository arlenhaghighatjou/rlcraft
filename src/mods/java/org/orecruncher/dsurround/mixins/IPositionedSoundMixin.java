package org.orecruncher.dsurround.mixins;

import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PositionedSound.class})
public interface IPositionedSoundMixin {
   @Accessor("volume")
   float getVolumeRaw();

   @Accessor("pitch")
   float getPitchRaw();
}
