package meldexun.blockoverlayfix.mixin;

import meldexun.blockoverlayfix.BlockOverlayFix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderGlobal.class})
public class MixinRenderGlobal {
   public MixinRenderGlobal() {
   }

   @Inject(
      method = {"renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/ChunkRenderContainer;renderChunkLayer(Lnet/minecraft/util/BlockRenderLayer;)V"
)}
   )
   public void onRenderWorldPass(BlockRenderLayer blockLayerIn, CallbackInfo info) {
      if (blockLayerIn == BlockRenderLayer.SOLID) {
         BlockOverlayFix.renderNearbyBlocks(Minecraft.getMinecraft().getRenderPartialTicks());
      }

   }
}
