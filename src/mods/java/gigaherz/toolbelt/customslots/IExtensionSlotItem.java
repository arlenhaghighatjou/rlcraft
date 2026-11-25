package gigaherz.toolbelt.customslots;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IExtensionSlotItem {
   @Nonnull
   default ImmutableSet<ResourceLocation> getAcceptableSlots(@Nonnull ItemStack stack) {
      return CapabilityExtensionSlotItem.ANY_SLOT_LIST;
   }

   default void onWornTick(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot) {
   }

   default void onEquipped(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot) {
   }

   default void onUnequipped(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot) {
   }

   default boolean canEquip(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot) {
      return true;
   }

   default boolean canUnequip(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot) {
      return EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) <= 0;
   }
}
