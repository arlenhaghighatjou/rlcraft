package rlmixins.mixin.quark;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlmixins.handlers.quark.RightClickSignEditHandler;
import vazkii.quark.tweaks.feature.RightClickSignEdit;

@Mixin({RightClickSignEdit.class})
public abstract class RightClickSignEditMixin {
   public RightClickSignEditMixin() {
   }

   @Inject(
      method = {"onInteract"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_quarkRightClickSignEdit_onInteract(RightClickBlock event, CallbackInfo ci) {
      if (!RightClickSignEditHandler.isEnabled) {
         ci.cancel();
      }

   }
}
