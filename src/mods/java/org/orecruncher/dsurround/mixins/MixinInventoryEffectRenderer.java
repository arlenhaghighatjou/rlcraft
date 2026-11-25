package org.orecruncher.dsurround.mixins;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import org.orecruncher.dsurround.ModOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InventoryEffectRenderer.class})
public abstract class MixinInventoryEffectRenderer extends GuiContainer {
   @Shadow
   protected boolean hasActivePotionEffects;

   public MixinInventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   @Inject(
      method = {"updateActivePotionEffects()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleUpdate(CallbackInfo ci) {
      if (ModOptions.asm.disablePotionIconsInInventory) {
         this.hasActivePotionEffects = false;
         ci.cancel();
      }

   }
}
