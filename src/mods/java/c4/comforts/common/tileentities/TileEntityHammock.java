package c4.comforts.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityHammock extends TileEntity {
   private boolean occupied = false;

   public TileEntityHammock() {
   }

   public boolean isOccupied() {
      return this.occupied;
   }

   public void setOccupied(boolean occupied) {
      this.occupied = occupied;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.occupied = compound.getBoolean("occupied");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setBoolean("occupied", this.occupied);
      return compound;
   }
}
