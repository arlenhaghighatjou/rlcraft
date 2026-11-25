package lykrast.defiledlands.common.tileentity;

import lykrast.defiledlands.common.world.biome.BiomeDefiled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileConjuringAltar extends TileEntity implements ITickable {
   private boolean active = false;

   public TileConjuringAltar() {
   }

   public void update() {
      this.recalculate();
   }

   public void recalculate() {
      boolean flag1 = this.world.isAirBlock(this.pos.up()) && this.world.isAirBlock(this.pos.up(2));
      boolean flag2 = this.world.getBiome(this.pos) instanceof BiomeDefiled;
      boolean result = flag1 && flag2;
      if (result != this.active) {
         this.markDirty();
      }

      this.active = result;
   }

   public boolean isActive() {
      return this.active;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.active = compound.getBoolean("active");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setBoolean("active", this.active);
      return compound;
   }
}
