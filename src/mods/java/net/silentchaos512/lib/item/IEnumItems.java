package net.silentchaos512.lib.item;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.silentchaos512.lib.registry.SRegistry;

public interface IEnumItems<E extends Enum<E>, I extends Item> extends IStringSerializable {
   @Nonnull
   E getEnum();

   @Nonnull
   I getItem();

   @Nonnull
   default ItemStack getStack() {
      return new ItemStack(this.getItem());
   }

   @Nonnull
   default ItemStack getStack(int amount) {
      return new ItemStack(this.getItem(), amount);
   }

   @Nonnull
   default String getName() {
      String prefix = this.getNamePrefix();
      return (!prefix.isEmpty() ? prefix + "_" : "") + this.getEnum().name().toLowerCase(Locale.ROOT);
   }

   @Nonnull
   default String getNamePrefix() {
      return "";
   }

   /** @deprecated */
   @Deprecated
   static void registerItems(IEnumItems[] array, SRegistry registry) {
      registerItems(Arrays.asList(array), registry);
   }

   /** @deprecated */
   @Deprecated
   static void registerItems(Collection<? extends IEnumItems> list, SRegistry registry) {
      list.forEach((e) -> {
         registry.registerItem(e.getItem(), e.getName());
      });
   }

   public static class RegistrationHelper {
      private final SRegistry registry;

      public RegistrationHelper(SRegistry registry) {
         this.registry = registry;
      }

      public void registerItems(IEnumItems... items) {
         for(IEnumItems item : items) {
            this.registry.registerItem(item.getItem(), item.getName());
         }

      }

      public <E extends Enum<E>> void registerBlocksGenericEnum(Function<E, Block> block, Function<E, String> name, Class<E> enumClass) {
         for(E e : (Enum[])enumClass.getEnumConstants()) {
            this.registry.registerBlock((Block)block.apply(e), (String)name.apply(e));
         }

      }

      public <E extends Enum<E>> void registerItemsGenericEnum(Function<E, Item> item, Function<E, String> name, Class<E> enumClass) {
         for(E e : (Enum[])enumClass.getEnumConstants()) {
            this.registry.registerItem((Item)item.apply(e), (String)name.apply(e));
         }

      }
   }
}
