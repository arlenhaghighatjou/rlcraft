package vazkii.quark.vanity.recipe;

import javax.annotation.Nonnull;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.ModRecipe;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;

public class ElytraDyeingRecipe extends ModRecipe {
   public ElytraDyeingRecipe() {
      super(new ResourceLocation("quark", "elytra_dyeing"));
   }

   public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
      boolean foundSource = false;
      boolean foundTarget = false;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemElytra) {
               if (foundTarget) {
                  return false;
               }

               foundTarget = true;
            } else {
               if (this.getDye(stack) == -1) {
                  return false;
               }

               if (foundSource) {
                  return false;
               }

               foundSource = true;
            }
         }
      }

      return foundSource && foundTarget;
   }

   @Nonnull
   public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
      int source = -1;
      ItemStack target = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty()) {
            int dye = this.getDye(stack);
            if (dye != -1) {
               source = dye;
            } else {
               target = stack;
            }
         }
      }

      if (!target.isEmpty()) {
         ItemStack copy = target.copy();
         ItemNBTHelper.setInt(copy, "quark:elytraDye", source);
         return copy;
      } else {
         return ItemStack.EMPTY;
      }
   }

   private int getDye(ItemStack stack) {
      if (stack.getItem() instanceof ItemDye) {
         return stack.getItemDamage();
      } else {
         int[] ids = OreDictionary.getOreIDs(stack);

         for(int id : ids) {
            String tag = OreDictionary.getOreName(id);
            int dye = LibMisc.OREDICT_DYES.indexOf(tag);
            if (dye > -1) {
               return dye;
            }
         }

         return -1;
      }
   }

   @Nonnull
   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.ELYTRA);
   }

   public boolean isDynamic() {
      return true;
   }

   public boolean canFit(int x, int y) {
      return true;
   }

   @Nonnull
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> list = NonNullList.withSize(2, Ingredient.EMPTY);
      list.set(0, Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.ELYTRA)}));
      ItemStack[] stacks = new ItemStack[16];

      for(int i = 0; i < 16; ++i) {
         stacks[i] = new ItemStack(Items.DYE, 1, i);
      }

      list.set(1, Ingredient.fromStacks(stacks));
      return list;
   }
}
