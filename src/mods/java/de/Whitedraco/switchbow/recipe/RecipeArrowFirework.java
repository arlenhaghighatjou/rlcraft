package de.Whitedraco.switchbow.recipe;

import de.Whitedraco.switchbow.ItemInit;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class RecipeArrowFirework extends Impl<IRecipe> implements IRecipe {
   private ItemStack resultItem = ItemStack.EMPTY;

   public RecipeArrowFirework() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      int arrow = 0;
      int firework = 0;

      for(int k1 = 0; k1 < inv.getSizeInventory(); ++k1) {
         ItemStack itemstack = inv.getStackInSlot(k1);
         if (itemstack != ItemStack.EMPTY) {
            if (itemstack.getItem() == Items.ARROW) {
               ++arrow;
               if (arrow >= 3) {
                  return false;
               }
            } else if (itemstack.getItem() == Items.FIREWORKS) {
               ++firework;
               if (firework >= 2) {
                  return false;
               }
            }
         }
      }

      this.resultItem = new ItemStack(ItemInit.ArrowFirework, 2);
      if (arrow == 2 && firework == 1) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();

         for(int k2 = 0; k2 < inv.getSizeInventory(); ++k2) {
            ItemStack itemstack3 = inv.getStackInSlot(k2);
            if (itemstack3 != null && itemstack3.getItem() == Items.FIREWORKS && itemstack3.hasTagCompound()) {
               nbttagcompound1 = itemstack3.getTagCompound();
            }
         }

         this.resultItem.setTagCompound(nbttagcompound1);
         return true;
      } else {
         return false;
      }
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.resultItem.copy();
   }

   public int getRecipeSize() {
      return 10;
   }

   @Nullable
   public ItemStack getRecipeOutput() {
      return new ItemStack(ItemInit.ArrowFirework);
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         nonnulllist.set(i, ForgeHooks.getContainerItem(itemstack));
      }

      return nonnulllist;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> list = NonNullList.withSize(8, Ingredient.EMPTY);
      list.set(0, Ingredient.fromItem(Items.ARROW));
      list.set(1, Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.FIREWORKS)}));
      list.set(2, Ingredient.fromItem(Items.ARROW));
      return list;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 3;
   }
}
