package rlmixins.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import rlmixins.handlers.ModRegistry;
import rlmixins.wrapper.CharmWrapper;

public class RecipeCurseBreak extends Impl<IRecipe> implements IRecipe {
   public RecipeCurseBreak() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      return slots == null ? ItemStack.EMPTY : ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(CharmWrapper.getCurseBreak(), 1));
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int bookSlot = -1;
      int talismanSlot = -1;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks != 2) {
         return null;
      } else {
         Iterator var9 = occupiedSlots.iterator();

         while(var9.hasNext()) {
            int i = ((Integer)var9.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.getItem() instanceof ItemEnchantedBook) {
               bookSlot = i;
            } else {
               if (!itemStack.getItem().equals(ModRegistry.cleansingTalisman)) {
                  return null;
               }

               talismanSlot = i;
            }
         }

         Integer[] slots = new Integer[2];
         slots[0] = bookSlot;
         slots[1] = talismanSlot;
         return bookSlot != -1 && talismanSlot != -1 ? slots : null;
      }
   }
}
