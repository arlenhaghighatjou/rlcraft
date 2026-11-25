package meldexun.renderlib.mixin.caching.renderer;

import javax.annotation.Nullable;
import meldexun.renderlib.api.ITileEntityRendererCache;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TileEntity.class})
public class MixinTileEntity implements ITileEntityRendererCache {
   @Unique
   private TileEntitySpecialRenderer<TileEntity> renderer;
   @Unique
   private boolean rendererInitialized;

   public MixinTileEntity() {
   }

   @Unique
   @Nullable
   public <T extends TileEntity> TileEntitySpecialRenderer<T> getRenderer() {
      if (!this.rendererInitialized) {
         this.renderer = this.loadRenderer((TileEntity)this);
         this.rendererInitialized = true;
      }

      return this.renderer;
   }
}
