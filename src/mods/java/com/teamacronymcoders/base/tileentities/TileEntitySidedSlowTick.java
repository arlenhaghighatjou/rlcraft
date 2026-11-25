package com.teamacronymcoders.base.tileentities;

import net.minecraft.util.ITickable;

public abstract class TileEntitySidedSlowTick extends TileEntitySidedBase implements ITickable {
   private int tickRate = 20;
   private int ticks = 0;

   public TileEntitySidedSlowTick() {
   }

   public TileEntitySidedSlowTick(int tickRate) {
      this.tickRate = tickRate;
   }

   public void update() {
      if (this.ticks == this.tickRate) {
         this.updateTile();
         this.ticks = 0;
      } else {
         ++this.ticks;
      }

   }

   public void updateTile() {
   }
}
