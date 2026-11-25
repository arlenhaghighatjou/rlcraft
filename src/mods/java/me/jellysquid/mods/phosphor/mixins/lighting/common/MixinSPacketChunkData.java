package me.jellysquid.mods.phosphor.mixins.lighting.common;

import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SPacketChunkData.class})
public abstract class MixinSPacketChunkData {
   public MixinSPacketChunkData() {
   }

   @Inject(
      method = {"calculateChunkSize"},
      at = {@At("HEAD")}
   )
   private void onCalculateChunkSize(Chunk chunkIn, boolean hasSkyLight, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
      ((ILightingEngineProvider)chunkIn).getLightingEngine().processLightUpdates();
   }
}
