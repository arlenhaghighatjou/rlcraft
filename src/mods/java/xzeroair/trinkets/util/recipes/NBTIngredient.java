package xzeroair.trinkets.util.recipes;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import xzeroair.trinkets.Trinkets;

public class NBTIngredient extends Ingredient {
   private final ItemStack[] matchingStacks;
   private IntList matchingStacksPacked;

   protected NBTIngredient(ItemStack... stacks) {
      super(0);
      this.matchingStacks = stacks;
   }

   public ItemStack[] getMatchingStacks() {
      return this.matchingStacks;
   }

   public boolean apply(@Nullable ItemStack stackToCheck) {
      if (stackToCheck == null) {
         return false;
      } else {
         for(ItemStack stack : this.matchingStacks) {
            if (stack.getItem() == stackToCheck.getItem()) {
               int metadata = stack.getMetadata();
               if (metadata == 32767 || metadata == stackToCheck.getMetadata()) {
                  if (Trinkets.IceAndFire) {
                     boolean isSkull = stack.getItem().getRegistryName().toString().equalsIgnoreCase("iceandfire:dragon_skull") && stackToCheck.getItem().getRegistryName().toString().equalsIgnoreCase("iceandfire:dragon_skull");
                     if (isSkull) {
                        int skull1 = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Stage") : -1;
                        int skull2 = stackToCheck.hasTagCompound() ? stackToCheck.getTagCompound().getInteger("Stage") : -1;
                        boolean allowCraft = skull1 != -1 && skull2 != -1 && skull1 == skull2;
                        if (isSkull && allowCraft) {
                           return true;
                        }
                     }
                  }

                  if (ItemStack.areItemStackTagsEqual(stackToCheck, stack)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public IntList getValidItemStacksPacked() {
      if (this.matchingStacksPacked == null) {
         this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

         for(ItemStack itemstack : this.matchingStacks) {
            this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
         }

         this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.matchingStacksPacked;
   }

   protected void invalidate() {
      this.matchingStacksPacked = null;
   }

   public static Ingredient fromStacks(ItemStack... stacks) {
      if (stacks.length > 0) {
         for(ItemStack itemstack : stacks) {
            if (!itemstack.isEmpty()) {
               return new NBTIngredient(stacks);
            }
         }
      }

      return EMPTY;
   }

   public static class Factory implements IIngredientFactory {
      public Factory() {
      }

      public Ingredient parse(JsonContext context, JsonObject json) {
         ItemStack stack = CraftingHelper.getItemStack(json, context);
         return NBTIngredient.fromStacks(stack);
      }
   }
}
