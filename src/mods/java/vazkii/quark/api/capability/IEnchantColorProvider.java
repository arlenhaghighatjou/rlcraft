package vazkii.quark.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IEnchantColorProvider {
   @CapabilityInject(IEnchantColorProvider.class)
   Capability<IEnchantColorProvider> CAPABILITY = null;

   static boolean hasProvider(ItemStack stack) {
      return stack.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static IEnchantColorProvider getProvider(ItemStack stack) {
      return (IEnchantColorProvider)stack.getCapability(CAPABILITY, (EnumFacing)null);
   }

   int getEnchantEffectColor();

   default boolean shouldTruncateColorBrightness() {
      return true;
   }
}
