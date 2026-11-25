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

public class RecipeMagicQuiver extends Impl<IRecipe> implements IRecipe {
   private ItemStack resultItem = ItemStack.EMPTY;

   public RecipeMagicQuiver() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      boolean c0 = false;
      boolean c1 = false;
      boolean c2 = false;
      boolean c3 = false;
      boolean c4 = false;
      boolean c5 = false;
      boolean c6 = false;
      boolean c7 = false;
      boolean c8 = false;

      for(int k1 = 0; k1 < inv.getSizeInventory(); ++k1) {
         ItemStack itemstack = inv.getStackInSlot(k1);
         if (itemstack != ItemStack.EMPTY) {
            switch(k1) {
            case 0:
               if (itemstack.getItem() == Items.LEATHER) {
                  c0 = true;
               } else {
                  c0 = false;
               }
               break;
            case 1:
               if (itemstack.getItem() == Items.STRING) {
                  c1 = true;
               } else {
                  c1 = false;
               }
               break;
            case 2:
               if (itemstack.getItem() == Items.LEATHER) {
                  c2 = true;
               } else {
                  c2 = false;
               }
               break;
            case 3:
               if (itemstack.getItem() == Items.LEATHER) {
                  c3 = true;
               } else {
                  c3 = false;
               }
               break;
            case 4:
               if (itemstack.getItem() == ItemInit.Quiver) {
                  c4 = true;
               } else {
                  c4 = false;
               }
               break;
            case 5:
               if (itemstack.getItem() == Items.LEATHER) {
                  c5 = true;
               } else {
                  c5 = false;
               }
            case 6:
            default:
               break;
            case 7:
               if (itemstack.getItem() == Items.LEATHER) {
                  c7 = true;
               } else {
                  c7 = false;
               }
            }

            if (k1 == 6) {
               if (itemstack != ItemStack.EMPTY) {
                  c6 = true;
               } else {
                  c6 = false;
               }
            } else if (k1 == 8) {
               if (itemstack != ItemStack.EMPTY) {
                  c8 = true;
               } else {
                  c8 = false;
               }
            }
         }
      }

      if (c0 && c1 && c2 && c3 && c4 && c5 && !c6 && c7 && !c8) {
         this.resultItem = new ItemStack(ItemInit.MagicQuiver, 1);
         ItemStack itemstack3 = inv.getStackInSlot(4);
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         if (itemstack3 != null && itemstack3.getItem() == ItemInit.Quiver && itemstack3.hasTagCompound()) {
            nbttagcompound1 = itemstack3.getTagCompound();
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
      return new ItemStack(ItemInit.MagicQuiver);
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> itemlist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < itemlist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         itemlist.set(i, ForgeHooks.getContainerItem(itemstack));
      }

      return itemlist;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> list = NonNullList.withSize(8, Ingredient.EMPTY);
      list.set(0, Ingredient.fromItem(Items.LEATHER));
      list.set(1, Ingredient.fromItem(Items.STRING));
      list.set(2, Ingredient.fromItem(Items.LEATHER));
      list.set(3, Ingredient.fromItem(Items.LEATHER));
      list.set(4, Ingredient.fromItem(ItemInit.Quiver));
      list.set(5, Ingredient.fromItem(Items.LEATHER));
      list.set(7, Ingredient.fromItem(Items.LEATHER));
      return list;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 3;
   }

   public String getGroup() {
      return "RecipeMagicQuiver";
   }
}
