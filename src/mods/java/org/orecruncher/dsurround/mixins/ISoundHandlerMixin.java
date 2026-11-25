package org.orecruncher.dsurround.mixins;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SoundHandler.class})
public interface ISoundHandlerMixin {
   @Accessor("sndManager")
   SoundManager getSoundManager();

   @Accessor("soundRegistry")
   SoundRegistry getSoundRegistry();
}
