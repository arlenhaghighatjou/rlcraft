package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyInterfaceItem;

public abstract class PoweredItem {
   public PoweredItem() {
   }

   public static boolean canUseEnergy(double energy, ItemStack stack) {
      return stack.getItem() instanceof IEnergyInterfaceItem ? ((IEnergyInterfaceItem)stack.getItem()).canUseEnergy(energy, stack) : false;
   }

   public static double useEnergy(double energy, ItemStack stack) {
      return stack.getItem() instanceof IEnergyInterfaceItem ? ((IEnergyInterfaceItem)stack.getItem()).useEnergy(energy, stack) : 0.0D;
   }

   public static void setEnergy(double energy, ItemStack stack) {
      if (stack.getItem() instanceof IEnergyInterfaceItem) {
         ((IEnergyInterfaceItem)stack.getItem()).setEnergy(energy, stack);
      }

   }

   public static double getEnergy(ItemStack stack) {
      return stack.getItem() instanceof IEnergyInterfaceItem ? ((IEnergyInterfaceItem)stack.getItem()).getEnergy(stack) : 0.0D;
   }

   public static double addEnergy(double energy, ItemStack stack) {
      return stack.getItem() instanceof IEnergyInterfaceItem ? ((IEnergyInterfaceItem)stack.getItem()).addEnergy(energy, stack) : 0.0D;
   }

   public static double getMaxPower(ItemStack stack) {
      return stack.getItem() instanceof IEnergyInterfaceItem ? ((IEnergyInterfaceItem)stack.getItem()).getMaxPower(stack) : 0.0D;
   }
}
