package org.orecruncher.dsurround.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiErrorBase;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SoundManager.class})
public abstract class MixinSoundManager {
   public MixinSoundManager() {
   }

   @Inject(
      method = {"getClampedVolume(Lnet/minecraft/client/audio/ISound;)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getClampedVolume(ISound sound, CallbackInfoReturnable<Float> ci) {
      GuiScreen current = Minecraft.getMinecraft().currentScreen;
      if (!(current instanceof GuiErrorBase)) {
         try {
            ci.setReturnValue(Float.valueOf(SoundEngine.getClampedVolume(sound)));
         } catch (Exception var5) {
            ;
         }
      }

   }
}
