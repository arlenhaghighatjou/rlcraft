package vazkii.quark.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import vazkii.quark.api.ICustomSearchHandler;

public interface ISearchHandler {
   @CapabilityInject(ISearchHandler.class)
   Capability<ISearchHandler> CAPABILITY = null;

   static boolean hasHandler(ItemStack stack) {
      return stack.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static ISearchHandler getHandler(ItemStack stack) {
      return (ISearchHandler)stack.getCapability(CAPABILITY, (EnumFacing)null);
   }

   boolean stackMatchesSearchQuery(String var1, ICustomSearchHandler.StringMatcher var2, ICustomSearchHandler.SearchMethod var3);
}
