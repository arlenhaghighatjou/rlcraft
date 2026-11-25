package org.orecruncher.dsurround.mixins;

import java.util.Map;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SoundManager.class})
public interface ISoundManagerMixin {
   @Accessor("playingSounds")
   Map<String, ISound> getPlayingSounds();

   @Accessor("invPlayingSounds")
   Map<ISound, String> getPlayingSoundsInv();

   @Accessor("delayedSounds")
   Map<ISound, Integer> getDelayedSounds();
}
