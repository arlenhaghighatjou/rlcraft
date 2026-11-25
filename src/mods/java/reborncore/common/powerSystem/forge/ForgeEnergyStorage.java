package reborncore.common.powerSystem.forge;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.common.powerSystem.TilePowerAcceptor;

public class ForgeEnergyStorage implements IEnergyStorage {
   TilePowerAcceptor acceptor;
   @Nullable
   EnumFacing facing;

   public ForgeEnergyStorage(TilePowerAcceptor acceptor, @Nullable EnumFacing facing) {
      this.acceptor = acceptor;
      this.facing = facing;
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      return this.acceptor.receiveEnergy(this.facing, maxReceive, simulate);
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      return this.acceptor.extractEnergy(this.facing, maxExtract, simulate);
   }

   public int getEnergyStored() {
      return this.acceptor.getEnergyStored(this.facing);
   }

   public int getMaxEnergyStored() {
      return this.acceptor.getMaxEnergyStored(this.facing);
   }

   public boolean canExtract() {
      return this.acceptor.canProvideEnergy(this.facing);
   }

   public boolean canReceive() {
      return this.acceptor.canAcceptEnergy(this.facing);
   }

   public void setFacing(@Nullable EnumFacing facing) {
      this.facing = facing;
   }
}
