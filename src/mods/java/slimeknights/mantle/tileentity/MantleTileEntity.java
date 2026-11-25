package slimeknights.mantle.tileentity;

import net.minecraft.tileentity.TileEntity;

public class MantleTileEntity extends TileEntity {
   public MantleTileEntity() {
   }

   public boolean isClient() {
      return this.getWorld() != null && this.getWorld().isRemote;
   }

   public void markDirtyFast() {
      if (this.world != null) {
         this.world.markChunkDirty(this.pos, this);
      }

   }
}
