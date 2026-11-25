package reborncore.api.power;

import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public interface IEnergyInterfaceItem extends IEnergyItemInfo {
   double getEnergy(ItemStack var1);

   void setEnergy(double var1, ItemStack var3);

   boolean canAddEnergy(double var1, ItemStack var3);

   double addEnergy(double var1, ItemStack var3);

   double addEnergy(double var1, boolean var3, ItemStack var4);

   boolean canUseEnergy(double var1, ItemStack var3);

   double useEnergy(double var1, ItemStack var3);

   double useEnergy(double var1, boolean var3, ItemStack var4);
}
