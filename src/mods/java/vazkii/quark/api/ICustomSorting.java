package vazkii.quark.api;

import java.util.Comparator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface ICustomSorting {
   @CapabilityInject(ICustomSorting.class)
   Capability<ICustomSorting> CAPABILITY = null;

   static boolean hasSorting(ItemStack stack) {
      return stack.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static ICustomSorting getSorting(ItemStack stack) {
      return (ICustomSorting)stack.getCapability(CAPABILITY, (EnumFacing)null);
   }

   Comparator<ItemStack> getItemComparator();

   String getSortingCategory();
}
