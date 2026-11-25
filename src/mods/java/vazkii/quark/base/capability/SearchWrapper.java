package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.quark.api.ICustomSearchHandler;
import vazkii.quark.api.capability.ISearchHandler;

public class SearchWrapper implements ISearchHandler, ICapabilityProvider {
   private final ItemStack stack;
   private final ICustomSearchHandler item;

   public SearchWrapper(ItemStack stack) {
      this.stack = stack;
      this.item = (ICustomSearchHandler)stack.getItem();
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CAPABILITY;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CAPABILITY ? CAPABILITY.cast(this) : null);
   }

   public boolean stackMatchesSearchQuery(String query, ICustomSearchHandler.StringMatcher matcher, ICustomSearchHandler.SearchMethod search) {
      return this.item.stackMatchesSearchQuery(this.stack, query, matcher, search);
   }
}
