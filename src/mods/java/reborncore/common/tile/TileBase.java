package reborncore.common.tile;

import net.minecraft.tileentity.TileEntity;

public class TileBase extends TileEntity {
   public TileBase() {
   }

   public void markBlockForUpdate() {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.getPos()), this.getWorld().getBlockState(this.getPos()), 3);
   }
}
