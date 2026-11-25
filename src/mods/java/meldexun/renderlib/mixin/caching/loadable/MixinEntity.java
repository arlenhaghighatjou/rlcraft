package meldexun.renderlib.mixin.caching.loadable;

import meldexun.renderlib.api.ILoadable;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Entity.class})
public class MixinEntity implements ILoadable {
   @Unique
   private boolean isChunkLoaded = true;

   public MixinEntity() {
   }

   public boolean isChunkLoaded() {
      return this.isChunkLoaded;
   }

   public void setChunkLoaded(boolean isChunkLoaded) {
      this.isChunkLoaded = isChunkLoaded;
   }
}
