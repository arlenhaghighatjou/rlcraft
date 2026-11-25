package org.orecruncher.dsurround.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.renderer.weather.RenderWeather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
   public MixinEntityRenderer() {
   }

   @Inject(
      method = {"addRainParticles()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void myRainParticles(CallbackInfo ci) {
      if (ModOptions.asm.enableWeatherASM && RenderWeather.addRainParticles(Minecraft.getMinecraft().entityRenderer)) {
         ci.cancel();
      }

   }
}
