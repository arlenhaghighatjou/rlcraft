package net.silentchaos512.lib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.lib.collection.StackList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class StackHelper {
   private StackHelper() {
      throw new IllegalAccessError("Utility class");
   }

   public static ItemStack fromBlockOrItem(IForgeRegistryEntry<?> blockOrItem) {
      if (blockOrItem instanceof Block) {
         return new ItemStack((Block)blockOrItem);
      } else {
         return blockOrItem instanceof Item ? new ItemStack((Item)blockOrItem) : ItemStack.EMPTY;
      }
   }

   @Nonnull
   public static ItemStack loadFromNBT(@Nullable NBTTagCompound tags) {
      return tags != null ? new ItemStack(tags) : ItemStack.EMPTY;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public static NBTTagCompound getTagCompound(ItemStack stack, boolean createIfNull) {
      if (stack.isEmpty()) {
         return null;
      } else {
         if (!stack.hasTagCompound() && createIfNull) {
            stack.setTagCompound(new NBTTagCompound());
         }

         return stack.getTagCompound();
      }
   }

   public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
      if (stack.isEmpty()) {
         return new NBTTagCompound();
      } else {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }

         return stack.getTagCompound();
      }
   }

   public static List<ItemStack> getOres(String oreDictKey) {
      return OreDictionary.getOres(oreDictKey);
   }

   public static List<ItemStack> getOres(String oreDictKey, boolean alwaysCreateEntry) {
      return OreDictionary.getOres(oreDictKey, alwaysCreateEntry);
   }

   public static List<String> getOreNames(ItemStack stack) {
      List<String> list = new ArrayList();
      if (stack.isEmpty()) {
         return list;
      } else {
         for(int id : OreDictionary.getOreIDs(stack)) {
            list.add(OreDictionary.getOreName(id));
         }

         return list;
      }
   }

   public static boolean matchesOreDict(ItemStack stack, String oreDictKey) {
      if (stack.isEmpty()) {
         return false;
      } else {
         for(int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).equals(oreDictKey)) {
               return true;
            }
         }

         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public static StackList getNonEmptyStacks(IInventory inv) {
      StackList list = StackList.of();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         list.add(inv.getStackInSlot(i));
      }

      return list;
   }

   public static ItemStack firstMatch(IInventory inv, Predicate<ItemStack> predicate) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty() && predicate.test(stack)) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }
}
