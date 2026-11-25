package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.api.capability.IEnchantColorProvider;

public class EnchantColorWrapper implements IEnchantColorProvider, ICapabilityProvider {
   private final ItemStack stack;
   private final ICustomEnchantColor item;

   public EnchantColorWrapper(ItemStack stack) {
      this.stack = stack;
      this.item = (ICustomEnchantColor)stack.getItem();
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CAPABILITY;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CAPABILITY ? CAPABILITY.cast(this) : null);
   }

   public int getEnchantEffectColor() {
      return this.item.getEnchantEffectColor(this.stack);
   }

   public boolean shouldTruncateColorBrightness() {
      return this.item.shouldTruncateColorBrightness(this.stack);
   }
}
