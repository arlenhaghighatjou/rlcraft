package vazkii.quark.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import vazkii.arl.block.tile.TileMod;

public class TileInductor extends TileMod {
   private int outputSignal;

   public TileInductor() {
   }

   public void writeSharedNBT(NBTTagCompound compound) {
      compound.setInteger("OutputSignal", this.outputSignal);
   }

   public void readSharedNBT(NBTTagCompound compound) {
      this.outputSignal = compound.getInteger("OutputSignal");
   }

   public int getOutputSignal() {
      return this.outputSignal;
   }

   public void setOutputSignal(int outputSignalIn) {
      this.outputSignal = outputSignalIn;
   }
}
