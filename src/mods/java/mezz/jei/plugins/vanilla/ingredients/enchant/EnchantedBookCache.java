package mezz.jei.plugins.vanilla.ingredients.enchant;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.function.Supplier;
import mezz.jei.collect.Table;
import mezz.jei.startup.PlayerJoinedWorldEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantedBookCache {
   private final Table<ResourceLocation, Integer, ItemStack> cache = Table.<ResourceLocation, Integer, ItemStack>hashBasedTable();

   public EnchantedBookCache() {
   }

   public ItemStack getEnchantedBook(EnchantmentData enchantmentData) {
      Enchantment enchantment = enchantmentData.enchantment;
      ResourceLocation registryName = enchantment.getRegistryName();
      if (registryName == null) {
         throw new IllegalArgumentException("Enchantment has no registry name: " + enchantment.getName());
      } else {
         return this.cache.computeIfAbsent(registryName, Integer.valueOf(enchantmentData.enchantmentLevel), () -> {
            return ItemEnchantedBook.getEnchantedItemStack(enchantmentData);
         });
      }
   }

   public void register(EventBus bus) {
      bus.register(PlayerJoinedWorldEvent.class, this::onPlayerJoinedWorldEvent);
   }

   public void onPlayerJoinedWorldEvent(PlayerJoinedWorldEvent event) {
      this.cache.clear();
   }
}
