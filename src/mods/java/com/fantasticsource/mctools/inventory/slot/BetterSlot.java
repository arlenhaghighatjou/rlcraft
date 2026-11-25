package com.fantasticsource.mctools.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class BetterSlot extends Slot {
   public final ResourceLocation texture;
   public final int u;
   public final int v;
   public final int texWidth16x;
   public final int texHeight16x;
   public final double uPixel;
   public final double vPixel;
   public boolean enabled = true;

   public BetterSlot(IInventory inventoryIn, int index, int x, int y, ResourceLocation texture, int texWidth16x, int texHeight16x, int u, int v) {
      super(inventoryIn, index, x, y);
      this.texture = texture;
      this.u = u;
      this.v = v;
      this.texWidth16x = texWidth16x;
      this.texHeight16x = texHeight16x;
      this.uPixel = 1.0D / (double)texWidth16x;
      this.vPixel = 1.0D / (double)texHeight16x;
   }

   public BetterSlot enable() {
      this.enabled = true;
      return this;
   }

   public BetterSlot disable() {
      this.enabled = false;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public BetterSlot setEnabled(boolean enabled) {
      if (enabled) {
         this.enable();
      } else {
         this.disable();
      }

      return this;
   }
}
