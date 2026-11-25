package reborncore.api.power;

import net.minecraft.util.EnumFacing;

public interface IEnergyInterfaceTile {
   double getEnergy();

   void setEnergy(double var1);

   double getMaxPower();

   boolean canAddEnergy(double var1);

   double addEnergy(double var1);

   double addEnergy(double var1, boolean var3);

   boolean canUseEnergy(double var1);

   double useEnergy(double var1);

   double useEnergy(double var1, boolean var3);

   boolean canAcceptEnergy(EnumFacing var1);

   boolean canProvideEnergy(EnumFacing var1);

   double getMaxOutput();

   double getMaxInput();

   EnumPowerTier getTier();
}
