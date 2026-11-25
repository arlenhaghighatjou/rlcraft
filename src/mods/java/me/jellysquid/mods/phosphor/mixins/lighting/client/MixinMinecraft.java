package me.jellysquid.mods.phosphor.mixins.lighting.client;

import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft {
   @Shadow
   @Final
   public Profiler mcProfiler;
   @Shadow
   public WorldClient world;

   public MixinMinecraft() {
   }

   @Inject(
      method = {"runTick"},
      at = {@At(
   value = "CONSTANT",
   args = {"stringValue=levelRenderer"},
   shift = At.Shift.BY,
   by = -3
)}
   )
   private void onRunTick(CallbackInfo ci) {
      this.mcProfiler.endStartSection("lighting");
      ((ILightingEngineProvider)this.world).getLightingEngine().processLightUpdates();
   }
}
