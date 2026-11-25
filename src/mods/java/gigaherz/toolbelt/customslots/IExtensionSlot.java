package gigaherz.toolbelt.customslots;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public interface IExtensionSlot {
   @Nonnull
   IExtensionContainer getContainer();

   @Nonnull
   ResourceLocation getType();

   @Nonnull
   ItemStack getContents();

   void setContents(@Nonnull ItemStack var1);

   void onContentsChanged();

   default boolean canEquip(@Nonnull ItemStack stack) {
      IExtensionSlotItem extItem = (IExtensionSlotItem)stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, (EnumFacing)null);
      return extItem != null && isAcceptableSlot(this, stack, extItem) && extItem.canEquip(stack, this);
   }

   default boolean canUnequip(@Nonnull ItemStack stack) {
      IExtensionSlotItem extItem = (IExtensionSlotItem)stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, (EnumFacing)null);
      return (extItem == null || extItem.canUnequip(stack, this)) && EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) <= 0;
   }

   static boolean isAcceptableSlot(@Nonnull IExtensionSlot slot, @Nonnull ItemStack stack, @Nonnull IExtensionSlotItem extItem) {
      ImmutableSet<ResourceLocation> slots = extItem.getAcceptableSlots(stack);
      return slots.contains(CapabilityExtensionSlotItem.ANY_SLOT) || slots.contains(slot.getType());
   }
}
