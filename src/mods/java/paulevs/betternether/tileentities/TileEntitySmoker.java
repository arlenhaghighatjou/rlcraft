package paulevs.betternether.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntitySmoker extends TileEntity implements ITickable {
   private boolean isEmitter;

   public TileEntitySmoker() {
   }

   public void update() {
      if (this.world.isRemote && this.isEmitter) {
         ;
      }

   }

   public void setEmitter(boolean isEmitter) {
      this.isEmitter = isEmitter;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.isEmitter = compound.getBoolean("emitter");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setBoolean("emitter", this.isEmitter);
      return compound;
   }
}
