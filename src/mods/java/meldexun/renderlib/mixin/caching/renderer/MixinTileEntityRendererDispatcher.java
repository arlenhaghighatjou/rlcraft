package meldexun.renderlib.mixin.caching.renderer;

import meldexun.renderlib.api.ITileEntityRendererCache;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityRendererDispatcher.class})
public class MixinTileEntityRendererDispatcher {
   public MixinTileEntityRendererDispatcher() {
   }

   @Redirect(
      method = {"render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;getRenderer(Lnet/minecraft/tileentity/TileEntity;)Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;"
)
   )
   public TileEntitySpecialRenderer<TileEntity> render_getRenderer(TileEntityRendererDispatcher tileEntityRenderDispatcher, TileEntity tileEntity) {
      if (tileEntity == null) {
         return null;
      } else {
         return tileEntity.isInvalid() ? null : ((ITileEntityRendererCache)tileEntity).getRenderer();
      }
   }
}
