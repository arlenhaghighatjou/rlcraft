package vazkii.quark.vanity.recipe;

import javax.annotation.Nonnull;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.arl.recipe.ModRecipe;
import vazkii.arl.util.ItemNBTHelper;

public class FireworkCloningRecipe extends ModRecipe {
   public FireworkCloningRecipe() {
      super(new ResourceLocation("quark", "firework_cloning"));
   }

   public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World var2) {
      boolean foundSource = false;
      boolean foundTarget = false;
      ItemStack source = ItemStack.EMPTY;
      ItemStack target = ItemStack.EMPTY;

      for(int i = 0; i < var1.getSizeInventory(); ++i) {
         ItemStack stack = var1.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (!(stack.getItem() instanceof ItemFirework)) {
               return false;
            }

            if (stack.getTagCompound() != null && this.hasExplosions(stack)) {
               if (foundSource) {
                  return false;
               }

               source = stack;
               foundSource = true;
            } else {
               if (foundTarget) {
                  return false;
               }

               target = stack;
               foundTarget = true;
            }
         }
      }

      return foundSource && foundTarget && this.getFlight(source) == this.getFlight(target);
   }

   @Nonnull
   public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
      ItemStack source = ItemStack.EMPTY;
      ItemStack target = ItemStack.EMPTY;

      for(int i = 0; i < var1.getSizeInventory(); ++i) {
         ItemStack stack = var1.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (this.hasExplosions(stack)) {
               source = stack;
            } else {
               target = stack;
            }
         }
      }

      if (!source.isEmpty() && !target.isEmpty()) {
         ItemStack copy = target.copy();
         NBTTagCompound cmp = new NBTTagCompound();
         cmp.setTag("Fireworks", ItemNBTHelper.getCompound(source, "Fireworks", false));
         copy.setTagCompound(cmp);
         copy.setCount(1);
         return copy;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nonnull
   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORKS);
   }

   public boolean isDynamic() {
      return true;
   }

   @Nonnull
   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty() && this.hasExplosions(stack)) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            remaining.set(i, copy);
         }
      }

      return remaining;
   }

   private byte getFlight(ItemStack stack) {
      return !stack.hasTagCompound() ? 0 : ItemNBTHelper.getCompound(stack, "Fireworks", false).getByte("Flight");
   }

   private boolean hasExplosions(ItemStack stack) {
      return ItemNBTHelper.getCompound(stack, "Fireworks", false).hasKey("Explosions");
   }

   public boolean canFit(int x, int y) {
      return true;
   }

   @Nonnull
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> list = NonNullList.withSize(2, Ingredient.EMPTY);
      list.set(0, Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.FIREWORKS)}));
      list.set(1, Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.FIREWORKS)}));
      return list;
   }
}
