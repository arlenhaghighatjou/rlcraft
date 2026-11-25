package reborncore.api.power;

import net.minecraft.item.ItemStack;

public interface IEnergyItemInfo {
   double getMaxPower(ItemStack var1);

   boolean canAcceptEnergy(ItemStack var1);

   boolean canProvideEnergy(ItemStack var1);

   double getMaxTransfer(ItemStack var1);
}
