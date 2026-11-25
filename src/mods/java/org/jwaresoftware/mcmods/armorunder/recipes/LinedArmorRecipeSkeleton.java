package org.jwaresoftware.mcmods.armorunder.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;

public abstract class LinedArmorRecipeSkeleton extends Impl<IRecipe> implements IRecipe {
   static final String NBT_MOD = XLining.NBT_VALUE_TAGNAME;
   protected final IRecipe _impl;

   LinedArmorRecipeSkeleton(IRecipe template) {
      this._impl = template;
   }

   public boolean canFit(int width, int height) {
      return width * height == 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStacks.NULLSTACK();
   }

   protected abstract boolean isLining(Item var1);

   protected abstract boolean canAddLining(ItemStack var1);

   protected abstract Def getXLining(ItemStack var1);

   protected boolean isEnabled() {
      return ArmorRecipes.ENABLED;
   }

   public boolean matches(InventoryCrafting grid, World world) {
      boolean match = this.isEnabled() && this._impl.matches(grid, world);
      if (match) {
         boolean found_armor = false;
         boolean found_lining = false;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next)) {
               if (this.isLining(next.getItem())) {
                  if (found_lining) {
                     return false;
                  }

                  found_lining = true;
               } else if (Armory.isLiningAllowed(next)) {
                  if (found_armor) {
                     return false;
                  }

                  found_armor = true;
               }
            }
         }

         if (!found_armor || !found_lining) {
            match = false;
         }
      }

      return match;
   }

   public ItemStack getCraftingResult(InventoryCrafting grid) {
      ItemStack out = ItemStacks.NULLSTACK();
      if (this.isEnabled()) {
         ItemStack lining = null;
         ItemStack armorpiece = null;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next)) {
               if (this.isLining(next.getItem())) {
                  lining = next;
               } else if (Armory.isLiningAllowed(next)) {
                  armorpiece = next;
               }
            }
         }

         if (lining != null && armorpiece != null && this.canAddLining(armorpiece)) {
            out = ItemStacks.copyItemStackSingle(armorpiece);
            Def defn = this.getXLining(lining);
            out = Armory.setXLining(out, defn);
         }
      }

      return out;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting grid) {
      return SharedGlue.emptyNonNullItemStackList(grid.getSizeInventory());
   }
}
