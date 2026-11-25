package meldexun.renderlib.mixin.caching.loadable;

import meldexun.renderlib.api.ILoadable;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TileEntity.class})
public class MixinTileEntity implements ILoadable {
   @Unique
   private boolean isChunkLoaded = true;

   public MixinTileEntity() {
   }

   public boolean isChunkLoaded() {
      return this.isChunkLoaded;
   }

   public void setChunkLoaded(boolean isChunkLoaded) {
      this.isChunkLoaded = isChunkLoaded;
   }
}
