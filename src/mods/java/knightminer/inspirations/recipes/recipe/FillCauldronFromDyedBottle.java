package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public enum FillCauldronFromDyedBottle implements ICauldronRecipe {
   INSTANCE;

   private FillCauldronFromDyedBottle() {
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level == InspirationsRegistry.getCauldronMax()) {
         return false;
      } else {
         return (state.isWater() || state.getColor() > -1) && stack.getItem() == InspirationsRecipes.dyedWaterBottle;
      }
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      int newColor = InspirationsRecipes.dyedWaterBottle.getColor(stack);
      if (level == 0) {
         return ICauldronRecipe.CauldronState.dye(newColor);
      } else {
         int color = state.getColor();
         if (newColor == color) {
            return state;
         } else {
            if (color == -1) {
               color = 8947848;
            }

            return ICauldronRecipe.CauldronState.dye(Util.combineColors(newColor, color, level));
         }
      }
   }

   public int getLevel(int level) {
      return level + 1;
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return stack.getItem().getContainerItem(stack);
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return SoundEvents.ITEM_BOTTLE_EMPTY;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
