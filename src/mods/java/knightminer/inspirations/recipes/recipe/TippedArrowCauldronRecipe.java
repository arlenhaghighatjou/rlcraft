package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public enum TippedArrowCauldronRecipe implements ICauldronRecipe {
   INSTANCE;

   private TippedArrowCauldronRecipe() {
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return state.getPotion() != null && stack.getItem() == Items.ARROW;
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      int size = Math.min(stack.getCount(), 8);
      return PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW, size), state.getPotion());
   }

   public ItemStack transformInput(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      stack.shrink(Math.min(stack.getCount(), 8));
      return stack;
   }

   public int getLevel(int level) {
      return level - 1;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
