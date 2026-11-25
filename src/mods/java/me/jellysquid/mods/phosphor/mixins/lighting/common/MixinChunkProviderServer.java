package me.jellysquid.mods.phosphor.mixins.lighting.common;

import java.util.Set;
import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkProviderServer.class})
public abstract class MixinChunkProviderServer {
   @Shadow
   @Final
   public WorldServer world;
   @Shadow
   @Final
   private Set<Long> droppedChunksSet;

   public MixinChunkProviderServer() {
   }

   @Inject(
      method = {"saveChunks"},
      at = {@At("HEAD")}
   )
   private void onSaveChunks(boolean all, CallbackInfoReturnable<Boolean> cir) {
      ((ILightingEngineProvider)this.world).getLightingEngine().processLightUpdates();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onTick(CallbackInfoReturnable<Boolean> cir) {
      if (!this.world.disableLevelSaving && !this.droppedChunksSet.isEmpty()) {
         ((ILightingEngineProvider)this.world).getLightingEngine().processLightUpdates();
      }

   }
}
