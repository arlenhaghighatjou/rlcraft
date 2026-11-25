package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;

public enum BannerClearRecipe implements ICauldronRecipe {
   INSTANCE;

   private BannerClearRecipe() {
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return state.isWater() && level > 0 && stack.getItem() == Items.BANNER && TileEntityBanner.getPatterns(stack) > 0;
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      ItemStack copy = stack.copy();
      copy.setCount(1);
      TileEntityBanner.removeBannerData(copy);
      return copy;
   }

   public int getLevel(int level) {
      return level - 1;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
