package meldexun.renderlib.mixin.caching.loadable;

import java.util.Collection;
import java.util.function.Consumer;
import meldexun.renderlib.api.ILoadable;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({World.class})
public class MixinWorld {
   public MixinWorld() {
   }

   @Inject(
      method = {"markTileEntityForRemoval"},
      at = {@At("HEAD")}
   )
   public void markTileEntityForRemoval(TileEntity tileEntity, CallbackInfo info) {
      ((ILoadable)tileEntity).setChunkLoaded(false);
   }

   @Inject(
      method = {"unloadEntities"},
      at = {@At("HEAD")}
   )
   public void unloadEntities(Collection<Entity> entityCollection, CallbackInfo info) {
      entityCollection.forEach((e) -> {
         ((ILoadable)e).setChunkLoaded(false);
      });
   }
}
